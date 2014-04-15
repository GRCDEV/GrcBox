package grc.upv.es.andropi.common;

import java.io.IOException;

import org.restlet.ext.xml.DomRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Put;

public interface AndroPiRootResource {
	@Get
	public DomRepresentation toXml() throws IOException;
	
	@Put
	public void store(DomRepresentation mailRep) throws IOException;
}
