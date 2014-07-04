package es.upv.grc.grcbox.server;

import java.io.IOException;
import java.util.*;

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
		int index = 0;
		for (GrcBoxInterface grcBoxInterface : outIfaces) {
			nameIndex.put(grcBoxInterface.getName(), index++);
			//TODO Uncomment
			//createIfaceTable(iface);
		}
		ifaceMonitor.setNameIndexMap(nameIndex);
		nm.registerForUpdates(ifaceMonitor);
	}
	
	private synchronized void createIfaceTable(GrcBoxInterface iface){
		String iprule = "ip rule add fwmark " + nameIndex.get(iface.getName()) + " table " + nameIndex.get(iface.getName());
		try {
			Runtime.getRuntime().exec(iprule);
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
			for (GrcBoxRule rule : rules.values()) {
				rmRuleFromSystem(rule);
				rules.remove(rule.getId());
			}
			if(rules.isEmpty()){
				rulesMap.remove(appId);
			}
		}
		appMap.remove(appId);
		System.out.println(System.currentTimeMillis()+" An App was removed from the DB, ID:"+ appId);
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
		int mark = nameIndex.get(rule.getIfName());
		if(rule.isIncomming()){
			ruleStr = new GrcBoxRuleIn(rule).createIptablesRule(mark);
		}
		else{
			ruleStr = new GrcBoxRuleOut(rule).createIptablesRule(mark);
		}
		//TODO excute command on shell
		System.out.println("A new rule is going to be excuted \n" + ruleStr);
	}
	
	private synchronized void rmRuleFromSystem(GrcBoxRule rule){
		/*
		 * TODO Dummy method
		 */
		String ruleStr;
		if(rule.isIncomming()){
			ruleStr = new GrcBoxRuleIn(rule).deleteIptablesRule();
		}
		else{
			ruleStr = new GrcBoxRuleOut(rule).deleteIptablesRule();
		}
		//TODO RUn a command shell with ruleStr
		System.out.println("A rule has been removed from System:\n"+ ruleStr);
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
