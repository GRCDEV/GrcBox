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

package org.restlet.test.ext.jaxrs.services.resources;

import java.io.IOException;
import java.sql.SQLException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.restlet.test.ext.jaxrs.services.tests.ThrowExceptionTest;

/**
 * @author Stephan Koops
 * @see ThrowExceptionTest
 */
@Path("throwExc")
public class ThrowExceptionResource {

    public static final int WEB_APP_EXC_STATUS = 583;

    @GET
    @Path("IOException")
    public String getIoe() throws IOException {
        throw new IOException("This exception is planned for testing !");
    }

    @GET
    @Path("sqlExc")
    public Object getSqlExc() throws SQLException {
        throw new SQLException();
    }

    @GET
    @Path("WebAppExc")
    public String getWebAppExc() throws WebApplicationException {
        throw new WebApplicationException(WEB_APP_EXC_STATUS);
    }

    @GET
    @Path("WebAppExcNullStatus")
    public String getWebAppExcNullStatus() throws WebApplicationException {
        throw new WebApplicationException((Response) null);
    }
}
