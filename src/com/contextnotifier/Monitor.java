package com.contextnotifier;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

public class Monitor extends Service {

	NotificationManager NM;
	LocationManager locationManager;
	LocationListener locationListener;
	String currentCondition;
	String currentDescription;
	String futureCondition;
	AsyncTask<Void, Void, String> conditions = null;
	Handler repeater;
	int interval = 300000;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		//TODO do something useful
		return Service.START_STICKY;
	}

	@Override
	public void onCreate() {
		NM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		final SharedPreferences prefs = getSharedPreferences("ContextNotifier", 0);
		interval = prefs.getInt("interval", 300000);
		repeater = new Handler();
		coreThread.run();
		Log.v("Weather", "start");
	}
	
	public Runnable coreThread = new Runnable(){
        @Override
        public void run() {
        	GPS();      	
        	repeater.postDelayed(coreThread, interval);
        }
	};

	public void GPS(){
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);  
		Criteria criteria = new Criteria();
		String bestProvider = locationManager.getBestProvider(criteria, false);  
		locationListener = new LocListener();
		locationManager.requestLocationUpdates(bestProvider, 2000, 0, locationListener);
	}

	private class LocListener implements LocationListener {
		public void onLocationChanged(Location loc) {
			if (loc != null) {
				if ((conditions == null) || (conditions.getStatus() == AsyncTask.Status.FINISHED)){
					conditions = new Weather(loc.getLatitude(), loc.getLongitude());
					conditions.execute();
				}
				try{
					locationManager.removeUpdates(locationListener);
				}catch(Exception e){
					e.printStackTrace();
				}
				try{
					locationManager = null;					
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub			
		}

		@Override
		public void onStatusChanged(String provider, int status,
				Bundle extras) {
			// TODO Auto-generated method stub
		}
	}

	public String makeURL(double lat, double lon){
		return "http://api.openweathermap.org/data/2.1/find/city?lat=" + Double.toString(lat) + "&lon=" + Double.toString(lon) + "&cnt=1";
	}

	private class Weather extends AsyncTask<Void, Void, String> {

		double lat, lon;

		public Weather(double lat, double lon){
			this.lat = lat;
			this.lon = lon;
		}

		@Override
		protected String doInBackground(Void... params) {
			try{
				URL url = new URL(makeURL(this.lat, this.lon));
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setConnectTimeout(5000);
				conn.setReadTimeout(10000);
				BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				String str = in.readLine();
				Log.v("Weather", str);
				return str;
			}catch(Exception e){
				e.printStackTrace();
				return "";
			}
		}      

		@Override
		protected void onPostExecute(String result) {
			parseCurrentJSON(result);
		}

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected void onProgressUpdate(Void... values) {
		}
	}   
	
	public void parseCurrentJSON(String str){
		try {
			JSONObject json = new JSONObject(str);
			JSONArray ar = json.getJSONArray("list");
			JSONObject city = ar.getJSONObject(0);
			JSONObject weather = city.getJSONArray("weather").getJSONObject(0);
			String Condition = weather.getString("main");
			String Description = weather.getString("description");
			if (!Description.equals(currentDescription)){
				currentCondition = Condition;
				currentDescription = Description;
				showNotification(Condition + ": " + Description);
			}					
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void showNotification(String weather){
			int icon = android.R.drawable.ic_menu_mapmode;
			NotificationCompat.Builder mBuilder =
			        new NotificationCompat.Builder(this)
			        .setSmallIcon(icon)
			        .setContentTitle("The weather has changed!")
			        .setContentText(weather);
			Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			mBuilder.setSound(uri);
			NM.notify(855, mBuilder.build());
	}

	@Override
	public IBinder onBind(Intent intent) {
		//TODO for communication return IBinder implementation
		return null;
	}
} 
