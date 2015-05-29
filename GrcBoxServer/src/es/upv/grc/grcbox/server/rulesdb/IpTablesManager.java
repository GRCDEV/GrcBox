package es.upv.grc.grcbox.server.rulesdb;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.LinkedList;
import java.util.List;


/**
 * The Class IpTablesManager.
 */
public class IpTablesManager {

	/**
	 * iptables-restore process reference
	 */
	private Process restoreProcess;
	
	/** The restore no flush process reference. */
	private Process restoreNoFlushProcess;
	
	/** The initialised. */
	private boolean initialised = false;

	/** The iptables restore command. */
	private final String IPTABLES_RESTORE = "iptables-restore";
	
	/** The iptables restore noflush command. */
	private final String IPTABLES_RESTORE_NOFLUSH = "iptables-restore -n";
	
	/** The Constant NAT_TABLE. */
	public final static String NAT_TABLE = "*nat";
	
	/** The Constant MANGLE_TABLE. */
	public final static String MANGLE_TABLE = "*mangle";
	
	/** The Constant COMMIT. */
	public final static String COMMIT = "COMMIT";
	

	
	/**
	 * Start the iptables-restore process and assign the outputStream and the 
	 * process
	 * @throws IOException Signals that an I/O exception has occurred.
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
	
	/**
	 * Write all the lines to iptables-restore
	 * Rules in the system are flushed
	 * COMMIT must be included
	 *
	 * @param lines the lines
	 * @param flush the flush
	 * @return true, if successful
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
	
	/**
	 * Commit a new nat rule without flushing
	 * @param line the line
	 */
	public void commitNatRule(String line){
		LinkedList<String> lines = new LinkedList<String>();
		lines.add(IpTablesManager.NAT_TABLE);
		lines.add(line);
		lines.add(IpTablesManager.COMMIT);
		commitLines(lines, false);
	}
	
	/**
	 * Commit a new mangle rule without flushing
	 * @param line the line
	 */
	public void commitMangleRule(String line){
		LinkedList<String> lines = new LinkedList<String>();
		lines.add(IpTablesManager.MANGLE_TABLE);
		lines.add(line);
		lines.add(IpTablesManager.COMMIT);
		commitLines(lines, false);
	}
	
	/**
	 * Flush all.
	 */
	public void flushAll(){
		LinkedList<String> lines = new LinkedList<String>();
		lines.add(NAT_TABLE);
		lines.add(COMMIT);
		lines.add(MANGLE_TABLE);
		lines.add(COMMIT);
		commitLines(lines, true);
	}
}
