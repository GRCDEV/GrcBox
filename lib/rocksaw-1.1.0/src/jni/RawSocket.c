/* Copyright 2004-2008 Daniel F. Savarese
 * Copyright 2009-2014 Savarese Software Research Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.savarese.com/software/ApacheLicense-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#include <errno.h>
#include <string.h>

#if defined(_WIN32)

#  include <winsock2.h>
#  include <ws2tcpip.h>

#  if !defined(close)
#    define close(fd) closesocket(fd)
#  endif

#else

#  include <netdb.h>
#  include <netinet/in.h>
#  include <sys/socket.h>
#  include <unistd.h>
#  include <sys/time.h>

#  ifdef ROCKSAW_USE_POLL

#    include <poll.h>

#  endif

#endif

#include "RawSocket.h"

/*
 * Utility functions.
 */

static int setintsockopt(int socket, int level, int option, int value);
static int getintsockopt(int socket, int level, int option);
static int settimeout(int socket, int option, int timeout);
static int gettimeout(int socket, int option);

static int setintsockopt(int socket, int level, int option, int value) {
  return setsockopt(socket, level, option, (void*)&value, sizeof(value));
}


static int getintsockopt(int socket, int level, int option) {
  int value  = -1;
  socklen_t size   = sizeof(value);
  int result = getsockopt(socket, level, option, (void*)&value, &size);

  if(result < 0) {
    return result;
  }

  return value;
}


static void milliseconds_to_timeval(int milliseconds, struct timeval *value) {
  int seconds = milliseconds / 1000;

  if(seconds > 0) {
    milliseconds-=(seconds*1000);
  }

  value->tv_sec  = seconds;
  value->tv_usec = milliseconds * 1000;
}


static int settimeout(int socket, int option, int timeout) {
#if defined(_WIN32)
  return setintsockopt(socket, SOL_SOCKET, option, timeout);
#else
  struct timeval value;

  milliseconds_to_timeval(timeout, &value);

  return setsockopt(socket, SOL_SOCKET, option, (void*)&value, sizeof(value));
#endif
}


static int gettimeout(int socket, int option) {
  int result;
  struct timeval value;
  socklen_t size = sizeof(value);

  result = getsockopt(socket, SOL_SOCKET, option, (void*)&value, &size);

  if(result < 0)
    return result;

  return (value.tv_sec * 1000 + value.tv_usec / 1000);
}


static struct sockaddr*
init_sockaddr_in(JNIEnv *env, struct sockaddr_in *sin, jbyteArray address) {
  jbyte *buf;

  memset(sin, 0, sizeof(struct sockaddr_in));
  sin->sin_family = PF_INET;
  buf = (*env)->GetByteArrayElements(env, address, NULL);
  memcpy(&sin->sin_addr, buf, sizeof(sin->sin_addr));
  (*env)->ReleaseByteArrayElements(env, address, buf, JNI_ABORT);
  return (struct sockaddr *)sin;
}


static struct sockaddr*
init_sockaddr_in6(JNIEnv *env, struct sockaddr_in6 *sin6, jbyteArray address,
                  int scope_id)
{
  jbyte *buf;

  memset(sin6, 0, sizeof(struct sockaddr_in6));
  sin6->sin6_family = PF_INET6;
  sin6->sin6_scope_id = scope_id;
  buf = (*env)->GetByteArrayElements(env, address, NULL);
  memcpy(&sin6->sin6_addr, buf, sizeof(sin6->sin6_addr));
  (*env)->ReleaseByteArrayElements(env, address, buf, JNI_ABORT);

  return (struct sockaddr *)sin6;
}

/*
 * Class:     com_savarese_rocksaw_net_RawSocket
 * Method:    __RockSawInit();
 * Signature: ()I
 */
JNIEXPORT jint JNICALL
Java_com_savarese_rocksaw_net_RawSocket__1_1RockSawStartup
(JNIEnv *env, jclass cls)
{
#if defined(_WIN32)
  WORD version = MAKEWORD(2, 0);
  WSADATA data;
  return (errno = WSAStartup(version, &data));
#else
  return 0;
#endif
}

/*
 * Class:     com_savarese_rocksaw_net_RawSocket
 * Method:    __RockSawShutdown();
 * Signature: ()V
 */
JNIEXPORT void JNICALL
Java_com_savarese_rocksaw_net_RawSocket__1_1RockSawShutdown
(JNIEnv *env, jclass cls)
{
#if defined(_WIN32)
  WSACleanup();
#endif
}

/*
 * Class:     com_savarese_rocksaw_net_RawSocket
 * Method:    __getErrorMessage
 * Signature: (Ljava/lang/StringBuffer;)V
 */
JNIEXPORT void JNICALL
Java_com_savarese_rocksaw_net_RawSocket__1_1getErrorMessage
(JNIEnv *env, jclass cls, jobject buffer)
{
  jclass sbc;
  jstring str;
  jmethodID mid;

  if(errno) {
    char *message = NULL;

#if defined(_WIN32)
    int formatted =
      FormatMessage(FORMAT_MESSAGE_ALLOCATE_BUFFER | 
                    FORMAT_MESSAGE_FROM_SYSTEM | 
                    FORMAT_MESSAGE_IGNORE_INSERTS,
                    NULL, errno,
                    MAKELANGID(LANG_NEUTRAL, SUBLANG_DEFAULT),
                    (LPTSTR) &message, 0, NULL );
    if(!formatted)
      message = strerror(errno);
#else
    message = strerror(errno);
#endif

    str = (*env)->NewStringUTF(env, message);

#if defined(_WIN32)
    if(formatted)
      LocalFree(message);
#endif

    sbc = (*env)->GetObjectClass(env, buffer);
    mid =
      (*env)->GetMethodID(env, sbc, "append",
                          "(Ljava/lang/String;)Ljava/lang/StringBuffer;");
    (*env)->CallObjectMethod(env, buffer, mid, str);
  }
}


/*
 * Class:     com_savarese_rocksaw_net_RawSocket
 * Method:    __select
 * Signature: (IZII)I
 *
 * Returns zero if the socket is ready for I/O.
 */
#ifndef ROCKSAW_USE_POLL
JNIEXPORT jint JNICALL
Java_com_savarese_rocksaw_net_RawSocket__1_1select
(JNIEnv *env, jclass cls,  jint socket, jboolean read, jint milliseconds)
{
  int result;

  struct timeval timeout;
  fd_set *rset = NULL, *wset = NULL, errset, fdset;

  FD_ZERO(&fdset);
  FD_ZERO(&errset);
  FD_SET(socket, &fdset);
  FD_SET(socket, &errset);
  
  milliseconds_to_timeval(milliseconds, &timeout);

  if(read)
    rset = &fdset;
  else
    wset = &fdset;

  result = select(socket + 1, rset, wset, &errset, &timeout);

  if(result >= 0) {
    if(FD_ISSET(socket, &errset))
      result = -1;
    else if(FD_ISSET(socket, &fdset))
      result = 0;
    else {
#  if defined(_WIN32)
      errno = WSAETIMEDOUT;
#  else
      errno = EAGAIN;
#  endif
      result = -1;
    }
  }

  return result;
}

#else

JNIEXPORT jint JNICALL
Java_com_savarese_rocksaw_net_RawSocket__1_1select
(JNIEnv *env, jclass cls, jint socket, jboolean read, jint milliseconds)
{
  int result;

  struct pollfd fds[1];
  int timeout = milliseconds;

  if(timeout == 0) {
    timeout = -1;
  }

  fds[0].fd = socket;
  fds[0].events = (read ? POLLIN : POLLOUT);
  fds[0].revents = 0;

  result = poll(fds, 1, timeout);

  if(result == 1) {
    result = 0;
  } else if(result == -1) {
    errno = EAGAIN;
  }

  return result;
}

#endif

/*
 * Class:     com_savarese_rocksaw_net_RawSocket
 * Method:    __PF_INET
 * Signature: ()I
 */
JNIEXPORT jint JNICALL
Java_com_savarese_rocksaw_net_RawSocket__1_1PF_1INET
(JNIEnv *env, jclass cls)
{
  return PF_INET;
}

/*
 * Class:     com_savarese_rocksaw_net_RawSocket
 * Method:    __PF_INET6
 * Signature: ()I
 */
JNIEXPORT jint JNICALL
Java_com_savarese_rocksaw_net_RawSocket__1_1PF_1INET6
(JNIEnv *env, jclass cls)
{
  return PF_INET6;
}

/*
 * Class:     com_savarese_rocksaw_net_RawSocket
 * Method:    __socket
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL
Java_com_savarese_rocksaw_net_RawSocket__1_1socket
(JNIEnv *env, jclass cls, jint family, jint protocol)
{
  return socket(family, SOCK_RAW, protocol);
}

/*
 * Class:     com_savarese_rocksaw_net_RawSocket
 * Method:    __bind
 * Signature: (II[B)I
 */
JNIEXPORT jint JNICALL
Java_com_savarese_rocksaw_net_RawSocket__1_1bind
(JNIEnv *env, jclass cls, jint socket, jint family, jbyteArray address,
 jint scope_id)
{
  struct sockaddr *saddr;
  socklen_t socklen;
  union {
    struct sockaddr_in sin;
    struct sockaddr_in6 sin6;
  } sin;

  if(family == PF_INET) {
    socklen = sizeof(sin.sin);
    saddr = init_sockaddr_in(env, &sin.sin, address);
  } else if(family == PF_INET6) {
    socklen = sizeof(sin.sin6);
    saddr = init_sockaddr_in6(env, &sin.sin6, address, scope_id);
  } else
    return -1;

  return bind(socket, saddr, socklen);
}

/*
 * Class:     com_savarese_rocksaw_net_RawSocket
 * Method:    __bindDevice
 * Signature: (ILjava/lang/String;)I
 */
JNIEXPORT jint JNICALL
Java_com_savarese_rocksaw_net_RawSocket__1_1bindDevice
(JNIEnv *env, jclass cls, jint socket, jstring device)
{
#if defined(SO_BINDTODEVICE)
  const char *utf = (*env)->GetStringUTFChars(env, device, NULL);
  int result = setsockopt(socket, SOL_SOCKET, SO_BINDTODEVICE,
                          (void*)utf, (*env)->GetStringLength(env, device));

  (*env)->ReleaseStringUTFChars(env, device, utf);

  return result;
#else
  return 1;
#endif
}

/*
 * Class:     com_savarese_rocksaw_net_RawSocket
 * Method:    getProtocolByName
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL
Java_com_savarese_rocksaw_net_RawSocket_getProtocolByName
(JNIEnv *env, jclass cls, jstring name)
{
  const char *utf        = (*env)->GetStringUTFChars(env, name, NULL);
  struct protoent *proto = getprotobyname(utf);

  (*env)->ReleaseStringUTFChars(env, name, utf);

  return proto->p_proto;
}


/*
 * Class:     com_savarese_rocksaw_net_RawSocket
 * Method:    __query_routing_interface
 * Signature: (II[B[B)I
 */
JNIEXPORT jint JNICALL
Java_com_savarese_rocksaw_net_RawSocket__1_1query_1routing_1interface
(JNIEnv *env, jclass cls, jint socket, jint family,
 jbyteArray destination, jbyteArray source)
{
  int result = 0;
#if defined(_WIN32)
  struct sockaddr_storage ifc;
  DWORD size;
  struct sockaddr *saddr;
  socklen_t socklen;
  union {
    struct sockaddr_in sin;
    struct sockaddr_in6 sin6;
  } sin;

  if(family == PF_INET) {
    socklen = sizeof(sin.sin);
    saddr = init_sockaddr_in(env, &sin.sin, destination);
  } else if(family == PF_INET6) {
    socklen = sizeof(sin.sin6);
    saddr = init_sockaddr_in6(env, &sin.sin6, destination, 0);
  } else
    return -1;

  result =
    WSAIoctl(socket, SIO_ROUTING_INTERFACE_QUERY, saddr, socklen,
             (struct sockaddr *)&ifc, sizeof(ifc), &size, NULL, NULL);

  if(result != SOCKET_ERROR) {
    jbyte *buf = (*env)->GetByteArrayElements(env, source, NULL);

    if(ifc.ss_family == PF_INET6) {
      memcpy(buf, &((struct sockaddr_in6*)&ifc)->sin6_addr,
             sizeof(struct in6_addr));
    } else {
      memcpy(buf, &((struct sockaddr_in*)&ifc)->sin_addr,
             sizeof(struct in_addr));
    }

    (*env)->ReleaseByteArrayElements(env, source, buf, 0);
  }

#endif
  return result;
}

/*
 * Class:     com_savarese_rocksaw_net_RawSocket
 * Method:    __close
 * Signature: (I)V
 */
JNIEXPORT jint JNICALL
Java_com_savarese_rocksaw_net_RawSocket__1_1close
(JNIEnv *env, jclass cls, jint socket)
{
  return close(socket);
}

/*
 * Class:     com_savarese_rocksaw_net_RawSocket
 * Method:    __recvfrom
 * Signature: (I[BIII)I
 */
JNIEXPORT jint JNICALL
Java_com_savarese_rocksaw_net_RawSocket__1_1recvfrom1
(JNIEnv *env, jclass cls, jint socket,
 jbyteArray data, jint offset, jint len, jint family)
{
  int result;
  jbyte *buf;

  if(family != PF_INET && family != PF_INET6) {
    errno = EINVAL;
    return errno;
  }

  buf = (*env)->GetByteArrayElements(env, data, NULL);

  result = recvfrom(socket, buf+offset, len, 0, NULL, NULL);

  (*env)->ReleaseByteArrayElements(env, data, buf, 0);

#if defined(_WIN32)
  if(result < 0)
    errno = WSAGetLastError();
#endif

  return result;
}

/*
 * Class:     com_savarese_rocksaw_net_RawSocket
 * Method:    __recvfrom
 * Signature: (I[BIII[B)I
 */
JNIEXPORT jint JNICALL
Java_com_savarese_rocksaw_net_RawSocket__1_1recvfrom2
(JNIEnv *env, jclass cls, jint socket,
 jbyteArray data, jint offset, jint len, jint family, jbyteArray address)
{
  int result;
  jbyte *buf;
  union {
    struct sockaddr_in sin;
    struct sockaddr_in6 sin6;
  } sin;
  struct sockaddr *saddr;
  void *addr;
  socklen_t socklen;
  size_t addrlen;
  
  if(family == PF_INET) {
    socklen = sizeof(sin.sin);
    addrlen = sizeof(sin.sin.sin_addr);
    memset(&sin, 0, sizeof(struct sockaddr_in));
    sin.sin.sin_family = PF_INET;
    saddr = (struct sockaddr *)&sin.sin;
    addr = &sin.sin.sin_addr;
  } else if(family == PF_INET6) {
    socklen = sizeof(sin.sin6);
    addrlen = sizeof(sin.sin6.sin6_addr);
    memset(&sin.sin6, 0, sizeof(struct sockaddr_in6));
    sin.sin6.sin6_family = PF_INET6;
    addr = &sin.sin6.sin6_addr;
    saddr = (struct sockaddr *)&sin.sin6;
  } else {
    errno = EINVAL;
    return errno;
  }

  buf = (*env)->GetByteArrayElements(env, data, NULL);

  result = recvfrom(socket, buf+offset, len, 0, saddr, &socklen);

  (*env)->ReleaseByteArrayElements(env, data, buf, 0);

  buf = (*env)->GetByteArrayElements(env, address, NULL);
  memcpy(buf, addr, addrlen);
  (*env)->ReleaseByteArrayElements(env, address, buf, 0);

#if defined(_WIN32)
  if(result < 0)
    errno = WSAGetLastError();
#endif

  return result;
}

/*
 * Class:     com_savarese_rocksaw_net_RawSocket
 * Method:    __sendto
 * Signature: (I[BIII[B)I
 */
JNIEXPORT jint JNICALL
Java_com_savarese_rocksaw_net_RawSocket__1_1sendto
(JNIEnv *env, jclass cls, jint socket,
 jbyteArray data, jint offset, jint len, jint family, jbyteArray address,
 jint scope_id)
{
  int result;
  jbyte *buf;
  union {
    struct sockaddr_in sin;
    struct sockaddr_in6 sin6;
  } sin;
  struct sockaddr *saddr;
  socklen_t socklen;

  if(family == PF_INET) {
    socklen = sizeof(sin.sin);
    saddr = init_sockaddr_in(env, &sin.sin, address);
  } else if(family == PF_INET6) {
    socklen = sizeof(sin.sin6);
    saddr = init_sockaddr_in6(env, &sin.sin6, address, scope_id);
  } else {
    errno = EINVAL;
    return errno;
  }

  buf = (*env)->GetByteArrayElements(env, data, NULL);

  result = sendto(socket, buf+offset, len, 0, saddr, socklen);

  (*env)->ReleaseByteArrayElements(env, data, buf, JNI_ABORT);

#if defined(_WIN32)
  if(result < 0)
    errno = WSAGetLastError();
#endif

  return result;
}


/*
 * Class:     com_savarese_rocksaw_net_RawSocket
 * Method:    __isErrorEAGAIN
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL
Java_com_savarese_rocksaw_net_RawSocket__1_1isErrorEAGAIN
(JNIEnv *env, jclass cls)
{
#if defined(_WIN32)
  return (errno == WSAETIMEDOUT);
#else
  return (errno == EAGAIN || errno == EWOULDBLOCK);
#endif
}


/*
 * Class:     com_savarese_rocksaw_net_RawSocket
 * Method:    __setIPHeaderInclude
 * Signature: (IZ)I
 */
JNIEXPORT jint JNICALL
Java_com_savarese_rocksaw_net_RawSocket__1_1setIPHeaderInclude
(JNIEnv *env, jclass cls, jint socket, jboolean on)
{
  return setintsockopt(socket, IPPROTO_IP, IP_HDRINCL, on);
}


/*
 * Class:     com_savarese_rocksaw_net_RawSocket
 * Method:    __getIPHeaderInclude
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL
Java_com_savarese_rocksaw_net_RawSocket__1_1getIPHeaderInclude
(JNIEnv *env, jclass cls, jint socket)
{
  return getintsockopt(socket, IPPROTO_IP, IP_HDRINCL);
}


/*
 * Class:     com_savarese_rocksaw_net_RawSocket
 * Method:    __setSendBufferSize
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL
Java_com_savarese_rocksaw_net_RawSocket__1_1setSendBufferSize
(JNIEnv *env, jclass cls, jint socket, jint size)
{
  return setintsockopt(socket, SOL_SOCKET, SO_SNDBUF, size);
}


/*
 * Class:     com_savarese_rocksaw_net_RawSocket
 * Method:    __getSendBufferSize
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL
Java_com_savarese_rocksaw_net_RawSocket__1_1getSendBufferSize
(JNIEnv *env, jclass cls, jint socket)
{
  return getintsockopt(socket, SOL_SOCKET, SO_SNDBUF);
}


/*
 * Class:     com_savarese_rocksaw_net_RawSocket
 * Method:    __setReceiveBufferSize
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL
Java_com_savarese_rocksaw_net_RawSocket__1_1setReceiveBufferSize
(JNIEnv *env, jclass cls, jint socket, jint size)
{
  return setintsockopt(socket, SOL_SOCKET, SO_RCVBUF, size);
}


/*
 * Class:     com_savarese_rocksaw_net_RawSocket
 * Method:    __getReceiveBufferSize
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL
Java_com_savarese_rocksaw_net_RawSocket__1_1getReceiveBufferSize
(JNIEnv *env, jclass cls, jint socket)
{
  return getintsockopt(socket, SOL_SOCKET, SO_RCVBUF);
}


/*
 * Class:     com_savarese_rocksaw_net_RawSocket
 * Method:    __setSendTimeout
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL
Java_com_savarese_rocksaw_net_RawSocket__1_1setSendTimeout
(JNIEnv *env, jclass cls, jint socket, jint timeout)
{
  return settimeout(socket, SO_SNDTIMEO, timeout);
}


/*
 * Class:     com_savarese_rocksaw_net_RawSocket
 * Method:    __getSendTimeout
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL
Java_com_savarese_rocksaw_net_RawSocket__1_1getSendTimeout
(JNIEnv *env, jclass cls, jint socket)
{
  return gettimeout(socket, SO_SNDTIMEO);
}


/*
 * Class:     com_savarese_rocksaw_net_RawSocket
 * Method:    __setReceiveTimeout
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL
Java_com_savarese_rocksaw_net_RawSocket__1_1setReceiveTimeout
(JNIEnv *env, jclass cls, jint socket, jint timeout)
{
  return settimeout(socket, SO_RCVTIMEO, timeout);
}


/*
 * Class:     com_savarese_rocksaw_net_RawSocket
 * Method:    __getReceiveTimeout
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL
Java_com_savarese_rocksaw_net_RawSocket__1_1getReceiveTimeout
(JNIEnv *env, jclass cls, jint socket)
{
  return gettimeout(socket, SO_RCVTIMEO);
}
