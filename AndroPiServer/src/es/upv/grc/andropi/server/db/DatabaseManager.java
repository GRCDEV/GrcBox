package es.upv.grc.andropi.server.db;

import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import es.upv.grc.andropi.common.AndroPiApp;
import es.upv.grc.andropi.common.AndroPiAppInfo;
import es.upv.grc.andropi.common.AndroPiRule;
import es.upv.grc.andropi.common.AndroPiRule.Protocol;

public class DatabaseManager {
	private long updateTime;
	/*
	 * Tables names
	 */
	private static String TABLE_APPS = "apps";
	private static String TABLE_IN_RULES = "inRules";
	private static String TABLE_OUT_RULES = "outRules";

	/*
	 * apps table columns names
	 */
	private static String COL_APPID = "appid";
	private static String COL_SECRET = "secret";
	private static String COL_LASTKEEPALIVE = "lastKeepAlive";
	private static String COL_APP_NAME = "name";

	/*
	 * in/out Rules columns names
	 */
	private static String COL_ID = "id";
	private static String COL_EXPIRE ="expire";
	private static String COL_PROTO = "proto";
	private static String COL_IFINDEX = "ifIndex";
	private static String COL_DSTPORT = "dstPort";
	private static String COL_SRCPORT = "srcPort";
	private static String COL_DSTADDR = "dstAddr";
	private static String COL_SRCADDR = "srcAddr";

	/*
	 * In rules columns names 
	 */
	private static String COL_DSTFWDPORT = "dstFwdAddr";
	private static String COL_DSTFWDADDR = "srcFwdAddr";

	Connection dbConnection;
	boolean connected;

	public DatabaseManager(long updateTime){
		this.updateTime = updateTime;
		dbConnection = null;
		connected = false;
	}

	/*
	 * This method should be called at the moment you initialize your DB
	 * Check tables' format and create new tables if necessary
	 * Remove all rules or purge old rules and apply non-expired rules according to configuration  
	 */
	public boolean PrepareDb(String path, boolean flush){
		try {
			dbConnection = ConnectToDb(path);
			connected = true;
			if(!CheckTables()){
				CreateTables();
			}
			else{
			}
			if(flush){
				flushRules();
			}
			else{
				purgeDb();
				applyAllRules();
			}
			return true;
		} catch (SQLException e) {
			System.out.println("Error Preparing the DB");
			e.printStackTrace();
			return false;
		}
	}
	
	/*
	 * Get all the rules stored in DB and add them to the system.
	 */
	private void applyAllRules() {
		List<AndroPiRule> rules = getAllRules();
		for (AndroPiRule androPiRule : rules) {
			addRuleToSystem(androPiRule);
		}
	}
	

	/*
	 * Obtains a list of rules from a result set.
	 */
	static public List<AndroPiRule> resultToRules(ResultSet rs, boolean inRules){
		List<AndroPiRule> list = new ArrayList<>();
		try {
			while(rs.next()){
				AndroPiRule rule = null;
				if(inRules){
					int appid = rs.getInt(COL_APPID);
					int id = rs.getInt(COL_ID);
					long expire = rs.getLong(COL_EXPIRE);
					int ifIndex = rs.getInt(COL_IFINDEX);
					int proto = rs.getInt(COL_PROTO);
					int dstPort = rs.getInt(COL_DSTPORT);
					int srcPort = rs.getInt(COL_SRCPORT);
					int dstAddr = rs.getInt(COL_DSTADDR);
					int srcAddr = rs.getInt(COL_SRCADDR);
					int dstFwdPort = rs.getInt(COL_DSTFWDPORT);
					int dstFwdAddr = rs.getInt(COL_DSTFWDADDR);
					rule = new AndroPiRule(id, Protocol.fromInt(proto), true, appid, ifIndex, expire, srcPort, dstPort, srcAddr, dstAddr, dstFwdPort, dstFwdAddr);
							}
				else{
					int appid = rs.getInt(COL_APPID);
					int id = rs.getInt(COL_ID);
					long expire = rs.getLong(COL_EXPIRE);
					int ifIndex = rs.getInt(COL_IFINDEX);
					int proto = rs.getInt(COL_PROTO);
					int dstPort = rs.getInt(COL_DSTPORT);
					int srcPort = rs.getInt(COL_SRCPORT);
					int dstAddr = rs.getInt(COL_DSTADDR);
					int srcAddr = rs.getInt(COL_SRCADDR);
					rule = new AndroPiRule(id, Protocol.fromInt(proto), true, appid, ifIndex, expire, srcPort, dstPort, srcAddr, dstAddr, -1, -1);
				}

				list.add(rule);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	/*
	 * Obtains a list of Apps from a ResultSet
	 */
	static public List<AndroPiApp> resultToApps(ResultSet rs){
		List<AndroPiApp> list = new ArrayList<>();
		try {
			while(rs.next()){
				int appId = rs.getInt(COL_APPID);
				int secret = rs.getInt(COL_SECRET);
				String name = rs.getString(COL_APP_NAME);
				long lastKeepAlive = rs.getInt(COL_LASTKEEPALIVE);
				AndroPiApp app = new AndroPiApp(appId, secret, name, lastKeepAlive);
				list.add(app);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	/*
	 * Removes expired rules.
	 */
	private void purgeDb(){
		List<AndroPiApp> apps = getApps();
		for (AndroPiApp androPiApp : apps) {
			long now = System.currentTimeMillis();
			if(now - androPiApp.getLastKeepAlive() > updateTime*3){
				purgeApp(androPiApp);
			}
		}
		List<AndroPiRule> rules = getExpiredRules();
		for (AndroPiRule androPiRule : rules) {
			rmRule(androPiRule);
		}
	}
	
	/*
	 * Remove an App and all its rules from system and DB
	 */
	public boolean purgeApp(AndroPiApp app) {
		 return purgeApp(app.getAppId());
	}
	
	public boolean purgeApp(int appId) {
		List<AndroPiRule> rules = getRulesByApp(appId);
		for (AndroPiRule androPiRule : rules) {
			rmRule(androPiRule);
		}
		try {
			Statement statement = dbConnection.createStatement();
			statement.executeUpdate("DELETE FROM "+TABLE_APPS +" WHERE "+ COL_APPID + "==" + appId);
			statement.close();
		} catch (SQLException e) {
			
			e.printStackTrace();
			return false;
		}
		return true;		
	}
	
	/*
	 * Returns all the rules associated to an app
	 */
	public List<AndroPiRule> getRulesByApp(int appId) {
		Statement statement;
		List<AndroPiRule> rules=new ArrayList<>();
		try {
			statement = dbConnection.createStatement();
			ResultSet rs = statement.executeQuery("SELECT * FROM "+TABLE_IN_RULES+ " WHERE "+COL_APPID +" == " + appId);
			rules.addAll(resultToRules(rs, true));
			rs = statement.executeQuery("SELECT * FROM "+TABLE_OUT_RULES+ " WHERE "+COL_APPID +" == " + appId);
			rules.addAll(resultToRules(rs, false));
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rules;
	}
	
	/*
	 * Returns the Id  all the rules associated to an app
	 */
	private List<Integer> getRulesIdByApp(int appId) {
		Statement statement;
		List<Integer> rules=new ArrayList<>();
		try {
			statement = dbConnection.createStatement();
			ResultSet rs = statement.executeQuery("SELECT " + COL_ID + " FROM " +TABLE_IN_RULES+ " WHERE "+COL_APPID +" == " + appId);
			while(rs.next()){
				rules.add(rs.getInt(COL_ID));
			}
			rs = statement.executeQuery("SELECT * FROM "+TABLE_OUT_RULES+ " WHERE "+COL_APPID +" == " + appId);
			while(rs.next()){
				rules.add(rs.getInt(COL_ID));
			}
			rs.close();
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return rules;
	}
	

	/*
	 * Add a new rule
	 *   check if any colliding rule exists
	 *   Call addRuleToSystem
	 *   Add rule to DB
	 */
	public AndroPiRule addRule(AndroPiRule rule){
		/*
		 * A Nat rule
		 */
		if(rule.isIncomming()){
			
		}
		/*
		 * An outgoing rule
		 */
		else{
			
		}
			
		if(!addRuleToSystem(rule)){
			rule = null;
		}
		return rule;
	}

	public boolean addRuleToSystem(AndroPiRule rule){
		System.out.println("A new rule has been added:");
		System.out.println("AppId:"+rule.getAppid());
		System.out.println("SrcPort:"+rule.getSrcPort());
		System.out.println("SrcAddr:"+rule.getSrcAddr());
		System.out.println("DstPort:"+rule.getDstPort());
		System.out.println("DstAddr:"+rule.getDstAddr());
		return true;
	}

	/*
	 * Remove rule from db
	 * Remove rule from system.	
	 */
	public void rmRule(AndroPiRule rule){
		rmRuleId(rule.getId());
	}
	
	public void rmRuleId(int id){
		rmRuleFromSystem(getRuleFromId(id));
		try {
			Statement statement = dbConnection.createStatement();
			statement.executeUpdate("DELETE FROM "+TABLE_IN_RULES +" WHERE "+ COL_ID + "==" + id);
			statement.executeUpdate("DELETE FROM "+TABLE_OUT_RULES +" WHERE "+ COL_ID + "==" + id);
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public AndroPiRule getRuleFromId(int id) {
		try{
			Statement st = dbConnection.createStatement();
			ResultSet rs = st.executeQuery("SELECT * FROM " + TABLE_IN_RULES + " WHERE "+ COL_ID +"=="+id);
			List<AndroPiRule> list = resultToRules(rs, true);
			if(list == null){
				rs = st.executeQuery("SELECT * FROM " + TABLE_IN_RULES + " WHERE "+ COL_ID +"=="+id);
				list = resultToRules(rs, true);
			}
			
			if(list == null){
				return null;
			}
			else{
				return list.get(0);
			}
		}
		catch (SQLException e){
			e.printStackTrace();
		}
		return null;
	}

	public boolean rmRuleFromSystem(AndroPiRule rule){
		System.out.println("A rule has been removed:");
		System.out.println("AppId:"+rule.getAppid());
		System.out.println("SrcPort:"+rule.getSrcPort());
		System.out.println("SrcAddr:"+rule.getSrcAddr());
		System.out.println("DstPort:"+rule.getDstPort());
		System.out.println("DstAddr:"+rule.getDstAddr());
		return true;	
	}
	
	
	/*
	 * Flushes all rules from db and system
	 */
	public void flushRules(){
		flushRulesFromSystem();
		try {
			Statement statement = dbConnection.createStatement();
			statement.executeUpdate("DELETE FROM "+TABLE_APPS);
			statement.executeUpdate("DELETE FROM "+TABLE_IN_RULES);
			statement.executeUpdate("DELETE FROM "+TABLE_OUT_RULES);
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public boolean flushRulesFromSystem(){
		System.out.println("All rules hae been flushed");
		return true;
	}

	/*
	 * Get Expired rules from tables
	 */
	public List<AndroPiRule> getExpiredRules(){
		long now = System.currentTimeMillis();
		List<AndroPiRule> rules = new ArrayList<>();
		try {
			Statement statement = dbConnection.createStatement();
			ResultSet rs = statement.executeQuery("SELECT * FROM "+TABLE_IN_RULES+ " WHERE "+COL_EXPIRE +" < " + now);
			List<AndroPiRule> inRules = resultToRules(rs, true);
			rs = statement.executeQuery("SELECT * FROM "+TABLE_OUT_RULES+ " WHERE "+COL_EXPIRE +" < " + now);
			List<AndroPiRule> outRules = resultToRules(rs, false);
			rules.addAll(inRules);
			rules.addAll(outRules);
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rules;
	}

	/*
	 * Get all rules from table
	 */
	public List<AndroPiRule> getAllRules(){
		List<AndroPiRule> rules = getInRules();
		rules.addAll(getOutRules());
		return rules;
	}
	
	/*
	 * Get in rules from table
	 */
	public List<AndroPiRule> getInRules(){
		List<AndroPiRule> rules = new ArrayList<>();
		try {
			Statement statement = dbConnection.createStatement();
			ResultSet rs = statement.executeQuery("SELECT * FROM "+TABLE_IN_RULES);
			rules = resultToRules(rs, true);
			rs.close();
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rules;		
	}
	
	/*
	 * get Out rules
	 */
	public List<AndroPiRule> getOutRules(){
		List<AndroPiRule> rules = new ArrayList<>();
		try {
			Statement statement = dbConnection.createStatement();
			ResultSet rs = statement.executeQuery("SELECT * FROM "+TABLE_OUT_RULES);
			rules = resultToRules(rs, true);
			rs.close();
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rules;		
	}

	/*
	 * Get all apps from list
	 */
	public List<AndroPiApp> getApps(){
		List<AndroPiApp> apps = new ArrayList<>();
		try {
			Statement statement = dbConnection.createStatement();
			ResultSet rs = statement.executeQuery("SELECT * FROM "+TABLE_APPS);
			apps = resultToApps(rs);
			rs.close();
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return apps;		
	}

	/*
	 * Load database according to configuration, if it does not exist, create path
	 */
	private Connection ConnectToDb(String path) throws SQLException{
		Connection connection;
		File tmp = new File(path);
		File directory = tmp.getParentFile();
		directory.mkdirs();

		connection = DriverManager.getConnection("jdbc:sqlite:"+path);
		return connection;
	}

	/*
	 * Creates the rules and apps tables in the DB.
	 */
	private boolean CreateTables(){
		Statement statement;
		try {
			statement = dbConnection.createStatement();
			statement.setQueryTimeout(30);  // set timeout to 30 sec.
			try{
				statement.executeUpdate("DROP TABLE IF EXISTS "+TABLE_APPS);
				statement.executeUpdate("DROP TABLE IF EXISTS "+TABLE_IN_RULES);
				statement.executeUpdate("DROP TABLE IF EXISTS "+TABLE_OUT_RULES);
				statement.executeUpdate("CREATE TABLE "+ TABLE_APPS +"(" +
						" "+ COL_APP_NAME +" text," +
						" " +COL_SECRET+" integer,"+
						" " + COL_LASTKEEPALIVE +" datetime)");
				statement.executeUpdate("CREATE TABLE " +TABLE_IN_RULES + "(" +
						" " + COL_APPID + "appid integer," +
						" " + COL_EXPIRE + " datetime," +
						" " + COL_PROTO + "integer" +
						" " + COL_IFINDEX + " integer," +
						" " + COL_DSTPORT + " integer," +
						" " + COL_SRCPORT + " integer," +
						" " + COL_SRCADDR + " integer," +
						" " + COL_DSTADDR + " integer," +
						" " + COL_DSTFWDPORT + " integer," +
						" " + COL_DSTFWDADDR + " integer" +
						")");
				statement.executeUpdate("CREATE TABLE " + TABLE_OUT_RULES + "(" +
						" " + COL_APPID + "appid integer," +
						" " + COL_EXPIRE + " datetime," +
						" " + COL_PROTO + "integer" +
						" " + COL_IFINDEX + " integer," +
						" " + COL_DSTPORT + " integer," +
						" " + COL_SRCPORT + " integer," +
						" " + COL_SRCADDR + " integer," +
						" " + COL_DSTADDR + " integer" +
						")");
				statement.close();
			}catch(SQLException e){
				System.out.println("Problem creating tables");
				e.printStackTrace();
				System.exit(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/*
	 * Check if tables exist and have the correct names
	 * TODO Check Variables types of the tables.
	 */
	private boolean CheckTables() throws SQLException{
		DatabaseMetaData meta = dbConnection.getMetaData();
		ResultSet rs = meta.getTables(null, null, null,  new String[] {"TABLE"});
		int exists = 0;
		while(rs.next()){
			String tableName = rs.getString("TABLE_NAME");
			if( tableName.equals("inRules") || tableName.equals("outRules") || tableName.equals("apps") ){
				exists++;
			}
		}
		rs.close();
		return exists == 3;
	}

	public AndroPiAppInfo getAppInfo(int appId) {
		AndroPiAppInfo info;
		AndroPiApp app = getApp(appId);
		if(app != null){
			info = new AndroPiAppInfo(app.getAppId(), app.getName());
		}
		else{
			info = null;
		}
		return info;
	}

	public AndroPiApp getApp(int appId) {
		AndroPiApp app = null;
		Statement st;
		List<AndroPiApp> apps = null;
		try {
			st = dbConnection.createStatement();
			ResultSet rs = st.executeQuery("SELECT * FROM "+ TABLE_APPS +" WHERE "+ COL_APPID + " == " + appId);
			apps  = resultToApps(rs);
			st.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		if(apps.size() == 1){
			app = apps.get(0);
		}
		return app;
	}

	public void modifyApp(int appId, String name){
		Statement st;
		try {
			st = dbConnection.createStatement();
			st.executeUpdate("UPDATE "+ TABLE_APPS +" SET " + COL_APP_NAME + "=" + name + " WHERE " + COL_APPID + " == " + appId);
			st.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/*
	 * create a new app, return its id
	 */
	public int newApp(String name, int secret) {
		Statement st;
		int appId;
		try{
			st=dbConnection.createStatement();
			String query = "INSERT INTO " + TABLE_APPS + " (" 
					+ COL_APP_NAME +","
					+ COL_SECRET+","
					+ COL_LASTKEEPALIVE+ ")" 
					+ " VALUES (\'"
					+ name + "\',\'"
					+ secret + "\', \'"
					+ System.currentTimeMillis()+"\')";
			st.executeUpdate(query);
			ResultSet rs = st.executeQuery("SELECT " + COL_APPID + " FROM "+ TABLE_APPS + " WHERE " + COL_SECRET + " == \'" + secret + "\'");
			appId = rs.getInt(COL_APPID);
			st.close();
		}
		catch (SQLException e) {
			e.printStackTrace();
			appId = -1;
		}
		return appId;
	}

	public AndroPiRule getRuleById(int ruleId) {
		AndroPiRule rule = null;
		Statement st;
		List<AndroPiRule> rules = null;
		try {
			st = dbConnection.createStatement();
			ResultSet rs = st.executeQuery("SELECT * FROM "+ TABLE_IN_RULES +" WHERE "+ COL_ID + " == " + ruleId);
			rules  = resultToRules(rs, true);
			
			if(rules == null){
				rs = st.executeQuery("SELECT * FROM "+ TABLE_OUT_RULES +" WHERE "+ COL_ID + " == " + ruleId);
				rules  = resultToRules(rs, false);
			}
			st.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		if(rules.size() == 1){
			rule = rules.get(0);
		}
		return rule;
	}
}
