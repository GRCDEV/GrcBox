package es.upv.grc.grcbox.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.restlet.Client;
import org.restlet.Context;
import org.restlet.data.ChallengeResponse;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.Parameter;
import org.restlet.data.Protocol;
import org.restlet.resource.ClientResource;
import org.restlet.util.Series;

import es.upv.grc.grcbox.common.GrcBoxAppInfo;
import es.upv.grc.grcbox.common.GrcBoxInterface;
import es.upv.grc.grcbox.common.resources.AppResource;
import es.upv.grc.grcbox.common.resources.AppsResource;
import es.upv.grc.grcbox.common.resources.IfacesResource;
import es.upv.grc.grcbox.common.resources.RootResource;
import es.upv.grc.grcbox.common.resources.RuleResource;
import es.upv.grc.grcbox.common.resources.RulesResource;
import es.upv.grc.grcbox.common.resources.AppsResource.IdSecret;
import es.upv.grc.grcbox.common.GrcBoxInterfaceList;
import es.upv.grc.grcbox.common.GrcBoxRule;
import es.upv.grc.grcbox.common.GrcBoxStatus;



/*
 * This class is a performance test of the GrcBox server.
 */
public class TestClient {
	enum Method{
		GRCBOX, 
		NORMAL
	}
	private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	private static int appid = 0;
	private static int ruleId = 0;
	private static ClientResource clientResource;
	
	private final static Runnable sendKeepAlive = new Runnable() {
		@Override
		public void run() {
			AppResource appResource = clientResource.getChild("/apps/"+appid, AppResource.class);
			appResource.keepAlive();
		}
	};
	
	public static void main(String[] args) {
		Logger logger = Logger.getLogger("MyLog");
		URL testUrl = null;
	    FileHandler fh;
	    Method method = null;
	    ScheduledFuture<?> keepAliveMonitor =null;
	    try {  
	        // This block configure the logger with handler and formatter  
	        fh = new FileHandler("./GrcBoxTestClient.log");  
	        logger.addHandler(fh);
	        SimpleFormatter formatter = new SimpleFormatter();  
	        fh.setFormatter(formatter);
	    } catch (SecurityException e) {  
	        e.printStackTrace();  
	    } catch (IOException e) {  
	        e.printStackTrace();  
	    }  

	    int loop=0;
	    if (args.length == 3) {
	        try {
	            loop = Integer.parseInt(args[1]);
	        } catch (NumberFormatException e) {
	            System.err.println("Argument" + args[1] + " must be an integer.");
	            System.exit(1);
	        }
	        
	        if(args[0].equals("grcbox")){
	        	method = Method.GRCBOX;
	        }
	        else if(args[0].equals("normal")){
	        	method = Method.NORMAL;
	        }
	        else{
	        	System.err.println("Argument" + args[0] + " must be grcbox/normal.");
	        	System.exit(1);
	        }
	        
	        try {
				testUrl = new URL(args[2]);
			} catch (MalformedURLException e) {
				System.err.println("Argument "+ args[2] + " must be a well formed URL");
				e.printStackTrace();
			}
	    }
	    else{
	    	System.err.println("The program need at least 3 arguments: <method>(normal/grcbox) numberOfLoops  url");
	    	System.exit(1);
	    }
	    
	    for (int iter = 0; iter < loop; iter++) {
	    	long startTime = System.currentTimeMillis();
	    	logger.info("##TEST " + iter + " Method " + method);
	    	logger.info("START "+ startTime );
	    	
	    	clientResource = new ClientResource("http://grcbox:8080");
	    	/*
	    	 * Using GRCBox
	    	 */
	    	if(method == Method.GRCBOX){
	    		
	    		
	    		/*
	    		 * Get the status of the server
	    		 */
	    		long checkTime1 = System.currentTimeMillis();
	    		RootResource rootResource = clientResource.getChild("/", RootResource.class);
	    		GrcBoxStatus status = rootResource.getGrcBoxStatus();
	    		long checkTime2 = System.currentTimeMillis();
	    		logger.info("CheckStatus " +checkTime2 +" "+ (checkTime2 -checkTime1));
	    		/*
	    		 * Get list of interfaces
	    		 */
	    		long ifacesTime1 = System.currentTimeMillis();
	    		IfacesResource ifacesResource = clientResource.getChild("/ifaces", IfacesResource.class);
	    		GrcBoxInterfaceList ifacesList = ifacesResource.getList();
	    		long ifacesTime2 = System.currentTimeMillis();
	    		Collection<GrcBoxInterface> ifaces = ifacesList.getList();
	    		logger.info("ListOfIfaces " +ifacesTime2 +" "+ (ifacesTime2 - ifacesTime1));
	    		
	    		GrcBoxInterface iface = null;
	    		/*
	    		 * chose the first CONNECTED interface
	    		 */
	    		for (GrcBoxInterface grcBoxInterface : ifaces) {
	    			if(grcBoxInterface.hasInternet()){
	    				iface = grcBoxInterface;
	    				break;
	    			}
	    		}
//	    		
	    		/*
	             * Register a new application
	             */
	    		long t1register = System.currentTimeMillis();
	        	Client client = new Client(new Context(), Protocol.HTTPS);
	        	Series<Parameter> parameters = client.getContext().getParameters();
	        	parameters.add("truststorePath",
	        			"src/org/restlet/example/book/restlet/ch05/clientTrust.jks");
	        	parameters.add("truststorePassword", "password");
	        	parameters.add("truststoreType", "JKS");
	        	clientResource.setNext(client);
	            AppsResource appsResource = clientResource.getChild("/apps", AppsResource.class);
	            GrcBoxAppInfo myInfo;
	            IdSecret myIdSecret;
	            myIdSecret = appsResource.newApp("TestApp"+iter);
	            appid = myIdSecret.getAppId();
	            ChallengeResponse authentication = new ChallengeResponse(
	        			ChallengeScheme.HTTP_BASIC, Integer.toString(myIdSecret.getAppId()), Integer.toString(myIdSecret.getSecret()).toCharArray());
	        	clientResource.setChallengeResponse(authentication);
	        	
	        	keepAliveMonitor = scheduler.scheduleAtFixedRate(
	        			sendKeepAlive, myIdSecret.getUpdatePeriod()/3, 
	        			myIdSecret.getUpdatePeriod()/3, 
	        			TimeUnit.MILLISECONDS);
	        	
	            long t2register = System.currentTimeMillis();
	            logger.info("Register "+ t2register + " " + (t2register - t1register));
	            
	    		int port = testUrl.getPort();
	    		InetAddress addr = null;
				try {
					addr = InetAddress.getByName(testUrl.getHost());
				} catch (UnknownHostException e) {
					System.err.println("Address of the destination host could not be resolved");
					System.exit(1);
					e.printStackTrace();
				}
				
				long t1rule = System.currentTimeMillis();
				RulesResource rulesResource = clientResource.getChild("/apps/"+myIdSecret.getAppId()+"/rules", RulesResource.class);
	    		GrcBoxRule rule = new GrcBoxRule(-1, GrcBoxRule.Protocol.TCP, false, myIdSecret.getAppId(), iface.getName(), 0, -1, port, addr.getHostAddress(), null, port, null);
	    		rule = rulesResource.newRule(rule);
	    		ruleId = rule.getId();
	    		long t2rule = System.currentTimeMillis();	    		
	    		logger.info("Rule " +t2rule+" "+ (t2rule - t1rule)); 
	    	}
	    	
	    	/*
	    	 * Common code for both implementations
	    	 */
	    	long t1Download = System.currentTimeMillis();
	        BufferedReader in;
			try {
				in = new BufferedReader(
				new InputStreamReader(testUrl.openStream()));
				String inputLine;
				while ((inputLine = in.readLine()) != null){
					
				}
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			long t2Download = System.currentTimeMillis();
			logger.info("Download  "+ t2Download + " " + (t2Download- t1Download));
			
			/*
			 * Second block after downloading the file
			 */
			if(method == Method.GRCBOX){
				long rmRule1 = System.currentTimeMillis();
				RuleResource ruleResource = clientResource.getChild("/apps/"+appid+"/rules/"+ruleId, RuleResource.class);
				ruleResource.remove();
				long rmRule2 = System.currentTimeMillis();
				logger.info("RmRule "+ rmRule2 + " " + (rmRule2-rmRule1));
				/*
				 * Deregister App
				 */
	            AppResource appResource = clientResource.getChild("/apps/"+appid, AppResource.class);
	            long t1remove = System.currentTimeMillis();
	            appResource.rm();
	            long t2remove = System.currentTimeMillis();
	            logger.info("Remove  "+ t2remove + " " + (t2remove- t1remove));
	            keepAliveMonitor.cancel(true);
	            clientResource.release();
			}
			logger.info("Total  "+ System.currentTimeMillis() + " " + (System.currentTimeMillis()- startTime));
	    }
	    System.exit(0);
	}

}
