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

import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.restlet.test.ext.jaxrs.services.tests.PrimitiveWrapperEntityTest;

/**
 * This resource is to test, what happens, if a primitive and a primitive
 * wrapper is required, also if nothing is given
 * 
 * @author Stephan Koops
 * @see PrimitiveWrapperEntityTest
 */
@Path("PrimitiveWrapperEntity")
@Produces("text/plain")
public class PrimitiveWrapperEntityResource {

    @PUT
    @Path("BooleanReturnboolean")
    public boolean BooleanReturnboolean(Boolean b) {
        if (b == null) {
            return false;
        }
        return b;
    }

    @PUT
    @Path("charReturnCharacter")
    public Character charReturnCharacter(char c) {
        return c;
    }

    @PUT
    @Path("integerReturnInteger")
    public Integer integerReturnInteger(Integer i) {
        return i;
    }

    @PUT
    @Path("intReturnInt")
    public int intReturnInt(int i) {
        return i;
    }

    @PUT
    @Path("byteArrayReturnByteArray")
    public byte[] byteArrayReturnByteArray(byte[] array) {
        return array;
    }
}
