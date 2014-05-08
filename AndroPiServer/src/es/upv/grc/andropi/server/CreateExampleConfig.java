package es.upv.grc.andropi.server;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import es.upv.grc.andropi.common.AndroPiInterface.Type;
import es.upv.grc.andropi.server.AndroPiConfig.*;


public class CreateExampleConfig {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		AndroPiConfig config = new AndroPiConfig();
		ConfigInterface iface = new ConfigInterface();
		ConfigDatabase db = new ConfigDatabase();
		db.setPath("db/rules.db");
		db.setUpdateTime(5000);
		db.setFlushAtStartup(true);
		iface.setCost(1);
		iface.setName("eth2");
		iface.setType(Type.ETHERNET);
		config.setDatabase(db);
		List<ConfigInterface> inInterfaces = new ArrayList<>();
		inInterfaces.add(iface);
		config.setInInterfaces(inInterfaces);
		config.setDebug(true);
		config.setOutInterfaces(inInterfaces);
		
		ObjectMapper mapper = new ObjectMapper();
		String configFile = "/res/config.json";
		URL uri = CreateExampleConfig.class.getResource(configFile);
		File file = new File(uri.getPath());
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		try {
			mapper.writeValue(file, config);
			System.out.println(mapper.writeValueAsString(config));
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
