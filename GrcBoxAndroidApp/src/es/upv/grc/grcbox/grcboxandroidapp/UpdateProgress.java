package es.upv.grc.grcbox.grcboxandroidapp;

import android.app.Activity;
import android.widget.ProgressBar;
import android.widget.TextView;

public class UpdateProgress {
	
	ProgressBar progress;
	TextView tview;
	
	public UpdateProgress(Activity activity)
	{
		progress = (ProgressBar)activity.findViewById(R.id.progressBar);
		tview = (TextView)activity.findViewById(R.id.message);
	}
	
	public void updateProgressText(int percent)
	{
		tview.setText("*Progress: " + percent + "% Completed");
	}
	
	public void updateProgressBar(int value)
	{
		progress.setProgress(value);
	}
}