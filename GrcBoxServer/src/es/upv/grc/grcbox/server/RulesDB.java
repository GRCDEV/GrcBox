package es.upv.grc.grcbox.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.freedesktop.dbus.exceptions.DBusException;
import org.restlet.resource.ResourceException;
import org.restlet.security.MapVerifier;

import es.upv.grc.grcbox.common.GrcBoxApp;
import es.upv.grc.grcbox.common.GrcBoxAppInfo;
import es.upv.grc.grcbox.common.GrcBoxInterface;
import es.upv.grc.grcbox.common.GrcBoxRule;
import es.upv.grc.grcbox.common.GrcBoxRuleList;
import es.upv.grc.grcbox.common.GrcBoxRule.Protocol;
import es.upv.grc.grcbox.common.GrcBoxRule.RuleType;
import es.upv.grc.grcbox.server.multicastProxy.MulticastProxy;
import es.upv.grc.grcbox.server.multicastProxy.MulticastSupportedPlugins;
import es.upv.grc.grcbox.server.multicastProxy.scampi.ScampiProxy;
import es.upv.grc.grcbox.server.networkInterfaces.NetworkInterfaceManager;
import es.upv.grc.grcbox.server.networkInterfaces.NetworkManagerListener;


public class RulesDB {
	private static final Logger LOG = Logger.getLogger(NetworkInterfaceManager.class.getName()); 

	private static volatile Integer _appId = 0;
	private static volatile Integer _ruleId = 0;
	private static int 	tableId = 7;
	private static volatile Map<Integer, GrcBoxApp> appMap = new HashMap<>();
	/*
	 * App, ruleId, Rule
	 * This map may contain not active rules
	 */
	private static volatile Map<Integer, Map<Integer, GrcBoxRule>> rulesMap = new HashMap<>();
	/*
	 * ifaceName, index
	 */
	private static volatile HashMap<String, Integer> nameIndex = new HashMap<>();
	/*
	 * ruleId, Proxy
	 */
	private static volatile HashMap<Integer, MulticastProxy> proxies = new HashMap<>();
	/*
	 * Only contains active rules
	 */
	private static volatile RulesSortedList activeRules = new RulesSortedList();
	private static volatile LinkedList<String> innerInterfaces;
	private static volatile LinkedList<String> natLines = new LinkedList<String>();
	private static volatile IpTablesManager ipTables = new IpTablesManager();
	private static volatile NetworkInterfaceManager nm;

	private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

	
	final static Runnable dbMonitor = new Runnable() {
		@Override
		public  void run() {
			long timeout = GrcBoxServerApplication.getConfig().getKeepAliveTime();
			long now = System.currentTimeMillis();
			List<GrcBoxApp> appList = getApps();
			for (GrcBoxApp grcBoxApp : appList) {
				long diff = now - grcBoxApp.getLastKeepAlive();
				if(diff > timeout ){
					LOG.info("Removing APP "+grcBoxApp.getAppId() + " " + diff + " ms old.");
					rmApp(grcBoxApp.getAppId());
				}
//				else{
					//TODO Currently the expire property is ignored.
//					List<GrcBoxRule> rules = db.getRulesByApp(androPiApp.getAppId());
//					for (GrcBoxRule rule : rules) {
//						if(rule.getExpire() < now){
//							db.rmRule(androPiApp.getAppId(), rule.getId());
//						}
//					}
//				}
			}
		}
	};
	
	private static class IfaceManager implements NetworkManagerListener{

		@Override
		public void interfaceRemoved(GrcBoxInterface iface) {
			removeOutIface(iface);
			LOG.info("Interface has been removed " + iface.getName());
		}

		@Override
		public void interfaceAdded(GrcBoxInterface iface) {
			if(iface.isUp()){
				initializeOutIface(iface);
			}
			LOG.info("Interface has been added " + iface.getName());
		}

		@Override
		public void interfaceChanged(GrcBoxInterface iface) {
			if(iface.isUp()){
				removeOutIface(iface);
				initializeOutIface(iface);
			}
			else{
				removeOutIface(iface);
			}
			
			LOG.info("Interface has changed " + iface.getName());
		}
	}
	
	
	/*
	 * Initialize the rules managing system.
	 */
	public static synchronized void initialize(){
		if(innerInterfaces.size() != 1){
			LOG.severe("ERROR: CUrrently GRCBox supports only one inner iface");
			System.exit(-1);
		}
		try {
			ipTables = new IpTablesManager();
			ipTables.initialise();
			ipTables.flushAll();
			nm = new NetworkInterfaceManager();
			nm.initialize();
			nm.subscribeInterfaces(new IfaceManager());
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		Collection<GrcBoxInterface> interfaces = getOutInterfaces();
		for (GrcBoxInterface grcBoxInterface : interfaces) {
			if(grcBoxInterface.isUp()){
				initializeOutIface(grcBoxInterface);
			}
		}
		
		long time = GrcBoxServerApplication.getConfig().getKeepAliveTime();
		final ScheduledFuture<?> monitorHandle = scheduler.scheduleAtFixedRate(dbMonitor, time, time, TimeUnit.MILLISECONDS);
	}
	
	private synchronized static void initializeOutIface(GrcBoxInterface iface){
		if(!nameIndex.containsKey(iface.getName())){
			nameIndex.put(iface.getName(), tableId++);
		}
			
		String ipnat = "-A POSTROUTING -o " + iface.getName() + " -j MASQUERADE";
		String iprule = "ip rule add fwmark " + nameIndex.get(iface.getName()) + " table " + nameIndex.get(iface.getName());
		String iproute = "ip route add table "+ nameIndex.get(iface.getName()) + " default dev " + iface.getName(); 
		
		String gateway = nm.getGateway(iface.getName());
		
		if(gateway != null){
				iproute += " via " + gateway;
		}
		
		try {
			LOG.info("Create routing table for Iface " + iface.getName() +"\n"+ iprule);
			LOG.info("Adding default routing rule for Iface  "+ iface.getName()+"\n" + iproute );
			Process proc;
			if(!GrcBoxServerApplication.getConfig().isDebug()){
				natLines.add(ipnat);
				//ipTables.commitNatRule(ipnat);
				proc = Runtime.getRuntime().exec(iprule);
				proc.waitFor();
				proc = Runtime.getRuntime().exec(iproute);
				proc.waitFor();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*
		 * First, . 
		 */
		for (Integer client: rulesMap.keySet()) {
			@SuppressWarnings("unchecked")
			Set<Integer> rules = new HashSet<Integer>(rulesMap.get(client).keySet());
			LOG.info("Removing" + rules.size() +" in "+ iface.getName() + " from system");
			for(Integer ruleId: rules){
				GrcBoxRule rule= rulesMap.get(client).get(ruleId);
				LOG.info("Reactivate rule" +rule.getId()+ " in "+ iface.getName() + " to system");
				activeRules.add(rule);
			}
		}
		
		updateRules();
	}
	
	private synchronized static void removeOutIface(GrcBoxInterface iface){
		/*
		 * First, remove all the rules from the system. They will be restored in initializeOutIface
		 */
		for (Integer client: rulesMap.keySet()) {
			@SuppressWarnings("unchecked")
			Set<Integer> rules = new HashSet<Integer>(rulesMap.get(client).keySet());
			LOG.info("Removing" + rules.size() +" in "+ iface.getName() + " from system");
			for(Integer ruleId: rules){
				GrcBoxRule rule= rulesMap.get(client).get(ruleId);
				if(rule != null){
					if(rule.getIfName().equals(iface.getName()) && activeRules.contains(rule)){
						LOG.info("Rule " + rule + "removed");
						rmRuleFromSystem(rule);
					}
				}
			}
		}
		
		/*
		 * Remove iprule
		 */
		String natDel = "-D POSTROUTING -o " + iface.getName() + " -j MASQUERADE";
		String natAdd = "-A POSTROUTING -o " + iface.getName() + " -j MASQUERADE";
		String rmRoute = "ip route del default table " + nameIndex.get(iface.getName());
		String ipruleDel = "ip rule del fwmark " + nameIndex.get(iface.getName());
		
		try {
			LOG.info("Remove nat rule from iptables \n" + natDel );
			LOG.info("Remove route from routing table \n" + rmRoute);
			LOG.info("Remove routing table for Iface " + iface.getName() +"\n"+ ipruleDel);

			if(!GrcBoxServerApplication.getConfig().isDebug()){
				Process proc;
				if(natLines.remove(natAdd)){
					ipTables.commitNatRule(natDel);
				}
				proc = Runtime.getRuntime().exec(rmRoute);
				proc.waitFor();
				proc = Runtime.getRuntime().exec(ipruleDel);
				proc.waitFor();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	/*
	 * Returns the app mapped to a certain ID or null
	 */
	public synchronized static GrcBoxApp getApp(Integer appId){
		return appMap.get(appId);
	}
	
	/*
	 * Return a list of Apps registered in the system
	 */
	public synchronized static List<GrcBoxApp> getApps() {
		return new LinkedList<>(appMap.values());
	}
	
	/*
	 * Returns the rule maped to a certain ID or null
	 */
	public synchronized static GrcBoxRule getRule(Integer appId, Integer ruleId){
		return rulesMap.get(appId).get(ruleId);
	}
	
	public synchronized static List<GrcBoxRule> getRulesByApp(Integer appId){
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
	public synchronized static int addApp(String name){
		int id = _appId++;
		GrcBoxApp app = new GrcBoxApp(id, name, System.currentTimeMillis());
		appMap.put(app.getAppId(), app);
		LOG.info("An App was added to the DB, ID:"+ id);
		return id;
	}
	
	/*
	 * Remove an applications and its rules from the DB and the system
	 */
	public synchronized static void rmApp(Integer appId){
		Collection<GrcBoxRule> rules = getRulesByApp(appId);
		for (GrcBoxRule grcBoxRule : rules) {
			rmRule(appId, grcBoxRule.getId());
		}
		appMap.remove(appId);
		MapVerifier verifier = GrcBoxServerApplication.getVerifier();
		verifier.getLocalSecrets().remove(Integer.toString(appId));
		LOG.info("An App was removed from the DB, ID:"+ appId +" Applications Registered " + GrcBoxServerApplication.getVerifier().getLocalSecrets().size());
	}
	
	/*
	 * Add a new rule, the ruleId is ignored
	 * Returns a list of the previously defined rules that are
	 * included in the given rule and may interfere with it and the defined
	 * rule as the last one of the list
	 */
	public synchronized static List<GrcBoxRule> addRule(Integer appId, GrcBoxRule rule){
		if(appMap.containsKey(appId)){
			rule.setId(_ruleId++);
			rule.setAppid(appId);
			
			/*
			 * Check if rule conflicts with any other active rule
			 */
			for (GrcBoxRule oldRule : activeRules.getSet()) {
				if(rule.conflicts(oldRule)){
					throw new ResourceException(409);
				}
			}
			
			/*
			 * If it does not conflict, it can be inserted
			 */
			if(!rulesMap.containsKey(appId)){
				rulesMap.put(appId, new HashMap<Integer, GrcBoxRule>());
			}
			rulesMap.get(appId).put(rule.getId(),rule);
			activeRules.add(rule);
			/*
			 * Get the set of preceding rules to check if
			 * any of them may interfere with this rule 
			 */
			SortedSet<GrcBoxRule> sortedList = activeRules.subSet(activeRules.first(), rule);
			List<GrcBoxRule> includedRules = new ArrayList<GrcBoxRule>();
			for (GrcBoxRule grcBoxRule : sortedList) {
				if(rule.includes(grcBoxRule)){
					includedRules.add(grcBoxRule);
				}
			}
			/*
			 * TODO migrate to updateRules
			 */
			if(rule.getType().equals(RuleType.MULTICAST)){
				startMulticastProxy(rule);
			}
			else{
				updateRules();
			}
			/*
			 * Add the rule itself to the list
			 */
			includedRules.add(rule);
			/*
			 * List of preceding included rules
			 */
			return includedRules;
		}
		else{
			return null;
		}
	}

	/*
	 * Remove a rule from DB and from System
	 */
	public synchronized static void rmRule(Integer appId, Integer ruleId){
		if(rulesMap.containsKey(appId) && rulesMap.get(appId).containsKey(ruleId)){
			rmRuleFromSystem(rulesMap.get(appId).get(ruleId));
			rulesMap.get(appId).remove(ruleId);
		}
	}
	
	private synchronized static void startMulticastProxy(GrcBoxRule rule){
		InetAddress dstAddr;
		try {
			dstAddr = InetAddress.getByName(rule.getDstAddr());
		} catch (UnknownHostException e) {
			e.printStackTrace();
			throw new ResourceException(409, e);
		} 			
		if(rule.getProto() != Protocol.UDP || !dstAddr.isMulticastAddress()){
			throw new ResourceException(409);
		}
		MulticastProxy proxy = null;
		if(rule.getMcastPlugin().equals(MulticastSupportedPlugins.SCAMPI.toString())){
			proxy = new ScampiProxy(rule.getAppid(), 
					innerInterfaces.get(0), 
					rule.getIfName(), 
					rule.getSrcAddr(), 
					rule.getDstAddr(), 
					rule.getDstPort(),
					nm.getIpAddress(rule.getIfName())
					);
		}
		else if(rule.getMcastPlugin().equals(MulticastSupportedPlugins.NONE.toString())){
			proxy = new MulticastProxy(rule.getAppid(), 
					innerInterfaces.get(0), 
					rule.getIfName(), 
					rule.getSrcAddr(), 
					rule.getDstAddr(), 
					rule.getDstPort()
					);
		}
		else{
			throw new ResourceException(400);
		}
		
		if(proxy != null){
			Thread proxyThread = new Thread(proxy);
			proxyThread.setName("MulticastProxy" + rule.getId());
			proxyThread.start();
			proxies.put(rule.getId(), proxy);
		}
	}
	
	private synchronized static void stopMulticastProxy(GrcBoxRule rule){
		InetAddress dstAddr;
		try {
			dstAddr = InetAddress.getByName(rule.getDstAddr());
		} catch (UnknownHostException e) {
			e.printStackTrace();
			throw new ResourceException(409, e);
		} 			
		if(rule.getProto() != Protocol.UDP || !dstAddr.isMulticastAddress()){
			throw new ResourceException(409);
		}
		MulticastProxy proxy = proxies.get(rule.getId());
		proxy.stop();
		proxies.remove(rule.getId());
		LOG.info("A multicast proxy was stopped");
	}
	
	private synchronized static void updateRules(){
		/*
		 * activeRules are assumed to be sorted
		 * Outgoing rules go to mangle table
		 * Incoming rules go to nat table
		 */
		List<GrcBoxRule> list = activeRules.getSortedList();
		LinkedList<String> natRules = new LinkedList<String>();
		LinkedList<String> mangleRules = new LinkedList<String>();
		natRules.add("*nat");
		mangleRules.add("*mangle");
		for (GrcBoxRule rule : list) {
			if(rule.getType().equals(GrcBoxRule.RuleType.INCOMING)){
				String ruleStr = "";
				ruleStr =  "-A PREROUTING -i " 
						+ rule.getIfName() + " -p " 
						+ rule.getProto().toString().toLowerCase();
				
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
				ruleStr += " -j DNAT --to-destination " 
						+ rule.getDstFwdAddr() + ":" 
						+ rule.getDstFwdPort();
				natRules.add(ruleStr);
			}
			else if(rule.getType().equals(GrcBoxRule.RuleType.OUTGOING)){
				String ruleStr = "";
				ruleStr += "-A PREROUTING -i "
						+ innerInterfaces.get(0) + " -p " 
						+ rule.getProto().toString().toLowerCase();

				if(rule.getDstPort() != -1 )
					ruleStr += " --dport " + rule.getDstPort();

				if(rule.getDstAddr() != null)
					ruleStr +=  " -d "+ rule.getDstAddr();

				if(rule.getSrcPort() != -1 )
					ruleStr += " --sport " + rule.getSrcPort();

				if(rule.getSrcAddr() != null)
					ruleStr += " -s " + rule.getSrcAddr();

				Integer mark = nameIndex.get(rule.getIfName());
				if(mark == null){
					throw new ResourceException(412);
				}
				ruleStr += " -j MARK --set-mark " + mark;
				mangleRules.add(ruleStr);
			}
		}
		/*
		 * Commit
		 */
		mangleRules.add(IpTablesManager.COMMIT);
		natRules.add(IpTablesManager.COMMIT);
		ipTables.commitLines(mangleRules, true);
		ipTables.commitLines(natRules, true);
		/*
		 * Commit natLines without flushing
		 */
		for (String line : natLines) {
			ipTables.commitNatRule(line);
		}
	}
	
	/*
	 * TODO migrate to iptables-restore
	 */
	private synchronized static  void rmRuleFromSystem(GrcBoxRule rule){
		if(rule.getType().equals(RuleType.MULTICAST)){
			stopMulticastProxy(rule);
		}
		else if(rule.getType().equals(RuleType.INCOMING)){
			String ruleStr = "-D PREROUTING -i " + rule.getIfName() + " -p " 
					+ rule.getProto().toString().toLowerCase();
			if(rule.getDstPort() == -1){
				throw new ResourceException(412);
			}
			ruleStr += " --dport " + rule.getDstPort();
			
			if(rule.getSrcPort() != -1)
				ruleStr += " --sport "+ rule.getSrcPort();
			
			if(rule.getSrcAddr() != null){
				ruleStr += " --s " + rule.getSrcAddr();
			}
			ruleStr += " -j DNAT --to-destination " +rule.getDstFwdAddr() + ":" 
					+ rule.getDstFwdPort();
			ipTables.commitNatRule(ruleStr);
		}
		else{
			String ruleStr = "-D PREROUTING -i " + innerInterfaces.get(0) + " -p " 
					+ rule.getProto().toString().toLowerCase();

			if(rule.getDstPort() != -1 )
				ruleStr += " --dport " + rule.getDstPort();

			if(rule.getDstAddr() != null)
				ruleStr +=  " -d "+ rule.getDstAddr();

			if(rule.getSrcPort() != -1 )
				ruleStr += " --sport " + rule.getSrcPort();

			if(rule.getSrcAddr() != null)
				ruleStr += " -s " + rule.getSrcAddr();
			
			ruleStr += " -j MARK --set-mark "+ nameIndex.get(rule.getIfName());
			ipTables.commitMangleRule(ruleStr);
		}
		activeRules.remove(rule);
	}

	public synchronized static GrcBoxAppInfo getAppInfo(int appId) {
		GrcBoxApp app = getApp(appId);
		return new GrcBoxAppInfo(appId, app.getName(), GrcBoxServerApplication.getConfig().getKeepAliveTime());
	}

	public synchronized static  void modifyApp(int appId, String name) {
		if(appMap.containsKey(appId)){
			appMap.get(appId).setName(name);
		}
	}
	
	/*
	 * KeepAliveApp: Update the timestamp of an App to prevent removing it.
	 */
	public synchronized static void keepAliveApp(int appId) {
		appMap.get(appId).setLastKeepAlive(System.currentTimeMillis());
	}

	public synchronized static Collection<GrcBoxInterface> getOutInterfaces() {
		Collection< GrcBoxInterface> list = nm.getInterfaces();
		for (String ifname : innerInterfaces) {
			boolean found = false;
			GrcBoxInterface toRemove = null;
			for(GrcBoxInterface iface : list){
				if(iface.getName().equals(ifname)){
					found = true;
					toRemove = iface;
				}
			}
			if(found && toRemove != null){
				list.remove(toRemove);
			}
		}
		return list;
	}
	
	public synchronized static void setInnerInterfaces(LinkedList<String> innerInterfaces) {
		RulesDB.innerInterfaces = innerInterfaces;
	}

	public synchronized static List<GrcBoxRule> getAllRules() {
		List<GrcBoxRule> list = new LinkedList<GrcBoxRule>();
		for (Integer app : rulesMap.keySet()) {
			for (GrcBoxRule grcBoxRule : rulesMap.get(app).values()) {
				list.add(grcBoxRule);
			}
		}
		return list;
	}
}
