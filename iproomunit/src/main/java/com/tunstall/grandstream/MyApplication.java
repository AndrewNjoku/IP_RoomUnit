package com.tunstall.grandstream;


import android.app.Application;

import com.tunstall.grandstream.config.ConfigFetcher;
import com.tunstall.grandstream.config.ConfigFile;
import com.tunstall.grandstream.config.ConfigFile.ParamIndex;
import com.tunstall.grandstream.config.ConfigParser;

public class MyApplication extends Application {
	private ConfigParser mConfigParser;
	private ConfigFetcher mConfigFetcher;
	private AppSettings mAppSettings;
	private WiFiApManager mWiFiApManager;
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		mConfigParser = new ConfigParser(this);


		ConfigFile defaultConfig = mConfigParser.parseDefaultConfig();


		//Application mAppSettings , persisting application data
		//defaultconfig is passed in to the constructor conatining data to be bound at startup , when application is first opened

		mAppSettings = new AppSettings(this, defaultConfig);


		//Wifi Manager which is in charge of wifi toggle activity in order to

		mWiFiApManager = new WiFiApManager(this, mAppSettings);


		mConfigFetcher = new ConfigFetcher(mAppSettings, mConfigParser, mWiFiApManager); 
		
		//According to email from Richard Stone at 2015-04-30 1531:
		//If the button is visible then WiFi hotspot enabled.

		mWiFiApManager.enableHotspot(mAppSettings.getBooleanForKey(AppSettings.PREF_WIFI_VISIBILITY));
	}
	
	public AppSettings getAppSettings() {
		return mAppSettings;
	}
	
	public ConfigFetcher getConfigFetcher() {
		return mConfigFetcher;
	}

	public WiFiApManager getWiFiApManager() {
		return mWiFiApManager;
	}
}

