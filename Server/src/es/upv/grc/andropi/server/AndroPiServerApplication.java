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

package es.upv.grc.andropi.server;


import java.beans.Statement;
import java.io.File;
import java.net.URL;
import java.sql.*;

import org.restlet.Application;
import org.restlet.Component;
import org.restlet.Restlet;
import org.restlet.data.Protocol;
import org.restlet.routing.Router;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.upv.grc.andropi.server.db.DatabaseManager;

/**
 * Routing to annotated server resources.
 */
public class AndroPiServerApplication extends Application {

	private static final String configFile = "/res/config.json";
	static AndroPiConfig config;
	static DatabaseManager rulesDB;
	protected static DatabaseManager getRulesDb() {
		return rulesDB;
	}

	protected static AndroPiConfig getConfig() {
		return config;
	}

	protected static void setConfig(AndroPiConfig config) {
		AndroPiServerApplication.config = config;
	}

	/**
	 * Launches the application with an HTTP server.
	 * 
	 * @param args
	 *            The arguments.
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		//Load Config File
		URL uri = AndroPiServerApplication.class.getResource(configFile);
		File file = new File(uri.getPath());
		ObjectMapper mapper = new ObjectMapper();
		config = mapper.readValue(file, AndroPiConfig.class);

		//Connect to database and load previously stored rules
		// load the sqlite-JDBC driver using the current class loader
		Class.forName("org.sqlite.JDBC");

		// create a database connection
		rulesDB = new DatabaseManager(config.getDatabase().getUpdateTime());
		rulesDB.PrepareDb(config.getDatabase().getPath(), config.getDatabase().isFlushAtStartup());

		Component androPiServer = new Component(AndroPiServerApplication.class.getResource("/es/upv/grc/andropi/server/AndroPiComponent.xml").toString());
		androPiServer.start();
	}

	public static DatabaseManager getRulesDB() {
		return rulesDB;
	}





	public static String getConfigfile() {
		return configFile;
	}
	

	/**
	 * Creates a root Router to dispatch call to server resources.
	 */
	@Override
	public Restlet createInboundRoot() {
		Router router = new Router(getContext());
		router.attach("/",
				RootServerResource.class);
		return router;
	}
}
