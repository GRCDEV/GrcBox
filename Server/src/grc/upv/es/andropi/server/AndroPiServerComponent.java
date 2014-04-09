package grc.upv.es.andropi.server;

import org.restlet.Component;
import org.restlet.data.Protocol;

public class AndroPiServerComponent extends Component {
	public static void main(String[] args) throws Exception {
		new AndroPiServerComponent().start();
	}
	public AndroPiServerComponent(){
		setName("RESTful AndroPi Server");
		setDescription("Solution for android Ad-Hoc Communication");
		setOwner("GRC Valencia");
		setAuthor("Sergio Martínez Tornell");
		getServers().add(Protocol.HTTP, 8111);
		getDefaultHost().attachDefault(new AndroPiServer());
	}
}
