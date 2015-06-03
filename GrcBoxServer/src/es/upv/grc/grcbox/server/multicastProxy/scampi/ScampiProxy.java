package es.upv.grc.grcbox.server.multicastProxy.scampi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.upv.grc.grcbox.common.GrcBoxRule;
import es.upv.grc.grcbox.common.GrcBoxRule.Protocol;
import es.upv.grc.grcbox.common.GrcBoxRule.RuleType;
import es.upv.grc.grcbox.server.multicastProxy.MulticastProxy;
import es.upv.grc.grcbox.server.rulesdb.RulesDB;
/**
 * This plugin will process the payload of the 
 * UDPAdvertise by natting the source IP.
 * It will also automatically creates INCOMMING and
 * OUTGOING rules according to UDPAdvertise contents.
 */
public class ScampiProxy extends MulticastProxy {
	
	/** The public addr. */
	private String publicAddr;
	
	/** The rules ids. */
	private ArrayList<Integer> rulesIds = new ArrayList<>();
	
	/** The rules. */
	private HashSet<GrcBoxRule> rules = new HashSet<>();
	
	/**
	 * Instantiates a new scampi proxy.
	 *
	 * @param appId the app id
	 * @param innerIface the inner iface
	 * @param outerIface the outer iface
	 * @param clientAddr the client addr
	 * @param subscribeAddr the subscribe addr
	 * @param listenPort the listen port
	 * @param publicAddr the public addr
	 */
	public ScampiProxy(int appId, String innerIface, String outerIface,
			String clientAddr, String subscribeAddr, int listenPort, String publicAddr) {
		super(appId, innerIface, outerIface, clientAddr, subscribeAddr, listenPort);
		this.publicAddr = publicAddr;
		LOG.info("Scampi Plugin initialized publicIP:" + publicAddr);
	}

	/* (non-Javadoc)
	 * @see es.upv.grc.grcbox.server.multicastProxy.MulticastProxy#processPayloadIncomming(byte[])
	 */
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
					/*
					 * Do not create outgoing rule, it is one hop don't need it
					 */
					//GrcBoxRule rule = new GrcBoxRule(0, Protocol.TCP, RuleType.OUTGOING, getAppId(), getOuterIface(), 0, -1, advert.cl_ports[i], getClientAddr(), advert.ip, -1, null);
					//addRule(rule);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return super.processPayloadIncomming(payload);
	}

	/* (non-Javadoc)
	 * @see es.upv.grc.grcbox.server.multicastProxy.MulticastProxy#processPayloadOutgoing(byte[])
	 */
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

	/* (non-Javadoc)
	 * @see es.upv.grc.grcbox.server.multicastProxy.MulticastProxy#stop()
	 */
	@Override
	public void stop() {
		super.stop();
		for (Integer id : rulesIds) {
			RulesDB.rmRule(getAppId(), id);
		}
	}

	/**
	 * Adds the rule.
	 *
	 * @param rule the rule
	 */
	private void addRule(GrcBoxRule rule) {
		boolean ret = rules.add(rule);
		if(ret){
			List<GrcBoxRule> ruleList = RulesDB.addRule(getAppId(), rule); 
			rule = ruleList.get(ruleList.size()-1);
			rulesIds.add(rule.getId());
		}
	}
}
