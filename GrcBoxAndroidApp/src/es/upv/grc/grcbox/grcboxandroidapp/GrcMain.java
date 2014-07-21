package es.upv.grc.grcbox.grcboxandroidapp;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;



public class GrcMain extends Activity {

	private EditText url, port, app, loop;
	private RadioGroup iface;
	private CheckBox grcBox, application, rule, down;
	
	private String urlValue, appValue;
	private int portValue, loopValue;
	private boolean regApp, regRule, download;	
	
	private volatile boolean isProcessing = false;
	
	private volatile Test test = null;
	
	private UpdateProgress progress;
	
	private final int DEFAULT_INT = -1;
	private final int PORT_MIN = 0;
	private final int PORT_MAX = 65535;
	
	private final String USE_GRC_BOX = "USE_GRC_BOX";
	private final String CHOSEN_INTERFACE = "CHOSEN_INTERFACE";
	private final String APP_TEST = "APP_TEST";
	private final String RULE_TEST = "RULE_TEST";
	private final String DOWNLOAD_TEST = "DOWNLOAD_TEST";
	private final String URL = "URL";
	private final String PORT = "PORT";
	private final String FILENAME = "FILENAME";
	private final String LOOP = "LOOP";
	
	private final String EXTENTION = ".csv";
	
	public void reset()
	{
		urlValue =  appValue = null;
		portValue = loopValue = DEFAULT_INT;
		regApp = regRule = download = false;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_grc_main);
		url = (EditText)findViewById(R.id.URL);
		port = (EditText)findViewById(R.id.PORT);
		app = (EditText)findViewById(R.id.AppName);
		loop = (EditText)findViewById(R.id.Iteration);		
		iface = (RadioGroup)findViewById(R.id.radioIface);
		iface = (RadioGroup)findViewById(R.id.radioIface);
		application = (CheckBox)findViewById(R.id.App);
		rule = (CheckBox)findViewById(R.id.Rule);
		down = (CheckBox)findViewById(R.id.Download);
		grcBox = (CheckBox)findViewById(R.id.checkBoxGrcBox);
		isProcessing = false;
		test = null;
		progress = new UpdateProgress(this);
		reset();
		System.out.println("ON-Create");
	}	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.grc_main, menu);
		return true;
	}
		 
	public void progressReport(int percent)
	{
		progress.updateProgressText(percent);
		progress.updateProgressBar(percent);
	}
	
	public void startProcessing(View view)
	{
		System.out.println("Processing!!!!");
		if(isProcessing)
		{
			//kill all	
			if(test != null && test.getState() != Thread.State.TERMINATED)
			{				
				test.terminate();
			}
			reset();
			isProcessing = false;
			progressReport(100);
		}
		else
		{
			isProcessing = true;
			urlValue = url.getText().toString().trim();
			try
			{
				portValue = Integer.parseInt(port.getText().toString().trim());
				if(portValue < PORT_MIN || portValue > PORT_MAX)
				{
					Toast.makeText(getApplicationContext(), "Invalid PORT value!", Toast.LENGTH_SHORT).show();
					reset();
					return;
				}
			}
			catch(Exception e)
			{
				Toast.makeText(getApplicationContext(), "Check PORT value!", Toast.LENGTH_SHORT).show();
				reset();
				return;
			}
			appValue = app.getText().toString().trim();
			try
			{
				loopValue = Integer.parseInt(loop.getText().toString().trim());
				if(loopValue < 0)
				{
					Toast.makeText(getApplicationContext(), "Invalid Loop value!", Toast.LENGTH_SHORT).show();
					reset();
					return;
				}
			}
			catch(Exception e)
			{
				Toast.makeText(getApplicationContext(), "Check LOOP value!", Toast.LENGTH_SHORT).show();
				reset();
				return;
			}
		//	test for the kind of test chosen
			if(application.isChecked()||rule.isChecked()||down.isChecked())
			{
				if(application.isChecked())
				{
					regApp = true;
				}
				if(rule.isChecked())
				{
					regRule = true;
				}
				if(down.isChecked())
				{
					download = true;
				}
			}
			else
			{
				Toast.makeText(getApplicationContext(), "Choose atleast one test!", Toast.LENGTH_SHORT).show();
				reset();
				return;
			}
			if (grcBox.isChecked())
			{
				try{
						test = new Test(this, urlValue, portValue, appValue+"GRC"+System.currentTimeMillis()+EXTENTION, loopValue);
						test.start();
				}
				catch(Exception e)
				{
					Log.e("GRC MAin","Grc Box not communicable!"+e.toString());
					//Toast.makeText(getApplicationContext(), "GrcBox Server Unavailable!", Toast.LENGTH_SHORT).show();
					reset();
					return;
				}
			}
			else
			{
				//GrcBox not to be used				
				test = new Test(this, regApp, regRule, download, urlValue, portValue
						, appValue+System.currentTimeMillis()+EXTENTION, loopValue);
				test.start();
			}				
			try
			{
				test.join();
				isProcessing = false;
			}
			catch(InterruptedException interrupted)
			{
				test.terminate();
				isProcessing = false;
			}
			progressReport(100);
			reset();
		}		
	}
}