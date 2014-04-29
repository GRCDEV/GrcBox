package es.upv.grc.andropi.server.db;

import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import es.upv.grc.andropi.common.AndroPiApp;
import es.upv.grc.andropi.common.AndroPiRule;

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
	private static String APPID = "appid";
	private static String SECRET = "secret";
	private static String LASTKEEPALIVE = "lastKeepAlive";
	private static String APP_NAME = "name";

	/*
	 * in/out Rules columns names
	 */
	private static String ID = "id";
	private static String EXPIRE ="expire";
	private static String IFINDEX = "ifIndex";
	private static String DSTPORT = "dstPort";
	private static String SRCPORT = "srcPort";
	private static String DSTADDR = "dstAddr";
	private static String SRCADDR = "srcAddr";

	/*
	 * In rules columns names 
	 */
	private static String DSTFWDPORT = "dstFwdAddr";
	private static String DSTFWDADDR = "srcFwdAddr";

	Connection dbConnection;
	boolean connected;

	public DatabaseManager(long updateTime){
		this.updateTime = updateTime;
		dbConnection = null;
		connected = false;
	}

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
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

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
					int appid = rs.getInt(APPID);
					int id = rs.getInt(ID);
					Date expire = rs.getDate(EXPIRE);
					int ifIndex = rs.getInt(IFINDEX);
					int dstPort = rs.getInt(DSTPORT);
					int srcPort = rs.getInt(SRCPORT);
					int dstAddr = rs.getInt(DSTADDR);
					int srcAddr = rs.getInt(SRCADDR);
					int dstFwdPort = rs.getInt(DSTFWDPORT);
					int dstFwdAddr = rs.getInt(DSTFWDADDR);
					rule = new AndroPiRule(id, appid, ifIndex, expire, srcPort, dstPort, srcAddr, dstAddr, dstFwdPort, dstFwdAddr);
				}
				else{
					int appid = rs.getInt(APPID);
					int id = rs.getInt(ID);
					Date expire = rs.getDate(EXPIRE);
					int ifIndex = rs.getInt(IFINDEX);
					int dstPort = rs.getInt(DSTPORT);
					int srcPort = rs.getInt(SRCPORT);
					int dstAddr = rs.getInt(DSTADDR);
					int srcAddr = rs.getInt(SRCADDR);
					rule = new AndroPiRule(id, appid, ifIndex, expire, srcPort, dstPort, srcAddr, dstAddr, -1, -1);
				}

				list.add(rule);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}

	static public List<AndroPiApp> resultToApps(ResultSet rs){
		List<AndroPiApp> list = new ArrayList<>();
		try {
			while(rs.next()){
				int appId = rs.getInt(APPID);
				int secret = rs.getInt(SECRET);
				String name = rs.getString(APP_NAME);
				long lastKeepAlive = rs.getInt(LASTKEEPALIVE);
				AndroPiApp app = new AndroPiApp(appId, secret, name, lastKeepAlive);
				list.add(app);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
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

	private void purgeApp(AndroPiApp app) {
		List<AndroPiRule> rules = getRulesByApp(app.getAppId());
		for (AndroPiRule androPiRule : rules) {
			rmRule(androPiRule);
		}
	}
	

	private List<AndroPiRule> getRulesByApp(int appId) {
		Statement statement;
		List<AndroPiRule> rules=new ArrayList<>();
		try {
			statement = dbConnection.createStatement();
			ResultSet rs = statement.executeQuery("SELECT * FROM "+TABLE_IN_RULES+ " WHERE "+APPID +" == " + appId);
			rules.addAll(resultToRules(rs, true));
			rs = statement.executeQuery("SELECT * FROM "+TABLE_OUT_RULES+ " WHERE "+APPID +" == " + appId);
			rules.addAll(resultToRules(rs, false));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
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
	public void addRule(AndroPiRule rule){

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

	}

	
	public boolean rmRuleFromSystem(AndroPiRule rule){
		System.out.println("A rule has been removed:");
		System.out.println("AppId:"+rule.getAppid());
		System.out.println("SrcPort:"+rule.getSrcPort());
		System.out.println("SrcAddr:"+rule.getSrcAddr());
		System.out.println("DstPort:"+rule.getDstPort());
		System.out.println("DstAddr:"+rule.getDstAddr());
		return true;	}
	
	/*
	 * Flushes all rules from db and system
	 */
	public void flushRules(){
		flushRulesFromSystem();
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
			ResultSet rs = statement.executeQuery("SELECT * FROM "+TABLE_IN_RULES+ " WHERE "+EXPIRE +" < " + now);
			List<AndroPiRule> inRules = resultToRules(rs, true);
			rs = statement.executeQuery("SELECT * FROM "+TABLE_OUT_RULES+ " WHERE "+EXPIRE +" < " + now);
			List<AndroPiRule> outRules = resultToRules(rs, false);
			rules.addAll(inRules);
			rules.addAll(outRules);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rules;
	}

	public List<AndroPiRule> getAllRules(){
		List<AndroPiRule> rules = getInRules();
		rules.addAll(getOutRules());
		return rules;
	}
	
	public List<AndroPiRule> getInRules(){
		List<AndroPiRule> rules = new ArrayList<>();
		try {
			Statement statement = dbConnection.createStatement();
			ResultSet rs = statement.executeQuery("SELECT * FROM "+TABLE_IN_RULES);
			rules = resultToRules(rs, true);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rules;		
	}

	public List<AndroPiRule> getOutRules(){
		List<AndroPiRule> rules = new ArrayList<>();
		try {
			Statement statement = dbConnection.createStatement();
			ResultSet rs = statement.executeQuery("SELECT * FROM "+TABLE_OUT_RULES);
			rules = resultToRules(rs, true);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rules;		
	}

	public List<AndroPiApp> getApps(){
		List<AndroPiApp> rules = new ArrayList<>();
		try {
			Statement statement = dbConnection.createStatement();
			ResultSet rs = statement.executeQuery("SELECT * FROM "+TABLE_APPS);
			rules = resultToApps(rs);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rules;		
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
				statement.executeUpdate("CREATE TABLE apps(" +
						" "+ APPID + " integer PRIMARY KEY, " +
						" "+ APP_NAME +" text," +
						" " +SECRET+" integer,"+
						" " + LASTKEEPALIVE +" datetime)");
				statement.executeUpdate("CREATE TABLE inRules(" +
						" " + APPID + "appid integer," +
						" " + ID + "integer PRIMARY KEY,"+
						" " + EXPIRE + " datetime," +
						" " + IFINDEX + " integer," +
						" " + DSTPORT + " integer," +
						" " + SRCPORT + " integer," +
						" " + SRCADDR + " integer," +
						" " + DSTADDR + " integer," +
						" " + DSTFWDPORT + " integer," +
						" " + DSTFWDADDR + " integer" +
						")");
				statement.executeUpdate("CREATE TABLE outRules(" +
						" " + APPID + "appid integer," +
						" " + ID + "integer PRIMARY KEY,"+
						" " + EXPIRE + " datetime," +
						" " + IFINDEX + " integer," +
						" " + DSTPORT + " integer," +
						" " + SRCPORT + " integer," +
						" " + SRCADDR + " integer," +
						" " + DSTADDR + " integer" +
						")");
			}catch(SQLException e){
				System.out.println("Problem creating tables");
				e.printStackTrace();
				System.exit(1);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
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
}
