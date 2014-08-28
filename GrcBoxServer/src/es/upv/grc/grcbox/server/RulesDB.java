package es.upv.grc.grcbox.server;

import java.io.IOException;
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
import es.upv.grc.grcbox.server.networkInterfaces.NetworkInterfaceManager;
import es.upv.grc.grcbox.server.networkInterfaces.NetworkManagerListener;


public class RulesDB {
	private static final Logger LOG = Logger.getLogger(NetworkInterfaceManager.class.getName()); 

	private static volatile Integer _appId = 0;
	private static volatile Integer _ruleId = 0;
	private static int 	tableId = 7;
	private static volatile Map<Integer, GrcBoxApp> appMap = new HashMap<>();
	private static volatile Map<Integer, Map<Integer, GrcBoxRule>> rulesMap = new HashMap<>();
	private static volatile HashMap<String, Integer> nameIndex = new HashMap<>();
	private static volatile LinkedList<String> innerInterfaces;
	private static volatile NetworkInterfaceManager nm;

	private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

	
	final  Runnable dbMonitor = new Runnable() {
		@Override
		public void run() {
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
	
	private class IfaceManager implements NetworkManagerListener{

		@Override
		public void interfaceRemoved(GrcBoxInterface iface) {
			removeOutIface(iface);
			LOG.info("Interface have been removed " + iface.getName());
		}

		@Override
		public void interfaceAdded(GrcBoxInterface iface) {
			if(iface.isUp()){
				initializeOutIface(iface);
			}
			LOG.info("Interface have been added " + iface.getName());
		}

		@Override
		public void interfaceChanged(GrcBoxInterface iface) {
			if(iface.isUp()){
				initializeOutIface(iface);
			}
			else{
				removeOutIface(iface);
			}
			
			LOG.info("Interface have changed " + iface.getName());
		}
	}
	
	
	/*
	 * Initialize the rules managing system.
	 */
	public synchronized void initialize(){
		
		flushNatAndMangle();
		if(innerInterfaces.size() != 1){
			LOG.severe("ERROR: CUrrently GRCBox supports only one inner iface");
			System.exit(-1);
		}
		try {
			nm = new NetworkInterfaceManager();
			nm.initialize();
			nm.subscribeInterfaces(new IfaceManager());
		} catch (DBusException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Collection<GrcBoxInterface> interfaces = nm.getInterfaces();
		for (GrcBoxInterface grcBoxInterface : interfaces) {
			initializeOutIface(grcBoxInterface);
		}
		
		/*
		 * TODO register for networkmanager updates
		 */
		long time = GrcBoxServerApplication.getConfig().getKeepAliveTime();
		final ScheduledFuture<?> monitorHandle = scheduler.scheduleAtFixedRate(dbMonitor, time, time, TimeUnit.MILLISECONDS);
	}
	
	private synchronized void initializeOutIface(GrcBoxInterface iface){
		if(!nameIndex.containsKey(iface.getName())){
			nameIndex.put(iface.getName(), tableId++);
		}
			
		String ipnat = "iptables -t nat -A POSTROUTING -o " + iface.getName() + " -j MASQUERADE";
		String iprule = "ip rule add fwmark " + nameIndex.get(iface.getName()) + " table " + nameIndex.get(iface.getName());
		String iproute = "ip route add table "+ nameIndex.get(iface.getName()) + " default dev " + iface.getName(); 
		
		String gateway = nm.getGateway(iface.getName());
		
		removeOutIface(iface);
		if(gateway != null){
				iproute += " via " + gateway;
		}
		
		try {
			LOG.info("Activating NAT on iface " + iface.getName() +"\n"+ ipnat);
			LOG.info("Create routing table for Iface " + iface.getName() +"\n"+ iprule);
			LOG.info("Adding default routing rule for Iface "+ iface.getName()+"\n" + iproute );
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
	
	private synchronized void removeOutIface(GrcBoxInterface iface){
		/*
		 * Remove iprule
		 */
		String natDel = "iptables -t nat -D POSTROUTING -o " + iface.getName() + " -j MASQUERADE";
		String rmRoute = "ip route del default table " + nameIndex.get(iface.getName());
		String ipruleDel = "ip rule del fwmark " + nameIndex.get(iface.getName());
		
		try {
			LOG.info("Remove nat rule from iptables \n" + natDel );
			LOG.info("Remove route from routing table \n" + rmRoute);
			LOG.info("Remove routing table for Iface " + iface.getName() +"\n"+ ipruleDel);

			if(!GrcBoxServerApplication.getConfig().isDebug()){
				Runtime.getRuntime().exec(natDel);
				Runtime.getRuntime().exec(rmRoute);
				Runtime.getRuntime().exec(ipruleDel);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	/*
	 * Flush all nat and masquerade rules from system.
	 */
	private synchronized void flushNatAndMangle(){
		String flushNat = "iptables -t nat -F";
		String flushMangle = "iptables -t mangle -F";
		
		try {
			LOG.info("Flushing nat and mangle rules \n" + flushNat + "\n"+ flushMangle);
	
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
		LOG.info("An App was added to the DB, ID:"+ id);
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
		LOG.info("An App was removed from the DB, ID:"+ appId +" Applications Registered " + GrcBoxServerApplication.getVerifier().getLocalSecrets().size());
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
	
	private synchronized  void addRuleToSystem(GrcBoxRule rule){
		/*
		 * TODO
		 * It must check multicast rules based on IP and throw an exception until supported
		 */
		String ruleStr = "";
		ruleStr = newRuleToCommand(rule);
		LOG.info("A new rule is going to be excuted \n" + ruleStr);
		if(!GrcBoxServerApplication.getConfig().isDebug()){
			try {
				Runtime.getRuntime().exec(ruleStr);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private  String newRuleToCommand(GrcBoxRule rule) {
		String ruleStr = "";
		if(rule.isIncomming()){
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
		else{
			ruleStr += "iptables -t mangle -A PREROUTING -i " + innerInterfaces.get(0) + " -p " + rule.getProto().toString().toLowerCase();

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
			
		}
		return ruleStr;
	}


	private synchronized  void rmRuleFromSystem(GrcBoxRule rule){
		String ruleStr; 
		ruleStr = rmRuleToCommand(rule);
		LOG.info("A rule has been removed from System:\n"+ ruleStr);
		if(!GrcBoxServerApplication.getConfig().isDebug()){
			try {
				Runtime.getRuntime().exec(ruleStr);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}


	private String rmRuleToCommand(GrcBoxRule rule) {
		String ruleStr = "";
		if(rule.isIncomming()){
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
		else{
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

	public synchronized Collection<GrcBoxInterface> getAllInterfaces() {
		return nm.getInterfaces();
	}
	
	public synchronized void setInnerInterfaces(LinkedList<String> innerInterfaces) {
		RulesDB.innerInterfaces = innerInterfaces;
	}

	public synchronized List<GrcBoxRule> getAllRules() {
		List<GrcBoxRule> list = new LinkedList<GrcBoxRule>();
		for (Integer app : rulesMap.keySet()) {
			for (GrcBoxRule grcBoxRule : rulesMap.get(app).values()) {
				list.add(grcBoxRule);
			}
		}
		return list;
	}
}
