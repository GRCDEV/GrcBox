package es.upv.grc.grcbox.android.fragments;

import java.util.ArrayList;
import java.util.Collection;

import es.upv.grc.grcbox.android.activities.MainActivity;
import es.upv.grc.grcbox.androlib.GrcBoxClientService;
import es.upv.grc.grcbox.common.GrcBoxApp;

import android.app.ListFragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ArrayAdapter;

public class AppsFragment extends ListFragment {
	ArrayAdapter<GrcBoxApp> mAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public void onStart() {
		ArrayList<GrcBoxApp> strings = new ArrayList<GrcBoxApp>();
		mAdapter = new ArrayAdapter<GrcBoxApp>(getActivity(), android.R.layout.simple_list_item_1, strings);
		setListAdapter(mAdapter);
		refresh();
		super.onStart();
	}
	
	private class AppsLoader extends AsyncTask<Void , Void, Collection<GrcBoxApp>>{
		@Override
		protected Collection<GrcBoxApp> doInBackground(Void... params) {
			MainActivity mActivity = (MainActivity)getActivity();
			if(mActivity.isBound()){
				GrcBoxClientService service = mActivity.getService();
				if(service.isRegistered()){
					return service.getApps();
				}
			}
			return new ArrayList<GrcBoxApp>();
			
		}

		@Override
		protected void onPostExecute(Collection<GrcBoxApp> result) {
			super.onPostExecute(result);
			mAdapter.clear();
			mAdapter.addAll(result);
		}
	}
	
	public void refresh(){
		new AppsLoader().execute();
	}
}
