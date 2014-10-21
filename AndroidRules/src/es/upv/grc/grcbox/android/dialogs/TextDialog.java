package es.upv.grc.grcbox.android.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.EditText;
/*
 * Ask the user to introduce a text, it has 2 buttons, cancel and OK
 */
public class TextDialog extends DialogFragment {
	String title = null;
	String question = null;
	TextDialogListener mListener = null;
	
	public TextDialog(){
		
	}
	
	public TextDialog(String title, String question) {
		super();
		this.title = title;
		this.question = question;
	}

	public interface TextDialogListener{
		public void onOkClick(String content);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		super.onCreateDialog(savedInstanceState);
		// Use the Builder class for convenient dialog construction
		if(savedInstanceState != null){
			title = savedInstanceState.getString("title");
			question = savedInstanceState.getString("question");
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		
		final EditText text = new EditText(getActivity());
		
		builder.setMessage(question)
		.setTitle(title)
		.setView(text)
		.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                mListener.onOkClick(text.getText().toString());
                dialog.dismiss();
            }
        })
		.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});
		// Create the AlertDialog object and return it
		return builder.create();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		outState.putString("title", title);
		outState.putString("question", question);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		 try {
	            // Instantiate the NoticeDialogListener so we can send events to the host
	            mListener = (TextDialogListener) getTargetFragment();
	        } catch (ClassCastException e) {
	            // The activity doesn't implement the interface, throw exception
	            throw new ClassCastException(activity.toString()
	                    + " must implement TextDialogListener");
	        }
	}
}
