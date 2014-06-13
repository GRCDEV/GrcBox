package es.upv.grc.grcbox.server;

import java.util.*;

import es.upv.grc.grcbox.common.AndroPiApp;
import es.upv.grc.grcbox.common.AndroPiAppInfo;
import es.upv.grc.grcbox.common.AndroPiRule;
import es.upv.grc.grcbox.common.AndroPiRuleIn;
import es.upv.grc.grcbox.common.AndroPiRuleOut;

public class RulesDB {
	private static Integer _appId = 0;
	private static Integer _ruleId = 0;
	private static Map<Integer, AndroPiApp> appMap = new HashMap<>();
	private static Map<Integer, Map<Integer, AndroPiRule>> rulesMap = new HashMap<>();
		
	/*
	 * Returns the app maped to a certain ID or null
	 */
	public AndroPiApp getApp(Integer appId){
		return appMap.get(appId);
	}
	
	/*
	 * Return a list of Apps registered in the system
	 */
	public List<AndroPiApp> getApps() {
		return new LinkedList<>(appMap.values());
	}
	
	/*
	 * Returns the rule maped to a certain ID or null
	 */
	public AndroPiRule getRule(Integer appId, Integer ruleId){
		return rulesMap.get(appId).get(ruleId);
	}
	
	public List<AndroPiRule> getRulesByApp(Integer appId){
		if(appMap.containsKey(appId)){
			if(rulesMap.containsKey(appId)){
				return new LinkedList<AndroPiRule>(rulesMap.get(appId).values());
			}
			else{
				return new LinkedList<AndroPiRule>();
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
		AndroPiApp app = new AndroPiApp(id, name, System.currentTimeMillis());
		appMap.put(app.getAppId(), app);
		System.out.println(System.currentTimeMillis()+" An App was added to the DB, ID:"+ id);
		return id;
	}
	
	/*
	 * Remove an applications and its rules from the DB and the system
	 */
	public void rmApp(Integer appId){
		Map<Integer, AndroPiRule> rules = rulesMap.get(appId);
		if(rules != null){
			for (AndroPiRule rule : rules.values()) {
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
	public AndroPiRule addRule(Integer appId, AndroPiRule rule){
		if(appMap.containsKey(appId)){
			rule.setId(_ruleId++);
			rule.setAppid(appId);
			if(!rulesMap.containsKey(appId)){
				rulesMap.put(appId, new HashMap<Integer, AndroPiRule>());
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
	
	private void addRuleToSystem(AndroPiRule rule) {
		/*
		 * TODO Dummy method
		 */
		String ruleStr;
		if(rule.isIncomming()){
			ruleStr = new AndroPiRuleIn(rule).createIptablesRule();
		}
		else{
			ruleStr = new AndroPiRuleOut(rule).createIptablesRule(5);
		}
		
		System.out.println("A new rule is going to be excuted \n" + ruleStr);
	}
	
	private void rmRuleFromSystem(AndroPiRule rule){
		/*
		 * TODO Dummy method
		 */
		String ruleStr;
		if(rule.isIncomming()){
			ruleStr = new AndroPiRuleIn(rule).deleteIptablesRule();
		}
		else{
			ruleStr = new AndroPiRuleOut(rule).deleteIptablesRule();
		}
		System.out.println("A rule has been removed from System:\n"+ ruleStr);
	}

	public AndroPiAppInfo getAppInfo(int appId) {
		AndroPiApp app = getApp(appId);
		return new AndroPiAppInfo(appId, app.getName(), AndroPiServerApplication.getConfig().getDatabase().getUpdateTime());
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
