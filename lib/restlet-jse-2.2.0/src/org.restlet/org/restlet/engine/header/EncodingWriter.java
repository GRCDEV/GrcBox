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

package org.restlet.engine.header;

import java.util.List;

import org.restlet.data.Encoding;

/**
 * Encoding header writer.
 * 
 * @author Jerome Louvel
 */
public class EncodingWriter extends MetadataWriter<Encoding> {

    /**
     * Writes a list of encodings.
     * 
     * @param encodings
     *            The encodings to write.
     * @return This writer.
     */
    public static String write(List<Encoding> encodings) {
        return new EncodingWriter().append(encodings).toString();
    }

    @Override
    protected boolean canWrite(Encoding encoding) {
        return !encoding.equals(Encoding.IDENTITY);
    }
}
