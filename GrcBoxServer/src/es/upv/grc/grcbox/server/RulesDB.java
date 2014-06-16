package es.upv.grc.grcbox.server;

import java.util.*;

import es.upv.grc.grcbox.common.GrcBoxApp;
import es.upv.grc.grcbox.common.GrcBoxAppInfo;
import es.upv.grc.grcbox.common.GrcBoxRule;
import es.upv.grc.grcbox.common.GrcBoxRuleIn;
import es.upv.grc.grcbox.common.GrcBoxRuleOut;

public class RulesDB {
	private static Integer _appId = 0;
	private static Integer _ruleId = 0;
	private static Map<Integer, GrcBoxApp> appMap = new HashMap<>();
	private static Map<Integer, Map<Integer, GrcBoxRule>> rulesMap = new HashMap<>();
		
	/*
	 * Returns the app maped to a certain ID or null
	 */
	public GrcBoxApp getApp(Integer appId){
		return appMap.get(appId);
	}
	
	/*
	 * Return a list of Apps registered in the system
	 */
	public List<GrcBoxApp> getApps() {
		return new LinkedList<>(appMap.values());
	}
	
	/*
	 * Returns the rule maped to a certain ID or null
	 */
	public GrcBoxRule getRule(Integer appId, Integer ruleId){
		return rulesMap.get(appId).get(ruleId);
	}
	
	public List<GrcBoxRule> getRulesByApp(Integer appId){
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
	public int addApp(String name){
		int id = _appId++;
		GrcBoxApp app = new GrcBoxApp(id, name, System.currentTimeMillis());
		appMap.put(app.getAppId(), app);
		System.out.println(System.currentTimeMillis()+" An App was added to the DB, ID:"+ id);
		return id;
	}
	
	/*
	 * Remove an applications and its rules from the DB and the system
	 */
	public void rmApp(Integer appId){
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
	 */
	public GrcBoxRule addRule(Integer appId, GrcBoxRule rule){
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
	public void rmRule(Integer appId, Integer ruleId){
		if(rulesMap.containsKey(appId) && rulesMap.get(appId).containsKey(ruleId)){
			rmRuleFromSystem(rulesMap.get(appId).get(ruleId));
			rulesMap.get(appId).remove(ruleId);
		}
	}
	
	private void addRuleToSystem(GrcBoxRule rule) {
		/*
		 * TODO Dummy method
		 */
		String ruleStr;
		if(rule.isIncomming()){
			ruleStr = new GrcBoxRuleIn(rule).createIptablesRule();
		}
		else{
			ruleStr = new GrcBoxRuleOut(rule).createIptablesRule(5);
		}
		
		System.out.println("A new rule is going to be excuted \n" + ruleStr);
	}
	
	private void rmRuleFromSystem(GrcBoxRule rule){
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
		System.out.println("A rule has been removed from System:\n"+ ruleStr);
	}

	public GrcBoxAppInfo getAppInfo(int appId) {
		GrcBoxApp app = getApp(appId);
		return new GrcBoxAppInfo(appId, app.getName(), GrcBoxServerApplication.getConfig().getDatabase().getUpdateTime());
	}

	public void modifyApp(int appId, String name) {
		if(appMap.containsKey(appId)){
			appMap.get(appId).setName(name);
		}
	}
	
	/*
	 * KeepAliveApp: Update the timestamp of an App to prevent removing it.
	 */
	public void keepAliveApp(int appId) {
		appMap.get(appId).setLastKeepAlive(System.currentTimeMillis());
	}
}
