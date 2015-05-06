package es.upv.grc.grcbox.android.fragments;

import java.util.ArrayList;
import java.util.Collection;

import es.upv.grc.grcbox.android.activities.MainActivity;
import es.upv.grc.grcbox.androlib.GrcBoxClientService;
import es.upv.grc.grcbox.common.GrcBoxInterface;

import android.app.ListFragment;
import android.os.AsyncTask;
import android.os.Bundle;
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
				if(service.isRegistered()){
					return service.getInterfaces();
				}
			}
			return new ArrayList<GrcBoxInterface>();
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
