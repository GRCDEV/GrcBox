package es.upv.grc.grcbox.server.multicastProxy.scampi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.upv.grc.grcbox.common.GrcBoxRule;
import es.upv.grc.grcbox.common.GrcBoxRule.Protocol;
import es.upv.grc.grcbox.common.GrcBoxRule.RuleType;
import es.upv.grc.grcbox.server.RulesDB;
import es.upv.grc.grcbox.server.multicastProxy.MulticastProxy;

/*
 * This plugin will process the payload of the 
 * UDPAdvertise by natting the source IP.
 * It will also automatically creates INCOMMING and
 * OUTGOING rules according to UDPAdvertise contents.
 */
public class ScampiProxy extends MulticastProxy {
	private String publicAddr;
	
	private ArrayList<Integer> rulesIds = new ArrayList<>();
	private HashSet<GrcBoxRule> rules = new HashSet<>();
	
	public ScampiProxy(int appId, String innerIface, String outerIface,
			String clientAddr, String subscribeAddr, int listenPort, String publicAddr) {
		super(appId, innerIface, outerIface, clientAddr, subscribeAddr, listenPort);
		this.publicAddr = publicAddr;
		LOG.info("Scampi Plugin initialized publicIP:" + publicAddr);
	}

	@Override
	protected byte[] processPayloadIncomming(byte[] payload) {
		ObjectMapper mapper = new ObjectMapper();
		UDPAdvertPOJO advert;
		try {
			advert = mapper.readValue(payload, UDPAdvertPOJO.class);
			/*
			 * Do not modify the packet just creates new rules to reach the destination
			 */
			LOG.info("Create New Rule");
			for (int i = 0; i < advert.cl_ports.length; i++) {
				if(advert.cl_types[i].equals("tcpcl")){
					GrcBoxRule rule = new GrcBoxRule(0, Protocol.TCP, RuleType.OUTGOING, getAppId(), getOuterIface(), 0, -1, advert.cl_ports[i], getClientAddr(), advert.ip, -1, null);
					RulesDB.addRule(getAppId(), rule);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return super.processPayloadIncomming(payload);
	}

	@Override
	protected byte[] processPayloadOutgoing(byte[] payload) {
		ObjectMapper mapper = new ObjectMapper();
		UDPAdvertPOJO advert;
		try {
			advert = mapper.readValue(payload, UDPAdvertPOJO.class);
			LOG.info("Replacing srcIp " + advert.ip + " with publicIP " + publicAddr );
			/*
			 * Create a rule per each CL
			 */
			for (int i = 0; i < advert.cl_ports.length; i++) {
				if(advert.cl_types[i].equals("tcpcl")){
					GrcBoxRule rule = new GrcBoxRule(0, Protocol.TCP, RuleType.INCOMING, getAppId(), getOuterIface(), 0, -1, advert.cl_ports[i], null, publicAddr, advert.cl_ports[i], getClientAddr());
					addRule(rule);
				}
			}
			/*
			 * Modify the UDPAdvert
			 */
			advert.ip = publicAddr;
			payload = mapper.writeValueAsBytes(advert);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return super.processPayloadOutgoing(payload);
	}

	@Override
	public void stop() {
		super.stop();
		for (Integer id : rulesIds) {
			RulesDB.rmRule(getAppId(), id);
		}
	}

	private void addRule(GrcBoxRule rule) {
		boolean ret = rules.add(rule);
		if(ret){
			rule = RulesDB.addRule(getAppId(), rule);
			rulesIds.add(rule.getId());
		}
	}
}
