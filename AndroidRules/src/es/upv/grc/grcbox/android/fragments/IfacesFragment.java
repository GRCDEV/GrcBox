package es.upv.grc.grcbox.android.fragments;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import es.upv.grc.grcbox.android.activities.MainActivity;
import es.upv.grc.grcbox.androlib.GrcBoxClientService;
import es.upv.grc.grcbox.androlib.GrcBoxClientService.GrcBoxBinder;
import es.upv.grc.grcbox.common.GrcBoxInterface;

import android.app.ListFragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.AsyncTaskLoader;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.ArrayAdapter;

public class IfacesFragment extends ListFragment {
	ArrayAdapter<GrcBoxInterface> mAdapter;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	
	
	@Override
	public void onStart() {
		ArrayList<GrcBoxInterface> strings = new ArrayList<GrcBoxInterface>();
		mAdapter = new ArrayAdapter<GrcBoxInterface>(getActivity(), android.R.layout.simple_list_item_1, strings);
		setListAdapter(mAdapter);
		refresh();
		super.onStart();
	}

	private class InterfacesLoader extends AsyncTask<Void , Void, Collection<GrcBoxInterface>>{
		@Override
		protected Collection<GrcBoxInterface> doInBackground(Void... params) {
			MainActivity mActivity = (MainActivity)getActivity();
			if(mActivity.isBound()){
				GrcBoxClientService service = mActivity.getService();
				return service.getInterfaces();
			}
			else{
				return new ArrayList<GrcBoxInterface>();
			}
		}

		@Override
		protected void onPostExecute(Collection<GrcBoxInterface> result) {
			super.onPostExecute(result);
			mAdapter.clear();
			mAdapter.addAll(result);
		}
	}
	
	public void refresh(){
		new InterfacesLoader().execute();
	}
}
