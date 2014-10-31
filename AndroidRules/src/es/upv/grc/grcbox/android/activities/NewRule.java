package es.upv.grc.grcbox.android.activities;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;

import es.upv.grc.grcbox.android.R;
import es.upv.grc.grcbox.androlib.GrcBoxClientService;
import es.upv.grc.grcbox.androlib.GrcBoxClientService.GrcBoxBinder;
import es.upv.grc.grcbox.common.GrcBoxInterface;
import es.upv.grc.grcbox.common.GrcBoxRule.Protocol;
import es.upv.grc.grcbox.common.GrcBoxRule.RuleType;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class NewRule extends Activity implements OnItemSelectedListener {
	
	public final static String PARAM_NAME = "name";
	public final static String PARAM_TYPE = "type";
	public final static String PARAM_PROTO = "proto";
	public final static String PARAM_ADDR = "address";
	public final static String PARAM_PORT = "port";
	public final static String PARAM_PLUGIN = "plugin";
	public final static String PARAM_IFACE = "iface";
	public final static String PARAM_ID = "id";
	private final static String PARAM_IFACES = "ifaces";
	private final static String PARAM_PLUGINS = "plugins";
	
	
	private ArrayAdapter<String> ifaceAdapter;
	private ArrayAdapter<String> pluginAdapter;
	private String[] ifacesList;
	private String[] pluginsList;
	private Collection<GrcBoxInterface> grcBoxInterfaces;
	private GrcBoxClientService grcBoxCli;
	private boolean mBound;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_rule);
		Spinner protoSpinner = (Spinner) findViewById(R.id.spinner_proto);
		ArrayList<String> protocols = new ArrayList<String>();
		for (Protocol proto : Protocol.values()) {
			protocols.add(proto.toString());
		}
		ArrayAdapter<String> adapter= new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, protocols);
		protoSpinner.setAdapter(adapter);
		
		Spinner typeSpinner = (Spinner) findViewById(R.id.spinner_type);
		ArrayList<String> types = new ArrayList<String>();
		for (RuleType type : RuleType.values()) {
			types.add(type.toString());
		}
		ArrayAdapter<String> typeAdapter= new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, types);
		typeSpinner.setAdapter(typeAdapter);
		typeSpinner.setOnItemSelectedListener(this);
		
		Spinner ifaceSpinner = (Spinner) findViewById(R.id.spinner_iface);
		ifaceAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
		ifaceSpinner.setAdapter(ifaceAdapter);
		ifaceSpinner.setOnItemSelectedListener(this);
		
		Spinner pluginSpinner = (Spinner) findViewById(R.id.spinner_plugin);
		pluginAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
		pluginSpinner.setAdapter(pluginAdapter);
		pluginSpinner.setOnItemSelectedListener(this);
		
        Intent intent = new Intent(this, GrcBoxClientService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
	}
	
    @Override
	protected void onSaveInstanceState(Bundle outState) {
    	super.onSaveInstanceState(outState);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unbindService(mConnection);
	}



	/** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {
    	
		@Override
        public void onServiceConnected(ComponentName className,
                IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            GrcBoxBinder binder = (GrcBoxBinder) service;
            grcBoxCli = binder.getService();
            mBound = true;
    		new InterfacesLoader().execute();
    		new PluginsLoader().execute();
        }
        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };
	
	private class InterfacesLoader extends AsyncTask<Void , Void, Collection<String>>{
		@Override
		protected Collection<String> doInBackground(Void... params) {
			ArrayList<String> ifacesList = new ArrayList<String>();
			if(mBound){
				Collection<GrcBoxInterface> ifaces = grcBoxCli.getInterfaces();
				for (GrcBoxInterface grcBoxInterface : ifaces) {
					ifacesList.add(grcBoxInterface.getName());
				}
				grcBoxInterfaces = ifaces;
			}
			return ifacesList;
		}

		@Override
		protected void onPostExecute(Collection<String> result) {
			super.onPostExecute(result);
			ifaceAdapter.clear();
			ifaceAdapter.addAll(result);
		}
	}
	
	private class PluginsLoader extends AsyncTask<Void , Void, Collection<String>>{
		@Override
		protected Collection<String> doInBackground(Void... params) {
			ArrayList<String> pluginList = new ArrayList<String>();
			if(mBound){
				Collection<String> plugins = grcBoxCli.getMulticastPlugins();
				pluginList.addAll(plugins);
			}
			return pluginList;
		}

		@Override
		protected void onPostExecute(Collection<String> result) {
			super.onPostExecute(result);
			pluginAdapter.clear();
			pluginAdapter.addAll(result);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.new_rule, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onItemSelected(AdapterView<?> view, View arg1, int arg2,
			long arg3) {
		/*
		 * Modify visible elements according to the type selected
		 */
		if(view == findViewById(R.id.spinner_type)){
			RuleType rule = RuleType.values()[(int) arg3];
			if(rule == RuleType.MULTICAST){
				findViewById(R.id.rowPlugin).setVisibility(View.VISIBLE);
				findViewById(R.id.rowAddress).setVisibility(View.VISIBLE);
				((Spinner)findViewById(R.id.spinner_proto)).setSelection(Protocol.UDP.ordinal());
			} 
			else if (rule == RuleType.INCOMING){
				findViewById(R.id.rowPlugin).setVisibility(View.GONE);
				findViewById(R.id.rowAddress).setVisibility(View.GONE);
				((Spinner)findViewById(R.id.spinner_proto)).setSelection(Protocol.TCP.ordinal());
			}
			else if (rule == RuleType.OUTGOING){
				findViewById(R.id.rowPlugin).setVisibility(View.GONE);
				findViewById(R.id.rowAddress).setVisibility(View.VISIBLE);
				((Spinner)findViewById(R.id.spinner_proto)).setSelection(Protocol.TCP.ordinal());
			}
		}
		else if(view == findViewById(R.id.spinner_iface)){
			String ifaceName = (String) view.getAdapter().getItem((int)arg3);
			TextView ifaceStatus = (TextView) findViewById(R.id.text_iface_status);
			GrcBoxInterface grcBoxIface = null;
			for (GrcBoxInterface ifaceObject  : grcBoxInterfaces) {
				if(ifaceObject.getName().equals(ifaceName)){
					grcBoxIface = ifaceObject;
					break;
				}
			}
			ifaceStatus.setText(grcBoxIface.toString());
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
	}
	
	/*
	 * Called when the button is pressed
	 */
	public void saveAndReturn(View view){
		Bundle resultBundle = new Bundle();
		Intent resultIntent = new Intent();

		String name = null;
		EditText textName = (EditText)findViewById(R.id.rule_name);
		if(!textName.getText().toString().equals("")){
			name = textName.getText().toString();
			resultIntent.putExtra(PARAM_NAME, name);
		}
		else{
			Toast.makeText(this, "The name of the rule is empty!", Toast.LENGTH_SHORT).show();
			return;
		}
		
		RuleType ruleType = null;
		Spinner spinnerType = (Spinner)findViewById(R.id.spinner_type);
		ruleType = RuleType.values()[spinnerType.getSelectedItemPosition()];
		resultIntent.putExtra(PARAM_TYPE, ruleType.ordinal());
		
		Protocol proto = null;
		Spinner spinnerProto = (Spinner)findViewById(R.id.spinner_proto);
		proto = Protocol.values()[(int) spinnerProto.getSelectedItemPosition()];
		resultIntent.putExtra(PARAM_PROTO, proto.ordinal());
		
		int portNum = 0;
		EditText textPort = (EditText)findViewById(R.id.port_number);
		try{
			portNum = Integer.parseInt(textPort.getText().toString());
			if(portNum == 0){
				throw new NumberFormatException();
			}
			resultIntent.putExtra(PARAM_PORT, portNum);
		}
		catch(NumberFormatException e){
            Toast.makeText(this, "The port number must be a number!!", Toast.LENGTH_SHORT).show();
            return;
		}
		
		String remoteAddr = null;
		EditText textAddr = (EditText)findViewById(R.id.remote_address);
		remoteAddr = textAddr.getText().toString();
		if(!remoteAddr.equals("ANY") && !remoteAddr.equals("")){
			try{
				InetAddress addr = InetAddress.getByName(remoteAddr);
				resultIntent.putExtra(PARAM_ADDR, remoteAddr);
			}
			catch(UnknownHostException e){
				Toast.makeText(this, "the remote address must be a valid IP or a host name", Toast.LENGTH_SHORT).show();
				return;
			}
		}
		
		String plugin = null;
		Spinner spinnerPlugin = (Spinner)findViewById(R.id.spinner_plugin);
		plugin = (String)spinnerPlugin.getSelectedItem();
		resultIntent.putExtra(PARAM_PLUGIN, plugin);
		
		String iface = null;
		Spinner spinnerIface = (Spinner)findViewById(R.id.spinner_iface);
		iface = (String)spinnerIface.getSelectedItem();
		resultIntent.putExtra(PARAM_IFACE, iface);
		
		
		setResult(RESULT_OK, resultIntent);
		finish();
	}
}
