package es.upv.grc.grcbox.grcboxandroidapp;

public class SaveDataFormat {
	
	private int obsNo;
	private long regAppTime;
	private long regRuleTime;
	private long downloadTime;
	private long deRegRuleTime;
	private long deRegAppTime;
	private long totalCycleTime;
	
	private long interfaceInfo;
	private long serverStatus;
	
	public static final int DEFAULT_VALUE = -1;
	
	public static final String DELIM = ",";
	
	public static final String EOL = "\n"; 
	
	public SaveDataFormat(int obsNo)
	{
		this.obsNo = obsNo;
		regAppTime = regRuleTime = downloadTime = deRegRuleTime = deRegAppTime = totalCycleTime = DEFAULT_VALUE;
		interfaceInfo = serverStatus = DEFAULT_VALUE;
	}

	public void setRegisterAppTime(long value)
	{
		regAppTime = value;
	}
	
	public void setRegisterRuleTime(long value)
	{
		regRuleTime = value;
	}
	
	public void setDownloadTime(long value)
	{
		downloadTime = value;
	}
	
	public void setDeRegisterRuleTime(long value)
	{
		deRegRuleTime = value;
	}
	
	public void setDeRegisterAppTime(long value)
	{
		deRegAppTime = value;
	}
	
	public void totalCycleTime(long value)
	{
		totalCycleTime = value;
	}
	
	public void setInterfaceInfoTime(long value)
	{
		interfaceInfo = value;
	}
	
	public void setServerStatusTime(long value)
	{
		serverStatus = value;
	}
	
	public String generateFormatedOuput()
	{
		String opt = String.valueOf(obsNo) + DELIM + String.valueOf(serverStatus) + DELIM + String.valueOf(interfaceInfo) 
				+ DELIM + String.valueOf(regAppTime) + DELIM + String.valueOf(regRuleTime)
				 + DELIM + String.valueOf(downloadTime) + DELIM + String.valueOf(deRegRuleTime) + DELIM + String.valueOf(deRegAppTime)
				  + DELIM + String.valueOf(totalCycleTime) + EOL;
		return opt;
	}
}