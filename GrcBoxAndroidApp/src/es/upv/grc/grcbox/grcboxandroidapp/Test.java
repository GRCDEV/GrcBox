package es.upv.grc.grcbox.grcboxandroidapp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.restlet.Client;
import org.restlet.Context;
import org.restlet.data.ChallengeResponse;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.Parameter;
import org.restlet.data.Protocol;
import org.restlet.engine.Engine;
import org.restlet.ext.jackson.JacksonConverter;
import org.restlet.resource.ClientResource;
import org.restlet.util.Series;

import android.app.Activity;
import android.util.Log;
import es.upv.grc.grcbox.common.AppResource;
import es.upv.grc.grcbox.common.AppsResource;
import es.upv.grc.grcbox.common.AppsResource.IdSecret;
import es.upv.grc.grcbox.common.GrcBoxAppInfo;
import es.upv.grc.grcbox.common.GrcBoxInterface;
import es.upv.grc.grcbox.common.GrcBoxInterfaceList;
import es.upv.grc.grcbox.common.GrcBoxRule;
import es.upv.grc.grcbox.common.GrcBoxRuleOut;
import es.upv.grc.grcbox.common.GrcBoxStatus;
import es.upv.grc.grcbox.common.IfacesResource;
import es.upv.grc.grcbox.common.RootResource;
import es.upv.grc.grcbox.common.RuleResource;
import es.upv.grc.grcbox.common.RulesResource;



public class Test extends Thread{
	
	private boolean useGrcBox;
	private GrcBoxInterface useInterface;
	private boolean appTest;
	private boolean ruleTest;
	private boolean downTest;
	
	private URI url;
	private int port;
	private String filename;
	private int loop;	
	
	private Activity activity;
	
	private volatile boolean stopThread;
	
	private static final long DEFAULT_VALUE = -1;
	
//	private GrcBoxClient gcb;
	
	private ClientResource clientResource;
	private GrcBoxInterface iface;
	private int appid;
	GrcBoxRule rule;
	ScheduledFuture<?> keepAliveMonitor = null;
	private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private IdSecret myIdSecret;
    private List<GrcBoxInterface> ifaces;
    
	public Test(Activity activity, GrcBoxInterface useInterface, boolean appTest, boolean ruleTest,
			boolean downTest, String url, int port, String filename, int loop)
	{
		Engine.getInstance().getRegisteredConverters().add(new JacksonConverter());
		this.activity =  activity;
		stopThread = false;
		useGrcBox = true;
		this.useInterface = useInterface;
		this.appTest = appTest;
		this.ruleTest = ruleTest;
		this.downTest = downTest;
		try
		{
			this.url = new URI(url);
		}
		catch(Exception e)
		{
			
		}
		this.port = port;
		this.filename = filename;
		this.loop = loop;
		
//		gcb = null;
		clientResource = new ClientResource("http://grcbox:8080");
		iface = null;
	}

	public Test(Activity activity, boolean appTest, boolean ruleTest,
			boolean downTest, String url, int port, String filename, int loop)
	{
		Engine.getInstance().getRegisteredConverters().add(new JacksonConverter());
		this.activity = activity;
		stopThread = false;
		useGrcBox = false;
		this.useInterface = null;
		this.appTest = appTest;
		this.ruleTest = ruleTest;
		this.downTest = downTest;
		try
		{
			this.url = new URI(url);
		}
		catch(Exception e)
		{
			
		}
		this.port = port;
		this.filename = filename;
		this.loop = loop;
		
//		gcb = null;
		clientResource = new ClientResource("http://grcbox:8080");
		iface = null;
	}
	
	//the Shiiiiiiiiiiittttttttttttttttttttt constructor
	public Test(Activity activity, String url, int port, String filename, int loop)
	{
		Engine.getInstance().getRegisteredConverters().add(new JacksonConverter());
		this.activity = activity;
		stopThread = false;
		useGrcBox = true;
		this.useInterface = null;
		this.appTest = true;
		this.ruleTest = true;
		this.downTest = true;
		try
		{
			this.url = new URI(url);
		}
		catch(Exception e)
		{
			
		}
		this.port = port;
		this.filename = filename;
		this.loop = loop;
		
//		gcb = null;
		clientResource = new ClientResource("http://grcbox:8080");
		iface = null;
	}
	
	private final Runnable sendKeepAlive = new Runnable() {
		@Override
		public void run() {
			AppResource appResource = clientResource.getChild("/apps/"+appid, AppResource.class);
			appResource.keepAlive();
		}
	};
	
	public void run()
	{		
		for(int i = 0; i < loop && !stopThread; i++)
		{			
			long time = System.currentTimeMillis();
			SaveDataFormat svf = new SaveDataFormat(i);	
			ScheduledFuture<?> keepAliveMonitor = null;			
			if(useGrcBox)
			{
				svf.setServerStatusTime(grcServerStatus());
				svf.setInterfaceInfoTime(getInterfaceInformation());
				chooseInterface();//
			}						
			if(appTest)
			{
				long val = performRegAppTest(i);
				svf.setRegisterAppTime(val);
//				Log.e("Register app", "test : " + String.valueOf(i) + " " + String.valueOf(val));
			}
			else
			{
				svf.setRegisterAppTime(DEFAULT_VALUE);
			}
			if(ruleTest)
			{
				long val = performRegRuleTest();
				svf.setRegisterRuleTime(val);
//				Log.e("Register rule", "test : " + String.valueOf(i) + " " + String.valueOf(val));
			}
			else
			{
				svf.setRegisterRuleTime(DEFAULT_VALUE);
			}
			if(downTest)
			{
				long val = performDownloadTest(i);
				svf.setDownloadTime(val);
//				Log.e("Download", "test : " + String.valueOf(i) + " " + String.valueOf(val));
			}
			//store value and reset variables
			if(ruleTest)
			{
				long val = performDeRegRuleTest();
				svf.setDeRegisterRuleTime(val);
//				Log.e("De-Register rule", "test : " + String.valueOf(i) + " " + String.valueOf(val));
			}
			else
			{
				svf.setDeRegisterRuleTime(DEFAULT_VALUE);
			}
			if(appTest)
			{
				long val = performDeRegAppTest();
				svf.setDeRegisterAppTime(val);
//				Log.e("De-Register app", "test : " + String.valueOf(i) + " " + String.valueOf(val));
			}
			else
			{
				svf.setDeRegisterAppTime(DEFAULT_VALUE);
			}
			svf.totalCycleTime(System.currentTimeMillis() - time);
			save(svf.generateFormatedOuput());
			System.out.println("Loop run: " + i);
		}
			
	}
	
	public long grcServerStatus()
	{
		if(useGrcBox)
		{
			long time = System.currentTimeMillis();
			RootResource rootResource = clientResource.getChild("/", RootResource.class);
//			Log.e("RootResource Object is Null", String.valueOf(rootResource==null?true:false));
			GrcBoxStatus status = rootResource.getAndroPiStatus();
//			Log.e("GrcBoxStatus Object is Null", String.valueOf(status==null?true:false));
			return System.currentTimeMillis() - time;
		}
		return DEFAULT_VALUE;
	}
	
	public long getInterfaceInformation()
	{
		if(useGrcBox)
		{
			long time = System.currentTimeMillis();
			IfacesResource ifacesResource = clientResource.getChild("/ifaces", IfacesResource.class);
			GrcBoxInterfaceList ifacesList = ifacesResource.getList();
			ifaces = ifacesList.getList();
//			System.out.println("The server has " +ifaces.size() + " ifaces");
			return System.currentTimeMillis() - time;
		}
		return DEFAULT_VALUE;
	}
	
	public long chooseInterface()
	{	
		if(useGrcBox)
		{
			/*
			 * chose the first CONNECTED interface
			 */
			for (GrcBoxInterface grcBoxInterface : ifaces) {
				if(grcBoxInterface.getState() == GrcBoxInterface.State.CONNECTED){
					iface = grcBoxInterface;
					break;
				}
			}
		}
		return DEFAULT_VALUE;
	}
	
	public long performRegAppTest(int iter)
	{
		if(useGrcBox)
		{
			long time = System.currentTimeMillis();		
			Client client = new Client(new Context(), Protocol.HTTPS);
        	Series<Parameter> parameters = client.getContext().getParameters();
        	parameters.add("truststorePath",
        			"src/org/restlet/example/book/restlet/ch05/clientTrust.jks");
        	parameters.add("truststorePassword", "password");
        	parameters.add("truststoreType", "JKS");
        	clientResource.setNext(client);
            AppsResource appsResource = clientResource.getChild("/apps", AppsResource.class);
            GrcBoxAppInfo myInfo;
            myIdSecret = appsResource.newApp("TestApp"+iter);
            appid = myIdSecret.getAppId();
            ChallengeResponse authentication = new ChallengeResponse(
        			ChallengeScheme.HTTP_BASIC, Integer.toString(myIdSecret.getAppId()), Integer.toString(myIdSecret.getSecret()).toCharArray());
        	clientResource.setChallengeResponse(authentication);
        	
        	keepAliveMonitor = scheduler.scheduleAtFixedRate(
        			sendKeepAlive, myIdSecret.getUpdatePeriod()/3, 
        			myIdSecret.getUpdatePeriod()/3, 
        			TimeUnit.MILLISECONDS);                		
			return System.currentTimeMillis() - time;
		}
		return DEFAULT_VALUE;
	}
	
	public long performDeRegAppTest()
	{
		if(useGrcBox)
		{
			long time = System.currentTimeMillis();		
//			GrcBoxClient.deregister();
			AppResource appResource = clientResource.getChild("/apps/"+appid, AppResource.class);
            appResource.rm();
            keepAliveMonitor.cancel(true);
			return System.currentTimeMillis() - time;
		}
		return DEFAULT_VALUE;
	}
	
	public long performRegRuleTest()
	{
		if(useGrcBox)
		{			
			int port = url.getPort();
			if(port == DEFAULT_VALUE)
			{
				port = this.port;
			}
    		InetAddress addr = null;
			try {
				addr = InetAddress.getByName(url.getHost());
			} catch (UnknownHostException e) {
				System.err.println("Address of the destination host could not be resolved");
				System.exit(1);
				e.printStackTrace();
			}
			long time = System.currentTimeMillis();
			RulesResource rulesResource = clientResource.getChild("/apps/"+myIdSecret.getAppId()+"/rules", RulesResource.class);
    		rule = new GrcBoxRuleOut(-1, GrcBoxRule.Protocol.TCP, myIdSecret.getAppId(), iface.getName(), 0, -1, port, addr.getHostAddress());
    		rule = rulesResource.newRule(rule);
    		return System.currentTimeMillis() - time;
		}		
		return DEFAULT_VALUE;
	}
	
	public long performDeRegRuleTest()
	{
		if(useGrcBox)
		{
			long time = System.currentTimeMillis();
			RuleResource ruleResource = clientResource.getChild("/apps/"+myIdSecret.getAppId()+"/rules/"+rule.getId(),RuleResource.class);
	        ruleResource.remove();
			return System.currentTimeMillis() - time;
		}
		return DEFAULT_VALUE;
	}
	
	public long performDownloadTest(int i)
	{		
		long time = System.currentTimeMillis();
		//direct download
			try
			{
				//String line;
				BufferedReader br = new BufferedReader(new InputStreamReader(url.toURL().openStream()));
				while (br.readLine() != null) {
		            //System.out.println(line);
		        }
//				System.out.println("Fetched " + url.toString() + " " + (i+1) + " times, in " + String.valueOf(System.currentTimeMillis() - time) + "ms");
			}
			catch(Exception e)
			{
				Log.e("TEST","Error while trying to fetch the url" + e.toString());
				return DEFAULT_VALUE;
			}
		return System.currentTimeMillis() - time;
	}
	
	public void save(String content)
	{
		FileManager fm = new FileManager(activity.getApplicationContext());
		fm.writeToFile(filename, content);
	}
	
	public void display()
	{
		FileManager fm = new FileManager(activity.getApplicationContext());
		fm.readFile(filename);
	}
	
	public void terminate()
	{
		stopThread = true;
	}
}
