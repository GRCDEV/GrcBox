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

package org.restlet.example.ext.jaxrs;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 * This demonstrates an very easy JAX-RS resource. It is available on relative
 * URI "easy" (see annotation &#64;{@link Path} on this class). This resource
 * has representations of two media types: "text/html" ({@link #getHtml()} and
 * "text/plain").
 * 
 * @author Stephan Koops
 * @see ExampleApplication
 */
@Path("easy")
public class EasyRootResource {

    /**
     * Returns a HTML representation of this resource.
     * 
     * @return a HTML representation of this resource.
     */
    @GET
    @Produces("text/html")
    public String getHtml() {
        return "<html><head></head><body>\n"
                + "This is an easy resource (as html text).\n"
                + "</body></html>";
    }

    /**
     * Returns a plain text representation of this resource.
     * 
     * @return a plain text representation of this resource.
     */
    @GET
    @Produces("text/plain")
    public String getPlain() {
        return "This is an easy resource (as plain text)";
    }
}
