package es.upv.grc.grcbox.android.activities;

import java.util.ArrayList;
import java.util.Collection;

import es.upv.grc.grcbox.android.R;
import es.upv.grc.grcbox.android.dialogs.ConnectDialog;
import es.upv.grc.grcbox.android.dialogs.ConnectDialog.ConnectDialogListener;
import es.upv.grc.grcbox.androlib.GrcBoxClientService;
import es.upv.grc.grcbox.androlib.GrcBoxClientService.GrcBoxBinder;
import es.upv.grc.grcbox.common.GrcBoxSsid;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ApListActivity extends Activity implements ConnectDialogListener{
	public static final String IFACE_NAME = "ifaceName";
	public static final int CONNECT = 0;
	private ArrayAdapter<GrcBoxSsid> mAdapter;
	private String iface;
	private GrcBoxClientService grcBoxCli;
	private boolean mBound;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ap_list);
	}
	
	@Override
	public void onStart() {
		mAdapter = new ArrayAdapter<GrcBoxSsid>(this, android.R.layout.simple_list_item_1);
		ListView apList = (ListView)findViewById(R.id.ApList);
		apList.setAdapter(mAdapter);
		
		apList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long id) {
				GrcBoxSsid ssid = mAdapter.getItem(position);
				ssidSelected(ssid);				
			}
		});
		
		iface = getIntent().getExtras().getString(IFACE_NAME);
		Intent intent = new Intent(this, GrcBoxClientService.class);
		bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
		super.onStart();
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
            new ApsLoader().execute(iface);
        }
        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };
	
	private class ApsLoader extends AsyncTask<String , Void, Collection<GrcBoxSsid>>{
		@Override
		protected Collection<GrcBoxSsid> doInBackground(String... params) {
			String iface = params[0];
			if(mBound){
				if(grcBoxCli.isRegistered()){
					return grcBoxCli.getAps(iface);
				}
			}
			return new ArrayList<GrcBoxSsid>();
		}

		@Override
		protected void onPostExecute(Collection<GrcBoxSsid> result) {
			super.onPostExecute(result);
			mAdapter.clear();
			mAdapter.addAll(result);
		}
	}
	
	private void ssidSelected(GrcBoxSsid ssid) {
		ConnectDialog conDialog = new ConnectDialog(ssid);
		conDialog.show(getFragmentManager(), "Connect");
	}

	@Override
	public void onDialogConnectClick(ConnectDialog dialog) {
			GrcBoxSsid ssid = dialog.getSsid();
			String password = null;
			if(ssid.isSecurity()){
				password = dialog.getPassword();
			}
			boolean auto = false;
			if(!ssid.isConfigured()){
				 auto = dialog.isAutoConnect();
			}
			new ApConnector().execute(iface, ssid.getSsid(), password, Boolean.toString(auto));
	}

	private class ApConnector extends AsyncTask<String , Void, Void>{
		@Override
		protected Void doInBackground(String... params) {
			String iface = params[0];
			String ssid = params[1];
			String password = params[2];
			String auto = params[3];
			boolean autoBool = Boolean.getBoolean(auto);
			if(mBound){
				if(grcBoxCli.isRegistered()){
					grcBoxCli.connectAp( iface, ssid, password, autoBool);
				}
			}
			return null;
		}
	}
	
	@Override
	public void onDialogCancelClick(ConnectDialog dialog) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDialogForgetClick(ConnectDialog dialog) {
		// TODO Auto-generated method stub
		
	}
}
