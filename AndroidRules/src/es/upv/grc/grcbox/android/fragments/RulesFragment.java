package es.upv.grc.grcbox.android.fragments;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import es.upv.grc.grcbox.android.R;
import es.upv.grc.grcbox.android.activities.DisplayGrcBoxRule;
import es.upv.grc.grcbox.android.activities.MainActivity;
import es.upv.grc.grcbox.android.dialogs.TextDialog;
import es.upv.grc.grcbox.android.dialogs.TextDialog.TextDialogListener;
import es.upv.grc.grcbox.androlib.GrcBoxClientService;
import es.upv.grc.grcbox.common.GrcBoxRule;

import android.annotation.SuppressLint;
import android.app.ListFragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

/*
 * TODO Show the ActionMode Bar when an Item is selected :S
 */
public class RulesFragment extends ListFragment implements MultiChoiceModeListener, TextDialogListener{
	private static final int NEW_PROFILE = 0;
	private ListView myListView;
	private ActionMode mode = null;
	private ArrayAdapter<DisplayGrcBoxRule> mAdapter;
	@SuppressLint("UseSparseArrays")
	private HashMap<Integer, String> ruleIdName;
	
	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	
	@Override
	public void onStart() {
		ArrayList<DisplayGrcBoxRule> strings = new ArrayList<DisplayGrcBoxRule>();
		mAdapter = new ArrayAdapter<DisplayGrcBoxRule>(getActivity(), android.R.layout.simple_list_item_1, strings);
		setListAdapter(mAdapter);
		refresh();
		super.onStart();
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		this.myListView = this.getListView();
		this.myListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
		this.myListView.setMultiChoiceModeListener(this);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		registerForContextMenu(getListView());
	}
	
	@Override
	public boolean onCreateActionMode(ActionMode mode, Menu menu) {
		MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.rule_context, menu);
		return true;
	}
	
	@Override
	public boolean onActionItemClicked(ActionMode arg0, MenuItem item) {
		if(item.getItemId() == R.id.action_save_profile){
			TextDialog dialog = new TextDialog("Name", "Write the name of the profile");
			dialog.setTargetFragment(this, NEW_PROFILE);
			dialog.show(getActivity().getFragmentManager(), "text");
		}
		else if( item.getItemId() == R.id.action_remove){
			SparseBooleanArray selectedItems = getListView().getCheckedItemPositions();
			for (int i = 0; i < selectedItems.size(); i++) {
				int key = selectedItems.keyAt(i);
				if(selectedItems.get(key)){
					GrcBoxRule rule = mAdapter.getItem(key).getRule();
					Toast.makeText(getActivity(), "Rule removed "+ rule, Toast.LENGTH_SHORT).show();
					new RemoveRuleTask().execute(rule);
				}
			}
		}
		mode = arg0;
		return true;
	}
	
	private class RemoveRuleTask extends AsyncTask<GrcBoxRule , Void, Void>{

		@Override
		protected Void doInBackground(GrcBoxRule... params) {
			MainActivity mActivity = (MainActivity)getActivity();
			if(mActivity.isBound()){
				GrcBoxClientService service = mActivity.getService();
				if(service.isRegistered()){
					service.removeRule(params[0]);
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			mode.finish();
			refresh();
		}
	}
	
	@Override
	public void onDestroyActionMode(ActionMode arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public boolean onPrepareActionMode(ActionMode arg0, Menu arg1) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public void onItemCheckedStateChanged(ActionMode arg0, int arg1, long arg2,
			boolean arg3) {
		Toast.makeText(getActivity(), "Item has been selected "+ arg1 + " " + arg2 + " " + arg3, Toast.LENGTH_SHORT).show();
	}

	/*
	 * Save selected rules with the provided name
	 * TODO
	 * @see es.upv.grc.grcbox.android.dialogs.TextDialog.TextDialogListener#onOkClick(java.lang.String)
	 */
	@Override
	public void onOkClick(String content) {
		Toast.makeText(getActivity(), "New Profile "+ content, Toast.LENGTH_SHORT).show();
		mode.finish();
	}
	
	private class RulesLoader extends AsyncTask<Void , Void, Collection<DisplayGrcBoxRule>>{
		@Override
		protected Collection<DisplayGrcBoxRule> doInBackground(Void... params) {
			MainActivity mActivity = (MainActivity)getActivity();
			ArrayList<DisplayGrcBoxRule> displayList = new ArrayList<DisplayGrcBoxRule>();
			
			if(mActivity == null){
				return displayList;
			}
			if(mActivity.isBound()){
				GrcBoxClientService service = mActivity.getService();
				if(service.isRegistered()){
					Collection<GrcBoxRule> rules = service.getRules();
					for (GrcBoxRule grcBoxRule : rules) {
						displayList.add(new DisplayGrcBoxRule(grcBoxRule, ruleIdName.get( grcBoxRule.getId()) ) );
					}
				}
				return displayList;
			}
			else{
				return new ArrayList<DisplayGrcBoxRule>();
			}

		}

		@Override
		protected void onPostExecute(Collection<DisplayGrcBoxRule> result) {
			super.onPostExecute(result);
			mAdapter.clear();
			mAdapter.addAll(result);
		}
	}
	
	public void refresh(){
		new RulesLoader().execute();
	}
	
	public HashMap<Integer, String> getIdNameMap(){
		return ruleIdName;
	}
	
	public void setIdNameMap(HashMap<Integer, String> map){
		ruleIdName = map;
	}
	
	public void addRule(int id, String name){
		ruleIdName.put(id, name);
	}
	
	public void rmRule(int id){
		ruleIdName.remove(id);
	}
}
