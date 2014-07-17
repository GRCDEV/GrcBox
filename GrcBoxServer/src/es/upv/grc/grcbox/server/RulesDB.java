package es.upv.grc.grcbox.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

import org.restlet.resource.ResourceException;
import org.restlet.security.MapVerifier;

import es.upv.grc.grcbox.common.GrcBoxApp;
import es.upv.grc.grcbox.common.GrcBoxAppInfo;
import es.upv.grc.grcbox.common.GrcBoxInterface;
import es.upv.grc.grcbox.common.GrcBoxRule;
import es.upv.grc.grcbox.common.GrcBoxRuleIn;
import es.upv.grc.grcbox.common.GrcBoxRuleOut;
import es.upv.grc.grcbox.server.networkInterfaces.NetworkInterfaceManager;
import es.upv.grc.grcbox.server.networkInterfaces.NetworkInterfaceManagerThreadNotRunning;
import es.upv.grc.grcbox.server.networkInterfaces.NetworkManagerNotRunning;
import es.upv.grc.grcbox.server.networkInterfaces.UnableToRunShellCommand;

public class RulesDB {
	private static volatile Integer _appId = 0;
	private static volatile Integer _ruleId = 0;
	private static volatile Map<Integer, GrcBoxApp> appMap = new HashMap<>();
	private static volatile Map<Integer, Map<Integer, GrcBoxRule>> rulesMap = new HashMap<>();
	private static volatile HashMap<String, Integer> nameIndex = new HashMap<>();
	private static volatile LinkedList<String> innerInterfaces;
	private static volatile LinkedList<String> outerInterfaces;
	private static volatile NetworkInterfaceManager nm;

	/*
	 * Initialise the rules managing system.
	 */
	public synchronized void initialize() throws NetworkManagerNotRunning, UnableToRunShellCommand{
		flushNatAndMangle();
		if(innerInterfaces.size() != 1){
			System.err.println("ERROR: CUrrently GRCBox supports only one inner iface");
			System.exit(-1);
		}
		nm = NetworkInterfaceManager.getObject();
		nm.start();
		IfaceMonitor ifaceMonitor = new IfaceMonitor(nm);
		List<GrcBoxInterface> outIfaces = null;
		try {
			outIfaces = getOuterInterfaces();
			List<GrcBoxInterface> ifaces = getAllInterfaces();
			for (GrcBoxInterface grcBoxInterface : ifaces) {
				if(innerInterfaces.contains(grcBoxInterface.getName()) && !outIfaces.contains(grcBoxInterface)){
					System.out.println("Ignoring iface "+ grcBoxInterface.getName() + " not listed as outer interface in config file.");
				}
			}
		} catch (NetworkInterfaceManagerThreadNotRunning e) {
			e.printStackTrace();
		}
		int index = 7;
		for (GrcBoxInterface grcBoxInterface : outIfaces) {
			nameIndex.put(grcBoxInterface.getName(), index++);
			initializeOutIface(grcBoxInterface);
		}
		ifaceMonitor.setNameIndexMap(nameIndex);
		nm.registerForUpdates(ifaceMonitor);
	}
	
	private synchronized void initializeOutIface(GrcBoxInterface iface){
		String ipnat = "iptables -t nat -A POSTROUTING -o " + iface.getName() + " -j MASQUERADE";
		String iprule = "ip rule add fwmark " + nameIndex.get(iface.getName()) + " table " + nameIndex.get(iface.getName());
		String iproute = "ip route add table "+ nameIndex.get(iface.getName()) + " default dev " + iface.getName(); 
		if(iface.getGatewayIp() != null){
				iproute += " via " + iface.getGatewayIp();
		}
		try {
			System.out.println("Activating NAT on iface " + iface.getName() +"\n"+ ipnat);
			System.out.println("Create routing table for Iface " + iface.getName() +"\n"+ iprule);
			System.out.println("Adding default routing rule for Iface "+ iface.getName()+"\n" + iproute );
			if(!GrcBoxServerApplication.getConfig().isDebug()){
				Runtime.getRuntime().exec(ipnat);
				Runtime.getRuntime().exec(iprule);
				Runtime.getRuntime().exec(iproute);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/*
	 * Flush all nat and masquerade rules from system.
	 */
	private void flushNatAndMangle(){
		String flushNat = "iptables -t nat -F";
		String flushMangle = "iptables -t mangle -F";
		
		try {
			System.out.println("Flushing nat and mangle rules \n" + flushNat + "\n"+ flushMangle);
	
			if(!GrcBoxServerApplication.getConfig().isDebug()){
				Runtime.getRuntime().exec(flushNat);
				Runtime.getRuntime().exec(flushMangle);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/*
	 * Returns the app maped to a certain ID or null
	 */
	public synchronized GrcBoxApp getApp(Integer appId){
		return appMap.get(appId);
	}
	
	/*
	 * Return a list of Apps registered in the system
	 */
	public synchronized List<GrcBoxApp> getApps() {
		return new LinkedList<>(appMap.values());
	}
	
	/*
	 * Returns the rule maped to a certain ID or null
	 */
	public synchronized GrcBoxRule getRule(Integer appId, Integer ruleId){
		return rulesMap.get(appId).get(ruleId);
	}
	
	public synchronized List<GrcBoxRule> getRulesByApp(Integer appId){
		if(appMap.containsKey(appId)){
			if(rulesMap.containsKey(appId)){
				return new LinkedList<GrcBoxRule>(rulesMap.get(appId).values());
			}
			else{
				return new LinkedList<GrcBoxRule>();
			}
		}
		else{
			return null;
		}
	}
	
	/*
	 * add a new App, the appId value is ignored, a new AndroPiApp is returned
	 */
	public synchronized int addApp(String name){
		int id = _appId++;
		GrcBoxApp app = new GrcBoxApp(id, name, System.currentTimeMillis());
		appMap.put(app.getAppId(), app);
		System.out.println(System.currentTimeMillis()+" An App was added to the DB, ID:"+ id);
		return id;
	}
	
	/*
	 * Remove an applications and its rules from the DB and the system
	 */
	public synchronized void rmApp(Integer appId){
		Map<Integer, GrcBoxRule> rules = rulesMap.get(appId);
		if(rules != null){
			Collection<GrcBoxRule> rulesList = rules.values();
			for (GrcBoxRule rule : rulesList) {
				rmRuleFromSystem(rule);
			}
			rulesMap.remove(appId);
		}
		appMap.remove(appId);
		MapVerifier verifier = GrcBoxServerApplication.getVerifier();
		verifier.getLocalSecrets().remove(Integer.toString(appId));
		System.out.println(System.currentTimeMillis()+" An App was removed from the DB, ID:"+ appId +" Applications Registered " + GrcBoxServerApplication.getVerifier().getLocalSecrets().size());
	}
	
	/*
	 * Add a new rule
	 * the ruleId is ignored, a new AndroPiRule object is returned
	 * 
	 */
	public synchronized GrcBoxRule addRule(Integer appId, GrcBoxRule rule){
		if(appMap.containsKey(appId)){
			rule.setId(_ruleId++);
			rule.setAppid(appId);
			if(!rulesMap.containsKey(appId)){
				rulesMap.put(appId, new HashMap<Integer, GrcBoxRule>());
			}
			rulesMap.get(appId).put(rule.getId(),rule);
			addRuleToSystem(rule);
			return rule;
		}
		else{
			return null;
		}
	}

	/*
	 * Remove a rule from DB and from System
	 */
	public synchronized void rmRule(Integer appId, Integer ruleId){
		if(rulesMap.containsKey(appId) && rulesMap.get(appId).containsKey(ruleId)){
			rmRuleFromSystem(rulesMap.get(appId).get(ruleId));
			rulesMap.get(appId).remove(ruleId);
		}
	}
	
	private synchronized void addRuleToSystem(GrcBoxRule rule) {
		/*
		 * TODO Dummy method
		 * It must check multicast rules based on IP and throw an exception until supported
		 */
		String ruleStr = "";
		ruleStr = newRuleToCommand(rule);
		System.out.println("A new rule is going to be excuted \n" + ruleStr);
		if(!GrcBoxServerApplication.getConfig().isDebug()){
			try {
				Process proc = Runtime.getRuntime().exec(ruleStr);
				BufferedReader stdInput = new BufferedReader(new 
			             InputStreamReader(proc.getInputStream()));
				String s = null;
		        while ((s = stdInput.readLine()) != null) {
		            System.out.println(s);
		        }
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private String newRuleToCommand(GrcBoxRule rule) {
		String ruleStr = "";
		if(rule instanceof GrcBoxRuleIn){
			ruleStr = "iptables -t nat -A PREROUTING -i " + rule.getIfName() + " -p " + rule.getProto().toString().toLowerCase();
			if(rule.getDstPort() == -1){
				throw new ResourceException(412);
			}
			ruleStr += " --dport " + rule.getDstPort();
			
			if(rule.getSrcPort() != -1)
				ruleStr += " --sport "+ rule.getSrcPort();
			
			if(rule.getSrcAddr() != null)
				ruleStr += " --s " + rule.getSrcAddr();
			
			if(rule.getDstFwdPort() == -1 || rule.getDstFwdAddr() == null){
				throw new ResourceException(412);
			}
			ruleStr += " -j DNAT --to-destination " +rule.getDstFwdAddr() + ":" + rule.getDstFwdPort();
		}
		else if(rule instanceof GrcBoxRuleOut ){
			ruleStr += "iptables -t mangle -A PREROUTING -i " + innerInterfaces.get(0) + " -p " + rule.getProto().toString().toLowerCase();

			if(rule.getDstPort() != -1 )
				ruleStr += " --dport " + rule.getDstPort();

			if(rule.getDstAddr() != null)
				ruleStr +=  " -d "+ rule.getDstAddr();

			if(rule.getSrcPort() != -1 )
				ruleStr += " --sport " + rule.getSrcPort();

			if(rule.getSrcAddr() != null)
				ruleStr += " -s " + rule.getSrcAddr();

			ruleStr += " -j MARK --set-mark "+ nameIndex.get(rule.getIfName());
			
		}
		return ruleStr;
}

	private synchronized void rmRuleFromSystem(GrcBoxRule rule){
		/*
		 * TODO Dummy method
		 */
		String ruleStr; 
		ruleStr = rmRuleToCommand(rule);
		System.out.println("A rule has been removed from System:\n"+ ruleStr);
		if(!GrcBoxServerApplication.getConfig().isDebug()){
			try {
				Process proc = Runtime.getRuntime().exec(ruleStr);
				BufferedReader stdInput = new BufferedReader(new 
						InputStreamReader(proc.getInputStream()));
				String s = null;
				while ((s = stdInput.readLine()) != null) {
					System.out.println(s);
				}			} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}


	private String rmRuleToCommand(GrcBoxRule rule) {
		String ruleStr = "";
		if(rule instanceof GrcBoxRuleIn){
			ruleStr = "iptables -t nat -D PREROUTING -i " + rule.getIfName() + " -p " + rule.getProto().toString().toLowerCase();
			if(rule.getDstPort() == -1){
				throw new ResourceException(412);
			}
			ruleStr += " --dport " + rule.getDstPort();
			
			if(rule.getSrcPort() != -1)
				ruleStr += " --sport "+ rule.getSrcPort();
			
			if(rule.getSrcAddr() != null){
				ruleStr += " --s " + rule.getSrcAddr();
			}
			ruleStr += " -j DNAT --to-destination " +rule.getDstFwdAddr() + ":" + rule.getDstFwdPort();
		}
		else if(rule instanceof GrcBoxRuleOut ){
			ruleStr += "iptables -t mangle -D PREROUTING -i " + innerInterfaces.get(0) + " -p " + rule.getProto().toString().toLowerCase();

			if(rule.getDstPort() != -1 )
				ruleStr += " --dport " + rule.getDstPort();

			if(rule.getDstAddr() != null)
				ruleStr +=  " -d "+ rule.getDstAddr();

			if(rule.getSrcPort() != -1 )
				ruleStr += " --sport " + rule.getSrcPort();

			if(rule.getSrcAddr() != null)
				ruleStr += " -s " + rule.getSrcAddr();
			
			ruleStr += " -j MARK --set-mark "+ nameIndex.get(rule.getIfName());
		}
		return ruleStr;
	}

	public synchronized GrcBoxAppInfo getAppInfo(int appId) {
		GrcBoxApp app = getApp(appId);
		return new GrcBoxAppInfo(appId, app.getName(), GrcBoxServerApplication.getConfig().getKeepAliveTime());
	}

	public synchronized void modifyApp(int appId, String name) {
		if(appMap.containsKey(appId)){
			appMap.get(appId).setName(name);
		}
	}
	
	/*
	 * KeepAliveApp: Update the timestamp of an App to prevent removing it.
	 */
	public synchronized void keepAliveApp(int appId) {
		appMap.get(appId).setLastKeepAlive(System.currentTimeMillis());
	}

	public synchronized List<GrcBoxInterface> getOuterInterfaces() throws NetworkInterfaceManagerThreadNotRunning {
		List<GrcBoxInterface> ifaces = getAllInterfaces();
		List<GrcBoxInterface> outIfaces = new LinkedList<>();
		for (GrcBoxInterface iface : ifaces) {
			if(!innerInterfaces.contains(iface.getName()) && outerInterfaces.contains(iface.getName())){
				outIfaces.add(iface);
			}
		}
		return outIfaces;
	}
	
	public synchronized List<GrcBoxInterface> getAllInterfaces() throws NetworkInterfaceManagerThreadNotRunning {
		return nm.getListOfAllInterfaces();
	}
	
	public synchronized void setInnerInterfaces(LinkedList<String> innerInterfaces) {
		RulesDB.innerInterfaces = innerInterfaces;
	}

	public synchronized void setOuterInterfaces(LinkedList<String> outerInterfaces) {
		RulesDB.outerInterfaces = outerInterfaces;
	}

	public List<GrcBoxRule> getAllRules() {
		List<GrcBoxRule> list = new LinkedList<GrcBoxRule>();
		for (Integer app : rulesMap.keySet()) {
			for (GrcBoxRule grcBoxRule : rulesMap.get(app).values()) {
				list.add(grcBoxRule);
			}
		}
		return list;
	}

	
}
