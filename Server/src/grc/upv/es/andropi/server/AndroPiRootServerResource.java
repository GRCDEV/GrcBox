package grc.upv.es.andropi.server;

import java.io.IOException;

import grc.upv.es.andropi.common.AndroPiRootResource;

import org.restlet.data.Reference;
import org.restlet.ext.xml.DomRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Put;
import org.restlet.resource.ServerResource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class AndroPiRootServerResource extends ServerResource  implements AndroPiRootResource {
	@Get
	public DomRepresentation toXml() throws IOException{
		DomRepresentation result = new DomRepresentation();
		result.setIndenting(true);
		Document doc = result.getDocument();
		Node mailElt = doc.createElement("mail");
		doc.appendChild(mailElt);

		Node statusElt = doc.createElement("status");
		statusElt.setTextContent("received");
		mailElt.appendChild(statusElt);

		Node subjectElt = doc.createElement("subject");
		subjectElt.setTextContent("Message to self");
		mailElt.appendChild(subjectElt);

		Node contentElt = doc.createElement("content");
		contentElt.setTextContent("Doh!");
		mailElt.appendChild(contentElt);

		Node accountRefElt = doc.createElement("accountRef");
		accountRefElt.setTextContent(new Reference(getReference(), "..")
		.getTargetRef().toString());
		mailElt.appendChild(accountRefElt);
		return result;
	}

	@Put
	public void store(DomRepresentation mailRep) throws IOException {
		Document doc = mailRep.getDocument();
		Element mailElt = doc.getDocumentElement();
		Element statusElt = (Element)
				mailElt.getElementsByTagName("status").item(0);
		mailElt.getElementsByTagName("status").item(0);
		Element subjectElt = (Element)
		mailElt.getElementsByTagName("subject").item(0);
		Element contentElt = (Element)
		mailElt.getElementsByTagName("content").item(0);
		Element accountRefElt = (Element) mailElt.getElementsByTagName(
		"accountRef").item(0);
		
		System.out.println("Status: " + statusElt.getTextContent());
		System.out.println("Subject: " + subjectElt.getTextContent());
		System.out.println("Content: " + contentElt.getTextContent());
		System.out.println("Account URI: " + accountRefElt.getTextContent());
	}
}
