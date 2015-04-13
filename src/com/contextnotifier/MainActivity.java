package com.contextnotifier;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.InputType;
import android.util.Log;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnDragListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class MainActivity extends Activity {
	AsyncTask<Void, Void, String> Go = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		/*EditText text = (EditText) findViewById(R.id.search);
		text.setRawInputType(InputType.TYPE_CLASS_TEXT);
		text.setImeOptions(EditorInfo.IME_ACTION_GO);
	    text.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
				if ((Go == null) || (Go.getStatus() == AsyncTask.Status.FINISHED)){
					Go = new DuckDuckGo(arg0.getText().toString());
					Go.execute();
				}
				return true;
			}
	    });*/
		setTitle("Weather Notifier");
		final SharedPreferences prefs = getSharedPreferences("ContextNotifier", 0);
		SeekBar mProgress = (SeekBar) findViewById(R.id.settings);
		mProgress.setMax(90);
		mProgress.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){
			int i = 1;
			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
				i = arg1;
			}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), "Weather will update every " + i + " minutes", Toast.LENGTH_SHORT).show();

				prefs.edit().putInt("interval", i * 60 * 1000).commit();

			}		
		});
        Button shake = (Button) findViewById(R.id.done);
        shake.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Log.v("Weather", "starting service");
			    Intent service = new Intent(getApplicationContext(), Monitor.class);
			    getApplicationContext().startService(service); 
			    finish();
			}
        	
        });
	}

}
