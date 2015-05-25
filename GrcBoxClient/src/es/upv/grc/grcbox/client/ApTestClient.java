package es.upv.grc.grcbox.client;

import org.restlet.engine.Engine;
import org.restlet.ext.jackson.JacksonConverter;
import org.restlet.resource.ClientResource;

import es.upv.grc.grcbox.common.ApAuth;
import es.upv.grc.grcbox.common.GrcBoxSsid;
import es.upv.grc.grcbox.common.resources.SsidResource;

public class ApTestClient {

	public static void main(String[] args) {
		Engine.getInstance().getRegisteredConverters().add(new JacksonConverter());
        ClientResource clientResource = new ClientResource("http://192.168.2.1:8080");
        SsidResource ssidResource = clientResource.getChild("/ifaces/wlan1/ssids/Lab-GRC", SsidResource.class);
        GrcBoxSsid ssid= ssidResource.retrieve();
        ApAuth apAuth = new ApAuth();
        apAuth.setAutoconnect(false);
        apAuth.setPassword("manzonijucanocalafate");
        ssidResource.connect(apAuth);
	}

}
