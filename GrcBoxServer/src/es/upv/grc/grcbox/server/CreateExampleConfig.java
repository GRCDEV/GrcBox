package es.upv.grc.grcbox.server;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;


import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * The Class CreateExampleConfig.
 */
public class CreateExampleConfig {

	/**
	 * This class was used only once to generate a config json file 
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		GrcBoxConfig config = new GrcBoxConfig();
		config.setDebug(true);
		config.setKeepAliveTime( 10000);
		LinkedList<String> ifaces = new LinkedList<String>();
		ifaces.add("wlan0");
		config.setInnerInterfaces(ifaces);
		ifaces.clear();
		ifaces.add("wlan1");
		ifaces.add("wlan2");
		config.setOuterInterfaces(ifaces);
		ObjectMapper mapper = new ObjectMapper();
		String configFile = "/res/config.json";
		URL uri = CreateExampleConfig.class.getResource(configFile);
		File file = new File(uri.getPath());
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		try {
			mapper.writeValue(file, config);
			System.out.println(mapper.writeValueAsString(config));
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
