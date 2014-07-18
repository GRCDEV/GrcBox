package es.upv.grc.grcbox.grcboxandroidapp;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

public class FileManager {
	//use shared directory
	//save in filename: app name + timestamp
	//type of file: csv
	private Context appContext;
	
	public FileManager(Context appContext)
	{
		this.appContext = appContext;
	}

	public void writeToFile(String name, String fileContents)
	{
		File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File file = new File(path, name);
        try {
        	
            path.mkdirs();
            OutputStream out = new BufferedOutputStream(new FileOutputStream(file, true));
			out.write(fileContents.getBytes());
			out.close();
        } catch (IOException e) {
        	Log.e("File error", "File write failed");
        }
        Log.e("File Saved at ", path.getAbsolutePath());
    }
	
	public void readFile(String name)
	{
		File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/"+ name);
		String line;
        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(file));
            while ((line = in.readLine()) != null) Log.e("file content", line);

        } catch (FileNotFoundException e) {
            Log.e("File error", "File not found");
        } catch (IOException e) {
            Log.e("File error", "File not readable");
        } 
        file = null;
    }
	
	public void delete(String name)
	{
		File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/"+ name);
		try
		{
			file.delete();			
		}
		catch(Exception e)
		{			
		}
	}

}