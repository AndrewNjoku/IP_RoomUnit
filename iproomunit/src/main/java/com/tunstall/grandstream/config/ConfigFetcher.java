package com.tunstall.grandstream.config;

import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.os.AsyncTask;
import android.util.Log;

import com.tunstall.grandstream.AppSettings;
import com.tunstall.grandstream.WiFiApManager;
import com.tunstall.grandstream.config.ConfigFile.ParamIndex;
import com.tunstall.utility.WeakHandler;

public class ConfigFetcher {

	private static final int MS_TO_MIN = 60000;
	private static final int RECHECK_TIME_IF_EXCEPTION = 30;
	private static final String LOG_TAG = "ConfigFetch";
	
	private AppSettings mAppSettings;
	private ConfigParser mConfigParser;
	private WeakHandler mHandler;
	private WiFiApManager mWiFiApManager;
	
	public ConfigFetcher(AppSettings appSettings, ConfigParser configParser, WiFiApManager wiFiApManager) {


		//Done
		mAppSettings = appSettings;

		//Done
		mConfigParser = configParser;

		//Understood
		mWiFiApManager = wiFiApManager;

		//First initialisation of handler to communicate with the threads message que
		mHandler = new WeakHandler();


		// Start fetching randomly between 5-30min so not all phones in the building
		// starts fetching at the same time after a power cycle.
		int startTime = (int)(Math.random() * 25 + 5);
		Log.d(LOG_TAG, "Will fetch new config file in " + startTime + " min.");
		mHandler.postDelayed(mRunnable, startTime * MS_TO_MIN);
	}
	
	public void force(){
		new FetchConfigTask().execute();
	}
	
	private Runnable mRunnable = new Runnable() {
		@Override
		public void run() {
			//fetchConfig();
			new FetchConfigTask().execute();
			
			String recheckTimeStr = mAppSettings.getStringForKey(AppSettings.PREF_CONFIG_CHECK_INTERVAL);
			int recheckTime = RECHECK_TIME_IF_EXCEPTION;
			
			try {
				recheckTime = Integer.parseInt(recheckTimeStr);
			} catch (NumberFormatException e) {
				Log.d(LOG_TAG, "Recheck time NumberFormatException.");
			}

			Log.d(LOG_TAG, "Will fetch new config file in " + recheckTime + " min.");
			mHandler.postDelayed(this, recheckTime * MS_TO_MIN); // reschedule the handler
		}
	};
	
	class FetchConfigTask extends AsyncTask<String, Void, Void> {
		
		@Override
		protected Void doInBackground(String... params) {
			String dateStr = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.UK).format(new Date());
	        try {
	            String source = mAppSettings.getStringForKey(AppSettings.PREF_CONFIG_URL);
				Log.d(LOG_TAG, "Fetching config file from: " + source);
				URL url = new URL( source);
				InputStream is = url.openStream();
				ConfigFile config = new ConfigFile();
				mConfigParser.parseConfig(is, config);
				mAppSettings.updatePreferencesForConfigFile(config);	
				mAppSettings.setLastConfigMessage("Downloaded at " + dateStr);
				
				//According to email from Richard Stone at 2015-04-30 1531:
				//If the button is visible then WiFi hotspot enabled.
				mWiFiApManager.enableHotspot(mAppSettings.getBooleanForKey(AppSettings.PREF_WIFI_VISIBILITY));
				
	        } catch (Exception e) {
	            Log.e(LOG_TAG, "Fetch config failed.", e);
	            mAppSettings.setLastConfigMessage("Download failed at " + dateStr + " - " + e.getMessage());
	        }
			return null;
	    }

	    protected void onPostExecute(InputStream is) {
	    }
	}
	
	public void onDestroy() {
		mHandler.removeCallbacks(mRunnable);
	}
}
