/**
 * Copyright 2005-2014 Restlet
 * 
 * The contents of this file are subject to the terms of one of the following
 * open source licenses: Apache 2.0 or LGPL 3.0 or LGPL 2.1 or CDDL 1.0 or EPL
 * 1.0 (the "Licenses"). You can select the license that you prefer but you may
 * not use this file except in compliance with one of these Licenses.
 * 
 * You can obtain a copy of the Apache 2.0 license at
 * http://www.opensource.org/licenses/apache-2.0
 * 
 * You can obtain a copy of the LGPL 3.0 license at
 * http://www.opensource.org/licenses/lgpl-3.0
 * 
 * You can obtain a copy of the LGPL 2.1 license at
 * http://www.opensource.org/licenses/lgpl-2.1
 * 
 * You can obtain a copy of the CDDL 1.0 license at
 * http://www.opensource.org/licenses/cddl1
 * 
 * You can obtain a copy of the EPL 1.0 license at
 * http://www.opensource.org/licenses/eclipse-1.0
 * 
 * See the Licenses for the specific language governing permissions and
 * limitations under the Licenses.
 * 
 * Alternatively, you can obtain a royalty free commercial license with less
 * limitations, transferable or non-transferable, directly at
 * http://www.restlet.com/products/restlet-framework
 * 
 * Restlet is a registered trademark of Restlet
 */

package org.restlet.ext.oauth;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import org.json.JSONException;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.CacheDirective;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.CookieSetting;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.engine.util.Base64;
import org.restlet.ext.oauth.internal.Token;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.routing.Filter;

/**
 * A restlet filter for initiating a web server flow or comparable to OAuth 2.0
 * 3-legged authorization.
 * 
 * On successful execution a working OAuth token will be maintained. It is
 * recommended to put a ServerResource after this filter to display to the end
 * user on successful service setup.
 * 
 * The following example shows how to gain an access token that will be
 * available for "DummyResource" to use to access some remote protected resource
 * 
 * <pre>
 * {
 *     &#064;code
 *     OAuthProxy proxy = new OauthProxy(getContext(), true);
 *     proxy.setClientId(&quot;clientId&quot;);
 *     proxy.setClientSecret(&quot;clientSecret&quot;);
 *     proxy.setRedirectURI(&quot;callbackURI&quot;);
 *     proxy.setAuthorizationURI(&quot;authURI&quot;);
 *     proxy.setTokenURI(&quot;tokenURI&quot;);
 *     proxy.setNext(DummyResource.class);
 *     router.attach(&quot;/write&quot;, write);
 * }
 * </pre>
 * 
 * @author Kristoffer Gronowski
 * @author Shotaro Uchida <fantom@xmaker.mx>
 * @see org.restlet.ext.oauth.OAuthParameters
 */
public class OAuthProxy extends Filter implements OAuthResourceDefs {

    private final static List<CacheDirective> no = new ArrayList<CacheDirective>();

    private final static String VERSION = "RFC6749"; // Final spec.

    /**
     * Returns the current proxy's version.
     * 
     * @return The current proxy's version.
     */
    public static String getVersion() {
        return VERSION;
    }

    private final boolean basicSecret;

    private final org.restlet.Client cc;

    private final SecureRandom random;

    private String clientId;

    private String clientSecret;

    private String redirectURI;

    private String[] scope;

    private String authorizationURI;

    private String tokenURI;

    /**
     * Sets up an OauthProxy. Defaults to form based authentication and not http
     * basic.
     * 
     * @param ctx
     *            The Restlet context.
     */
    public OAuthProxy(Context ctx) {
        this(ctx, true); // Use BASIC method as default.
    }

    /**
     * Sets up an OAuthProxy.
     * 
     * @param useBasicSecret
     *            If true use http basic authentication otherwise use form
     *            based.
     * @param ctx
     *            The Restlet context.
     */
    public OAuthProxy(Context ctx, boolean useBasicSecret) {
        this(ctx, useBasicSecret, null);
    }

    /**
     * Sets up an OAuthProxy.
     * 
     * @param useBasicSecret
     *            If true use http basic authentication otherwise use form
     *            based.
     * @param ctx
     *            The Restlet context.
     * @param requestClient
     *            A predefined client that will be used for remote client
     *            request. Useful when you need to set e.g. SSL initialization
     *            parameters
     */
    public OAuthProxy(Context ctx, boolean useBasicSecret,
            org.restlet.Client requestClient) {
        this.basicSecret = useBasicSecret;
        setContext(ctx);
        no.add(CacheDirective.noStore());
        this.cc = requestClient;
        try {
            random = SecureRandom.getInstance("SHA1PRNG");
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException(ex);
        }
    }

    private String setupState(Response response) {
        String sessionId = UUID.randomUUID().toString();

        byte[] secret = new byte[20];
        random.nextBytes(secret);
        String state = Base64.encode(secret, false);

        CookieSetting cs = new CookieSetting("_state", sessionId);
        response.getCookieSettings().add(cs);

        getContext().getAttributes().put(sessionId, state);

        return state;
    }

    private void validateState(Request request, Form params) throws Exception {
        String sessionId = request.getCookies().getFirstValue("_state");
        String state = (String) getContext().getAttributes().get(sessionId);
        if (state != null && state.equals(params.getFirstValue(STATE))) {
            return;
        }
        // CSRF detected
        throw new Exception("The state does not match.");
    }

    protected OAuthParameters createAuthorizationRequest() {
        OAuthParameters parameters = new OAuthParameters().responseType(
                ResponseType.code).add(CLIENT_ID, getClientId());
        if (redirectURI != null) {
            parameters.redirectURI(redirectURI);
        }
        if (scope != null) {
            parameters.scope(scope);
        }
        return parameters;
    }

    protected OAuthParameters createTokenRequest(String code) {
        OAuthParameters parameters = new OAuthParameters().grantType(
                GrantType.authorization_code).code(code);
        if (redirectURI != null) {
            parameters.redirectURI(redirectURI);
        }
        return parameters;
    }

    protected Representation getErrorPage(Exception ex) {
        // Failed in initial auth resource request
        StringBuilder sb = new StringBuilder();
        sb.append("<html><body><pre>");
        if (ex instanceof OAuthException) {
            OAuthException oex = (OAuthException) ex;
            sb.append("OAuth2 error detected.\n");

            sb.append("Error : ").append(oex.getError());
            if (oex.getErrorDescription() != null) {
                sb.append("Error description : ").append(
                        oex.getErrorDescription());
            }

            if (oex.getErrorURI() != null) {
                sb.append("<a href=\"");
                sb.append(oex.getErrorURI());
                sb.append("\">Error Description</a>");
            }
        } else {
            sb.append("General error detected.\n");
            sb.append("Error : ").append(ex.getMessage());
        }

        sb.append("</pre></body></html>");

        return new StringRepresentation(sb.toString(), MediaType.TEXT_HTML);
    }

    private Token requestToken(String code) throws OAuthException, IOException,
            JSONException {
        getLogger().fine("Came back after authorization code = " + code);

        final AccessTokenClientResource tokenResource;
        String endpoint = getTokenURI();
        if (endpoint.contains("graph.facebook.com")) {
            // We should use Facebook implementation. (Old draft spec.)
            tokenResource = new FacebookAccessTokenClientResource(
                    new Reference(endpoint));
        } else {
            tokenResource = new AccessTokenClientResource(new Reference(
                    endpoint));
            tokenResource
                    .setAuthenticationMethod(basicSecret ? ChallengeScheme.HTTP_BASIC
                            : null);
        }
        tokenResource.setClientCredentials(getClientId(), getClientSecret());

        if (cc != null) {
            tokenResource.setNext(cc);
        }

        OAuthParameters tokenRequest = createTokenRequest(code);

        try {
            getLogger().fine("Sending access form : " + tokenRequest);
            return tokenResource.requestToken(tokenRequest);
        } finally {
            tokenResource.release();
        }
    }

    private int sendErrorPage(Response response, Exception ex) {
        response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST, ex.getMessage());
        response.setEntity(getErrorPage(ex));
        return STOP;
    }

    @Override
    protected int beforeHandle(Request request, Response response) {
        // Sets the no-store Cache-Control header
        request.setCacheDirectives(no);

        Form params = new Form(request.getOriginalRef().getQuery());
        getLogger().fine("Incomming request query = " + params);

        try {
            // Check if error is available.
            String error = params.getFirstValue(ERROR);
            if (error != null && !error.isEmpty()) {
                validateState(request, params); // CSRF protection
                return sendErrorPage(response,
                        OAuthException.toOAuthException(params));
            }
            // Check if code is available.
            String code = params.getFirstValue(CODE);
            if (code != null && !code.isEmpty()) {
                // Execute authorization_code grant
                validateState(request, params); // CSRF protection
                Token token = requestToken(code);
                request.getAttributes().put(Token.class.getName(), token);
                return CONTINUE;
            }
        } catch (Exception ex) {
            if (!(ex instanceof OAuthException)) {
                getLogger().log(Level.SEVERE, "OAuthProxy error", ex);
            }
            return sendErrorPage(response, ex);
        }

        // Redirect to authorization uri
        OAuthParameters authRequest = createAuthorizationRequest();
        authRequest.state(setupState(response)); // CSRF protection
        Reference redirRef = authRequest.toReference(getAuthorizationURI());
        getLogger().fine("Redirecting to : " + redirRef.toUri());
        response.setCacheDirectives(no);
        response.redirectTemporary(redirRef);
        getLogger().fine("After Redirecting to : " + redirRef.toUri());
        return STOP;
    }

    /**
     * @return the clientId
     */
    public String getClientId() {
        return clientId;
    }

    /**
     * @param clientId
     *            the clientId to set
     */
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    /**
     * @return the clientSecret
     */
    public String getClientSecret() {
        return clientSecret;
    }

    /**
     * @param clientSecret
     *            the clientSecret to set
     */
    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    /**
     * @return the redirectURI
     */
    public String getRedirectURI() {
        return redirectURI;
    }

    /**
     * @param redirectURI
     *            the redirectURI to set
     */
    public void setRedirectURI(String redirectURI) {
        this.redirectURI = redirectURI;
    }

    /**
     * @return the scope
     */
    public String[] getScope() {
        return scope;
    }

    /**
     * @param scope
     *            the scope to set
     */
    public void setScope(String[] scope) {
        this.scope = scope;
    }

    /**
     * @return the authorizationURI
     */
    public String getAuthorizationURI() {
        return authorizationURI;
    }

    /**
     * @param authorizationURI
     *            the authorizationURI to set
     */
    public void setAuthorizationURI(String authorizationURI) {
        this.authorizationURI = authorizationURI;
    }

    /**
     * @return the tokenURI
     */
    public String getTokenURI() {
        return tokenURI;
    }

    /**
     * @param tokenURI
     *            the tokenURI to set
     */
    public void setTokenURI(String tokenURI) {
        this.tokenURI = tokenURI;
    }

}
