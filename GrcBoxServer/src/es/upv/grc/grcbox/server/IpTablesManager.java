package es.upv.grc.grcbox.server;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.LinkedList;
import java.util.List;


public class IpTablesManager {

	/*
	 * iptables-restore process reference
	 */
	private Process restoreProcess;
	private Process restoreNoFlushProcess;
	
	private boolean initialised = false;

	
	private final String IPTABLES_RESTORE = "iptables-restore";
	private final String IPTABLES_RESTORE_NOFLUSH = "iptables-restore -n";
	
	public final static String NAT_TABLE = "*nat";
	public final static String MANGLE_TABLE = "*mangle";
	public final static String COMMIT = "COMMIT";
	

	
	/*
	 * Start the iptables-restore process and assign the outputStream and the 
	 * process
	 */
	public void initialise() throws IOException{
		try{
			restoreProcess = Runtime.getRuntime().exec(IPTABLES_RESTORE);
			restoreNoFlushProcess = Runtime.getRuntime().exec(IPTABLES_RESTORE_NOFLUSH);
			initialised = true;
		}
		catch (IOException e) {
			initialised = false;
			throw e;
		}
	}
	/*
	 * Write all the lines to iptables-restore
	 * COMMIT must be included
	 */
	public boolean commitLines(List<String> lines, boolean flush){
		if(initialised){
			 
			OutputStream outStream;
			if(flush){
				outStream =restoreProcess.getOutputStream(); 
			}
			else{
				outStream = restoreNoFlushProcess.getOutputStream();
			}
			BufferedWriter restorewriter = new BufferedWriter(new OutputStreamWriter
					(outStream));
			try {
				for (String line : lines) {
					restorewriter.write(line);
					restorewriter.newLine();
				}
				restorewriter.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return true;
		}
		else{
			return false;
		}
	}
	
	/*
	 * Write a new nat rule without flushing
	 */
	public void commitNatRule(String line){
		LinkedList<String> lines = new LinkedList<String>();
		lines.add(IpTablesManager.NAT_TABLE);
		lines.add(line);
		lines.add(IpTablesManager.COMMIT);
		commitLines(lines, false);
	}
	
	public void flushAll(){
		LinkedList<String> lines = new LinkedList<String>();
		lines.add(NAT_TABLE);
		lines.add(COMMIT);
		lines.add(MANGLE_TABLE);
		lines.add(COMMIT);
		commitLines(lines, true);
	}
}
