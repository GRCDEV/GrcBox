package es.upv.grc.grcbox.android.activities;


import java.util.HashMap;
import java.util.LinkedList;


import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import es.upv.grc.grcbox.android.R;
import es.upv.grc.grcbox.android.fragments.AppsFragment;
import es.upv.grc.grcbox.android.fragments.IfacesFragment;
import es.upv.grc.grcbox.android.fragments.RulesFragment;
import es.upv.grc.grcbox.androlib.GrcBoxClientService;
import es.upv.grc.grcbox.androlib.GrcBoxClientService.GrcBoxBinder;
import es.upv.grc.grcbox.androlib.GrcBoxClientService.OnRegisteredChangedListener;
import es.upv.grc.grcbox.common.GrcBoxRule;
import es.upv.grc.grcbox.common.GrcBoxRule.Protocol;
import es.upv.grc.grcbox.common.GrcBoxRule.RuleType;

@SuppressLint("UseSparseArrays")
public class MainActivity extends Activity implements ActionBar.TabListener, OnRegisteredChangedListener{
	private static final String RULES_TAG= "rules";
	private static final String APPS_TAG= "apps";
	private static final String IFACES_TAG = "ifaces";
	private static final String TAB_SELECTED = "tabKey";
	private static final String PARAM_MAP = "ruleIdMap";
	private static final int NEW_RULE_ACTION = 0; 
	private static final int EDIT_RULE_ACTION = 1; 

	private RulesFragment rulesFragment = null;
	private AppsFragment appsFragment = null;
	private IfacesFragment ifacesFragment = null;
	
	private GrcBoxClientService grcBoxCli = null;
	private boolean mBound = false;
	private Menu mMenu;
	private String selectedTab;
	
	private LinkedList<GrcBoxRule>  pendingRules = new LinkedList<GrcBoxRule>();
	
    @SuppressWarnings("unchecked")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayShowTitleEnabled(false);
		
        appsFragment = new AppsFragment();
        rulesFragment = new RulesFragment();
        ifacesFragment = new IfacesFragment();
        
        Tab tab = actionBar.newTab()
        		.setText(R.string.rules)
        		.setTabListener(this)
        		.setTag(RULES_TAG);
        actionBar.addTab(tab);

        tab = actionBar.newTab()
        		.setText(R.string.apps)
        		.setTabListener(this)
        		.setTag(APPS_TAG);
        actionBar.addTab(tab);
        
        tab = actionBar.newTab()
        		.setText(R.string.interfaces)
        		.setTabListener(this)
        		.setTag(IFACES_TAG);
        actionBar.addTab(tab);
        
        if(savedInstanceState != null){
        	getActionBar().setSelectedNavigationItem(savedInstanceState.getInt(TAB_SELECTED));
        	rulesFragment.setIdNameMap((HashMap<Integer, String>) savedInstanceState.getSerializable(PARAM_MAP));
        }
        else{
        	rulesFragment.setIdNameMap(new HashMap<Integer, String>());
        }
        
        Intent intent = new Intent(this, GrcBoxClientService.class);
       	startService(intent);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
	protected void onDestroy() {
    	grcBoxCli.unSubscribeRegisteredChangedListener(this);
    	unbindService(mConnection);
		super.onDestroy();
	}



	public boolean isBound(){
    	return mBound;
    }
    
    public GrcBoxClientService getService(){
    	return grcBoxCli;
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
    		
    		if(!grcBoxCli.isRegistered()){
    			grcBoxCli.register(getResources().getString(R.string.app_name));
    		}
			for(GrcBoxRule rule: pendingRules){
				new NewRuleTask("").execute(rule);
			}
			pendingRules.clear();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    @Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable(PARAM_MAP, rulesFragment.getIdNameMap());
		outState.putInt(TAB_SELECTED, getActionBar().getSelectedNavigationIndex());
	}


	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        mMenu = menu;
        if(selectedTab == APPS_TAG){
			if(mMenu != null){
				MenuItem addButton = mMenu.findItem(R.id.action_add);
				addButton.setVisible(false);
				MenuItem loadButton = mMenu.findItem(R.id.action_load_profile);
				loadButton.setVisible(false);
			}
		}
		else if(selectedTab == RULES_TAG){
			if(mMenu != null){
				MenuItem addButton = mMenu.findItem(R.id.action_add);
				addButton.setVisible(true);
				MenuItem loadButton = mMenu.findItem(R.id.action_load_profile);
				loadButton.setVisible(true);
			}
		}
		else if(selectedTab == IFACES_TAG){
			if(mMenu != null){
				MenuItem addButton = mMenu.findItem(R.id.action_add);
				addButton.setVisible(false);
				MenuItem loadButton = mMenu.findItem(R.id.action_load_profile);
				loadButton.setVisible(false);
			}
		}
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_add) {
        	if(grcBoxCli.isRegistered()){
        		Intent intent = new Intent(this, NewRule.class);
        		startActivityForResult(intent, NEW_RULE_ACTION);
        	}
        	else{
    			Toast.makeText(MainActivity.this, "You are not connected to a GRCBOX you can't create new rules", Toast.LENGTH_SHORT).show();
        	}
        }
        else if(id == R.id.action_refresh){
        	refresh();
        }
        else if(id == R.id.action_close){
        	
            final Intent intent = new Intent(this, GrcBoxClientService.class);
            new DeRegisterTask().execute();
            stopService(intent);
        	finish();
        }
        return super.onOptionsItemSelected(item);
    }
    

    public class DeRegisterTask extends AsyncTask<Void, Void, Void>{

		@Override
		protected Void doInBackground(Void... arg0) {
			grcBoxCli.deregister();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
		}
    }
    
	private void refresh() {
        if(selectedTab == RULES_TAG){
    		rulesFragment.refresh();
    	}
    	if(selectedTab == IFACES_TAG){
    		ifacesFragment.refresh();
    	}
    	if(selectedTab == APPS_TAG){
    		appsFragment.refresh();
    	}
	}



	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == RESULT_CANCELED){
			return;
		}
		Bundle bundle = null;
		bundle = data.getExtras();
		String name = null;
		RuleType type = null;
		Protocol proto = null;
		String addr = null;
		int port = 0;
		String plugin = null;
		String iface = null;
		int id = 0;
		if(requestCode == NEW_RULE_ACTION || requestCode == EDIT_RULE_ACTION){
			name = bundle.getString(NewRule.PARAM_NAME);
			type = RuleType.values()[bundle.getInt(NewRule.PARAM_TYPE)];
			proto = Protocol.values()[bundle.getInt(NewRule.PARAM_PROTO)];
			addr = bundle.getString(NewRule.PARAM_ADDR);
			port = bundle.getInt(NewRule.PARAM_PORT);
			plugin = bundle.getString(NewRule.PARAM_PLUGIN);
			iface = bundle.getString(NewRule.PARAM_IFACE);
			id=0;
		}
		if(requestCode == EDIT_RULE_ACTION){
			id = bundle.getInt(NewRule.PARAM_ID);
		}
		

		GrcBoxRule rule = null;
		if(type.equals(RuleType.MULTICAST)){
			rule = new GrcBoxRule(id, proto, type, 0, iface, 0, -1, port, null, addr, -1, null, plugin);
		}
		else if(type.equals(RuleType.INCOMING)){
			rule = new GrcBoxRule(id, proto, type, 0, iface, 0, -1, port, null, null, port, null);
		}
		else if(type.equals(RuleType.OUTGOING)){
			rule = new GrcBoxRule(id, proto, type, 0, iface, 0, -1, port, null, addr, -1, null);
		}
		
		new NewRuleTask(name).execute(rule);
	}

	public class NewRuleTask extends AsyncTask<GrcBoxRule, Void, GrcBoxRule>{
		String name;
		public NewRuleTask(String name) {
			super();
			this.name = name;
		}

		@Override
		protected GrcBoxRule doInBackground(GrcBoxRule... rule) {
			GrcBoxRule registeredRule = null;
			if(isBound() && grcBoxCli.isRegistered()){
				 registeredRule = grcBoxCli.registerNewRule(rule[0]);
			}
			else{
				pendingRules.add(rule[0]);
			}
			return registeredRule;
		}

		@Override
		protected void onPostExecute(GrcBoxRule result) {
			if(result != null){
				rulesFragment.addRule(result.getId(), name);
				rulesFragment.refresh();
				Toast.makeText(MainActivity.this, "A new rule has been created", Toast.LENGTH_SHORT).show();
			}
			super.onPostExecute(result);
		}
    }
	
	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		if(tab.getTag() == APPS_TAG){
			if(mMenu != null){
				MenuItem addButton = mMenu.findItem(R.id.action_add);
				addButton.setVisible(false);
				MenuItem loadButton = mMenu.findItem(R.id.action_load_profile);
				loadButton.setVisible(false);
			}
			ft.replace(android.R.id.content,appsFragment);
			selectedTab = APPS_TAG;
		}
		else if(tab.getTag() == RULES_TAG){
			if(mMenu != null){
				MenuItem addButton = mMenu.findItem(R.id.action_add);
				addButton.setVisible(true);
				MenuItem loadButton = mMenu.findItem(R.id.action_load_profile);
				loadButton.setVisible(true);
			}
			ft.replace(android.R.id.content,rulesFragment);
			selectedTab = RULES_TAG;
		}
		else if(tab.getTag() == IFACES_TAG){
			if(mMenu != null){
				MenuItem addButton = mMenu.findItem(R.id.action_add);
				addButton.setVisible(false);
				MenuItem loadButton = mMenu.findItem(R.id.action_load_profile);
				loadButton.setVisible(false);
			}
			ft.replace(android.R.id.content, ifacesFragment);
			selectedTab = IFACES_TAG;
		}
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
	}

	@Override
	public void onRegisteredChanged(boolean newValue) {
		if(newValue){
			refresh();
		}
	}
}
