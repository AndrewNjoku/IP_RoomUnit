package com.tunstall.grandstream;



import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.tunstall.com.R;
import com.tunstall.grandstream.SocketService.SocketSender;
import com.tunstall.grandstream.config.ConfigFetcher;

public class SettingsActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener{
	
	private AppSettings mAppSettings;
	private PackageInfo mPInfo;
	private SharedPreferences mPrefs;
	private ConfigFetcher mConfigFetcher;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i("Settings ","onCreate");
		mAppSettings = ((MyApplication) getApplicationContext()).getAppSettings();
		mConfigFetcher = ((MyApplication) getApplicationContext()).getConfigFetcher();
		mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		
		Button button = new Button(this);
		button.setText(R.string.force_download);
		setListFooter(button);
		
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {  
				mConfigFetcher.force();	
			} });
		
		createPreferences();
    }

	@Override
	public void onResume() {
		super.onResume();	
		Log.i("Settings ","onResume");
		mAppSettings.registerListener(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		// Unregister the listener whenever a key changes
		mAppSettings.unregisterListener(this);
	}
	
	private void createPreferences() {
		mAppSettings.setDefaultValuesIfConfigHasNeverBeenDownloaded();
		Log.i("Settings ","createPreferences");
		
		PreferenceScreen screen = getPreferenceManager().createPreferenceScreen(this);
		EditTextPreference editTextPref = null;
		SwitchPreference switchPref = null;
		Preference pref = null;
		PreferenceCategory category = new PreferenceCategory(this);
		
		/*
		 * GENERAL
		 */
		
		category.setTitle(R.string.general);
		screen.addPreference(category);
		
		String key = AppSettings.PREF_CONFIG_URL;
		if (mAppSettings.isPrefChangeableInUi(key)) {
			editTextPref = new EditTextPreference(this);
			editTextPref.setKey(key);
			editTextPref.setTitle(R.string.ui_config_file_url);
			editTextPref.setSummary(mAppSettings.getStringForKey(key));
			category.addPreference(editTextPref);
		}
		
		key = AppSettings.PREF_CONFIG_CHECK_INTERVAL;
		if (mAppSettings.isPrefChangeableInUi(key)) {
			editTextPref = new EditTextPreference(this);
			editTextPref.setKey(key);
			editTextPref.setTitle(R.string.ui_config_recheck);
			editTextPref.setSummary(mAppSettings.getStringForKey(key));
			category.addPreference(editTextPref);
		}
		
		key = AppSettings.PREF_SETTINGS_PIN;
		if (mAppSettings.isPrefChangeableInUi(key)) {
			editTextPref = new EditTextPreference(this);
			editTextPref.setKey(key);
			editTextPref.setTitle(R.string.settings_pin);
			editTextPref.setSummary(mAppSettings.getStringForKey(key));
			category.addPreference(editTextPref);
		}
		
		key = AppSettings.PREF_RESIDENT_ID;
		if (mAppSettings.isPrefChangeableInUi(key)) {
			editTextPref = new EditTextPreference(this);
			editTextPref.setKey(key);
			editTextPref.setTitle(R.string.residentid);
			editTextPref.setSummary(mAppSettings.getStringForKey(key));
			category.addPreference(editTextPref);
		}
		
		key = AppSettings.PREF_IP_ADDRESS;
		if (mAppSettings.isPrefChangeableInUi(key)) {
			editTextPref = new EditTextPreference(this);
			editTextPref.setKey(key);
			editTextPref.setTitle(R.string.ipaddress);
			editTextPref.setSummary(mAppSettings.getStringForKey(key));
			category.addPreference(editTextPref);
		}
		
		key = AppSettings.PREF_PORT;
		if (mAppSettings.isPrefChangeableInUi(key)) {
			editTextPref = new EditTextPreference(this);
			editTextPref.setKey(key);
			editTextPref.setTitle(R.string.port);
			editTextPref.setSummary(mAppSettings.getStringForKey(key));
			category.addPreference(editTextPref);
		}
		
		key = AppSettings.PREF_DOOR_VIDEO_TIMEOUT;
		if (mAppSettings.isPrefChangeableInUi(key)) {
			editTextPref = new EditTextPreference(this);
			editTextPref.setKey(key);
			editTextPref.setTitle(R.string.video_timeout);
			editTextPref.setSummary(mAppSettings.getStringForKey(key));
			category.addPreference(editTextPref);
		}
		
		key = AppSettings.PREF_LAST_CONFIG_MESSAGE;
		editTextPref = new EditTextPreference(this);
		editTextPref.setKey(key);
		editTextPref.setTitle(R.string.last_config);
		editTextPref.setSummary(mAppSettings.getLastConfigMessage());
		category.addPreference(editTextPref);
		
		//Add version
		pref = new Preference(this);
		pref.setTitle(R.string.version);		
		try {
			mPInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		pref.setSummary(mPInfo.versionName);
		category.addPreference(pref);
		
		if (category.getPreferenceCount() == 0) {
			screen.removePreference(category);
		}
		
		/*
		 *  Wifi
		 */
		
		category = new PreferenceCategory(this);
		category.setTitle(R.string.wifi);
		screen.addPreference(category);
		
		key = AppSettings.PREF_WIFI_VISIBILITY;
		if (mAppSettings.isPrefChangeableInUi(key)) {
			switchPref = new SwitchPreference(this);
			switchPref.setKey(key);
			switchPref.setTitle(R.string.visible_in_options_menu);
			category.addPreference(switchPref);
		}
		
		key = AppSettings.PREF_WIFI_SSID;
		if (mAppSettings.isPrefChangeableInUi(key)) {
			editTextPref = new EditTextPreference(this);
			editTextPref.setKey(key);
			editTextPref.setTitle(R.string.SSID);
			editTextPref.setSummary(mAppSettings.getStringForKey(key));
			category.addPreference(editTextPref);
		}
		
		key = AppSettings.PREF_WIFI_PWD;
		if (mAppSettings.isPrefChangeableInUi(key)) {
			editTextPref = new EditTextPreference(this);
			editTextPref.setKey(key);
			editTextPref.setTitle(R.string.password);
			editTextPref.setSummary(mAppSettings.getStringForKey(key));
			category.addPreference(editTextPref);
		}
		
		if (category.getPreferenceCount() == 0) {
			screen.removePreference(category);
		}
		
		/*
		 * Buttons
		 */
		
		category = new PreferenceCategory(this);
		category.setTitle(R.string.button_visibility);
		screen.addPreference(category);
		
		key = AppSettings.PREF_DOOR_OPEN;
		if (mAppSettings.isPrefChangeableInUi(key)) {
			switchPref = new SwitchPreference(this);
			switchPref.setKey(key);
			switchPref.setTitle(R.string.door_open);
			category.addPreference(switchPref);
		}
		
		key = AppSettings.PREF_DOOR_VIDEO;
		if (mAppSettings.isPrefChangeableInUi(key)) {
			switchPref = new SwitchPreference(this);
			switchPref.setKey(key);
			switchPref.setTitle(R.string.door_video);
			category.addPreference(switchPref);
		}
		
		key = AppSettings.PREF_DOOR_PRIVACY;
		if (mAppSettings.isPrefChangeableInUi(key)) {
			switchPref = new SwitchPreference(this);
			switchPref.setKey(key);
			switchPref.setTitle(R.string.door_privacy);
			category.addPreference(switchPref);
		}
		
		key = AppSettings.PREF_HOME_AWAY;
		if (mAppSettings.isPrefChangeableInUi(key)) {
			switchPref = new SwitchPreference(this);
			switchPref.setKey(key);
			switchPref.setTitle(R.string.homeaway);
			category.addPreference(switchPref);
		}
		
		key = AppSettings.PREF_IM_OK;
		if (mAppSettings.isPrefChangeableInUi(key)) {
			switchPref = new SwitchPreference(this);
			switchPref.setKey(key);
			switchPref.setTitle(R.string.imok);
			category.addPreference(switchPref);
		}
		
		key = AppSettings.PREF_ALARM;
		if (mAppSettings.isPrefChangeableInUi(key)) {
			switchPref = new SwitchPreference(this);
			switchPref.setKey(key);
			switchPref.setTitle(R.string.alarm);
			category.addPreference(switchPref);
		}
		
		if (category.getPreferenceCount() == 0) {
			screen.removePreference(category);
		}
		
		setPreferenceScreen(screen);
	}
	
	private void updatePreferenceSummary(String key) {
		Log.i("Settings ","key: " + key);
		Preference pref = getPreferenceScreen().findPreference(key);
		if (pref instanceof EditTextPreference) {
			EditTextPreference editTextPref = (EditTextPreference) getPreferenceScreen()
				.findPreference(key);
			editTextPref.setSummary(mAppSettings.getStringForKey(key));	
			Log.i("Settings ","Updated summary");
		}
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		updatePreferenceSummary(key);
	}
}
