package es.upv.grc.grcbox.android.fragments;

import java.util.ArrayList;
import java.util.Collection;

import es.upv.grc.grcbox.android.activities.ApListActivity;
import es.upv.grc.grcbox.android.activities.MainActivity;
import es.upv.grc.grcbox.android.activities.NewRule;
import es.upv.grc.grcbox.androlib.GrcBoxClientService;
import es.upv.grc.grcbox.common.GrcBoxInterface;
import android.app.ListFragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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



	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		GrcBoxInterface grcBoxIface = (GrcBoxInterface) mAdapter.getItem(position);
		Intent intent = new Intent(this.getActivity(), ApListActivity.class);
		intent.putExtra(ApListActivity.IFACE_NAME, grcBoxIface.getName());
		startActivity(intent);
		super.onListItemClick(l, v, position, id);
	}
}
