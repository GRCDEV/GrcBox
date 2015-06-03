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

package es.upv.grc.grcbox.common.resources;

import org.restlet.resource.*;

import es.upv.grc.grcbox.common.*;

/**
 * This resource defines available methods related to rules
 */
public interface RulesResource {

	/**
	 * return a list of rules associated to an app
	 *
	 * @return the list
	 */
	@Get("json")
    public GrcBoxRuleList getList();
    
    /*
     * 
     */
    /**
     * Create a new rule associated to this app
	 * Returns a list of the previously defined rules that are
	 * included in the given rule and may interfere with it and
	 * the defined rule as last element.
     *
     * @param rule the rule
     * @return the grc box rule list
     */
    @Post
    public GrcBoxRuleList newRule(GrcBoxRule rule);
}
