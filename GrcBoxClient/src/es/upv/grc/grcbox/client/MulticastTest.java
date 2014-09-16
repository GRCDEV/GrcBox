package es.upv.grc.grcbox.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
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
import es.upv.grc.grcbox.common.GrcBoxInterfaceList;
import es.upv.grc.grcbox.common.GrcBoxRule;
import es.upv.grc.grcbox.common.GrcBoxStatus;
import es.upv.grc.grcbox.common.GrcBoxRule.RuleType;
import es.upv.grc.grcbox.common.resources.AppResource;
import es.upv.grc.grcbox.common.resources.AppsResource;
import es.upv.grc.grcbox.common.resources.IfacesResource;
import es.upv.grc.grcbox.common.resources.RootResource;
import es.upv.grc.grcbox.common.resources.RuleResource;
import es.upv.grc.grcbox.common.resources.RulesResource;
import es.upv.grc.grcbox.common.resources.AppsResource.IdSecret;

public class MulticastTest {
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
	/*
	 * test the multicast support of the GRCBOx System.
	 * Arguments: Multicast Ip, Port, received packets before listen, sent packets.
	 */
	public static void main(String[] args) {
		Logger logger = Logger.getLogger("MulticastTest");
		FileHandler fh;
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
		clientResource = new ClientResource("http://grcbox:8080");
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
			if(grcBoxInterface.getName().equals("eth2")){
				iface = grcBoxInterface;
				break;
			}
		}
		
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
        myIdSecret = appsResource.newApp("MultiCastTestApp");
        appid = myIdSecret.getAppId();
        ChallengeResponse authentication = new ChallengeResponse(
    			ChallengeScheme.HTTP_BASIC, Integer.toString(myIdSecret.getAppId()), Integer.toString(myIdSecret.getSecret()).toCharArray());
    	clientResource.setChallengeResponse(authentication);
    	
    	keepAliveMonitor = scheduler.scheduleAtFixedRate(
    			sendKeepAlive, myIdSecret.getUpdatePeriod()/3, 
    			myIdSecret.getUpdatePeriod()/3, 
    			TimeUnit.MILLISECONDS);
    	
        long t2register = System.currentTimeMillis();
        int port = Integer.parseInt(args[1]);
        String dstAddr = args[0];
        logger.info("Register "+ t2register + " " + (t2register - t1register));
        /*
         * Register the multicast Rule
         */
		RulesResource rulesResource = clientResource.getChild("/apps/"+myIdSecret.getAppId()+"/rules", RulesResource.class);
		GrcBoxRule rule = new GrcBoxRule(-1, GrcBoxRule.Protocol.UDP, RuleType.MULTICAST, myIdSecret.getAppId(), iface.getName(), 0, -1, port, null, dstAddr, -1, null);
		rule = rulesResource.newRule(rule);
		ruleId = rule.getId();
		RuleResource ruleResource = clientResource.getChild("/apps/"+appid+"/rules/"+ruleId, RuleResource.class);
		int receive = Integer.parseInt(args[2]);
		int sent = Integer.parseInt(args[3]);
		try {
			MulticastSocket mSock = new MulticastSocket(4552);
			mSock.setNetworkInterface(NetworkInterface.getByName("eth0"));
			mSock.joinGroup(InetAddress.getByName(dstAddr));
			for(int i = 0; i < receive; i++){
				byte [] buf = new byte[2512];
				DatagramPacket p = new DatagramPacket(buf, buf.length);
				mSock.receive(p);
				logger.info("New Message received from " + p.getAddress().toString());
			}
			
			for(int i = 0; i < sent; i++){
				String payload = "Cosa";
				DatagramPacket p = new DatagramPacket(payload.getBytes(),payload.getBytes().length, InetAddress.getByName(dstAddr), port);
				mSock.send(p);
				Thread.sleep(1000);
			}
			mSock.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ruleResource.remove();
		keepAliveMonitor.cancel(false);
		System.exit(0);
	}
}
