package es.upv.grc.grcbox.android.dialogs;

import es.upv.grc.grcbox.android.R;
import es.upv.grc.grcbox.common.GrcBoxSsid;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

public class ConnectDialog extends DialogFragment {
	private boolean security;
	private boolean configured;
	private String password;
	private boolean autoConnect;



	private GrcBoxSsid ssid;
	ConnectDialogListener mListener;
	public ConnectDialog(){
		
	}
	
	public ConnectDialog(GrcBoxSsid ssid){
		this.security = ssid.isSecurity();
		this.configured = ssid.isConfigured();
		this.ssid = ssid;
	}
	
	/* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface ConnectDialogListener {
        public void onDialogConnectClick(ConnectDialog dialog);
        public void onDialogCancelClick(ConnectDialog dialog);
        public void onDialogForgetClick(ConnectDialog dialog);
    }

	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		super.onCreateDialog(savedInstanceState);
		// Use the Builder class for convenient dialog construction
		if(savedInstanceState != null){
			security = savedInstanceState.getBoolean("security");
			configured = savedInstanceState.getBoolean("configured");
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		// Get the layout inflater
	    LayoutInflater inflater = getActivity().getLayoutInflater();
	    final View view = inflater.inflate(R.layout.dialog_connect, null);
		
	    
	    
		builder.setTitle("Connect")
			.setView(view)
			.setPositiveButton(android.R.string.ok, new OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which) {
					password = ((EditText)view.findViewById(R.id.editPassword)).getText().toString();
					autoConnect = ((CheckBox) view.findViewById(R.id.checkAutoConnect)).isActivated();
					mListener.onDialogConnectClick(ConnectDialog.this);
				}	
			})
			.setNegativeButton(android.R.string.cancel, new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					mListener.onDialogCancelClick(ConnectDialog.this);
				}
			});
		if(!security){
	    	view.findViewById(R.id.editPassword).setVisibility(View.INVISIBLE);
	    	view.findViewById(R.id.text_password).setVisibility(View.INVISIBLE);
	    }
	    
	    if(configured){
	    	view.findViewById(R.id.editPassword).setVisibility(View.INVISIBLE);
	    	view.findViewById(R.id.text_password).setVisibility(View.INVISIBLE);
	    	view.findViewById(R.id.checkAutoConnect).setVisibility(View.INVISIBLE);
	    	
	    	builder.setNeutralButton(R.string.action_forget, new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					mListener.onDialogForgetClick(ConnectDialog.this);
				}
			});
	    }
		
		// Create the AlertDialog object and return it
		return builder.create();
	}
	
	 // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the ConnectDialogListener so we can send events to the host
            mListener = (ConnectDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

	public GrcBoxSsid getSsid() {
		return ssid;
	}
	
	public String getPassword() {
		return password;
	}

	public boolean isAutoConnect() {
		return autoConnect;
	}

}
