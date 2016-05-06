#!/bin/sh
#
# Automatically start the GrcBoxServer
#
cd /root/GrcBox
java -jar GrcBoxServer.jar 1>/root/grcBoxServer.log  2>/root/grcBoxServer.err.log