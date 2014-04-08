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

package org.restlet.ext.sip.internal;

import java.util.List;

import org.restlet.engine.header.HeaderWriter;
import org.restlet.ext.sip.OptionTag;

/**
 * Option tag like header writer.
 * 
 * @author Thierry Boileau
 */
public class OptionTagWriter extends HeaderWriter<OptionTag> {

    /**
     * Writes a list of option tags.
     * 
     * @param optionTags
     *            The list of option tags.
     * @return The formatted list of option tags.
     */
    public static String write(List<OptionTag> optionTags) {
        return new OptionTagWriter().append(optionTags).toString();
    }

    /**
     * Writes an option tag.
     * 
     * @param optionTag
     *            The option tag.
     * @return The formatted option tag.
     */
    public static String write(OptionTag optionTag) {
        return new OptionTagWriter().append(optionTag).toString();
    }

    @Override
    public HeaderWriter<OptionTag> append(OptionTag value) {
        append(value.getTag());

        return this;
    }

}
