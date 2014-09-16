package es.upv.grc.grcbox.server.multicastProxy;

public class TestMulticastProxy {

	public static void main(String[] args) {
		MulticastProxy mcProxy = new MulticastProxy(0, "eth1", "eth2", "192.168.1.1", "224.0.2.1", 4552);
		(new Thread(mcProxy)).start();
	}
}
