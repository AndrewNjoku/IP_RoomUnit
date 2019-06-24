package com.tunstall.grandstream;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;

import com.tunstall.grandstream.config.ConfigFile;
import com.tunstall.grandstream.config.ConfigFile.ParamIndex;

import static com.tunstall.grandstream.Storage.constants.*;


public class AppSettings {



	private SharedPreferences mPrefs;
	private ConfigFile mDefaultConfig;

	public AppSettings(Context context, ConfigFile defaultConfig) {
		mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
		mDefaultConfig = defaultConfig;
		updateConfigUrlWithMacAddress();
	}

	private void updateConfigUrlWithMacAddress() {
		String configUrl = mDefaultConfig.getValueForParam(ConfigFile.ParamIndex.CONFIG_FILE_URL);
		if (configUrl.endsWith("%s.csv")) {
			configUrl = String.format(configUrl, getStoredMacAddress());
			mDefaultConfig.updateParam(ConfigFile.ParamIndex.CONFIG_FILE_URL, configUrl);
		}
	}

	public void registerListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
		mPrefs.registerOnSharedPreferenceChangeListener(listener);
	}

	public void unregisterListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
		mPrefs.unregisterOnSharedPreferenceChangeListener(listener);
	}



	public String getStringForKey(String key) {
		if (key.equals(PREF_LAST_CONFIG_MESSAGE)) {

			return mPrefs.getString(key, LAST_CONFIG_MESSAGE_DEFAULT);
		} else {
			return mPrefs.getString(key,
					mDefaultConfig.getValueForParam(getParamForKey(key)));
		}
	}

	public boolean getBooleanForKey(String key) {
		return mPrefs.getBoolean(key,
				mDefaultConfig.getBooleanValueForParam(getParamForKey(key)));
	}

	public boolean isPrefChangeableInUi(String key) {
		return mPrefs.getBoolean(key+CHANGEABLE_APPEND,
				mDefaultConfig.isParamChangeableInUi(getParamForKey(key)));
	}

	public int getButtonVisibility(String key) {
		boolean visible = getBooleanForKey(key);
		if (visible) {
			return View.VISIBLE;
		} else {
			return View.GONE;
		}
	}

	private ConfigFile.ParamIndex getParamForKey(String key) {
		ConfigFile.ParamIndex param = null;
		if (key == PREF_IP_ADDRESS) {
			param = ConfigFile.ParamIndex.IP_ADDRESS;
		} else if (key == PREF_PORT) {
			param = ConfigFile.ParamIndex.PORT;
		} else if (key == PREF_RESIDENT_ID) {
			param = ConfigFile.ParamIndex.RESIDENT_ID;

		} else if (key == PREF_DOOR_OPEN) {
			param = ConfigFile.ParamIndex.DOOR_OPEN_BUTTON;
		} else if (key == PREF_DOOR_VIDEO) {
			param = ConfigFile.ParamIndex.DOOR_VIDEO_BUTTON;
		} else if (key == PREF_DOOR_PRIVACY) {
			param = ConfigFile.ParamIndex.DOOR_PRIVACY_BUTTON;
		} else if (key == PREF_HOME_AWAY) {
			param = ConfigFile.ParamIndex.HOME_AWAY_BUTTON;
		} else if (key == PREF_IM_OK) {
			param = ConfigFile.ParamIndex.IM_OK_BUTTON;
		} else if (key == PREF_ALARM) {
			param = ConfigFile.ParamIndex.ALARM_BUTTON;
		} else if (key == PREF_WIFI_VISIBILITY) {
			param = ConfigFile.ParamIndex.WIFI_HOTSPOT_BUTTON;

		} else if (key == PREF_WIFI_SSID) {
			param = ConfigFile.ParamIndex.WIFI_HOTSPOT_SSID;
		} else if (key == PREF_WIFI_PWD) {
			param = ConfigFile.ParamIndex.WIFI_HOTSPOT_PWD;

		} else if (key == PREF_DOOR_VIDEO_TIMEOUT) {
			param = ConfigFile.ParamIndex.DOOR_VIDEO_TIMEOUT;
		} else if (key == PREF_CONFIG_URL) {
			param = ConfigFile.ParamIndex.CONFIG_FILE_URL;
		} else if (key == PREF_CONFIG_CHECK_INTERVAL) {
			param = ConfigFile.ParamIndex.CONFIG_FILE_INTERVAL;
		} else if (key == PREF_SETTINGS_PIN) {
			param = ConfigFile.ParamIndex.SETTINGS_PIN;
		}

		return param;
	}

	public void updatePreferencesForConfigFile(ConfigFile config) {
		SharedPreferences.Editor editor = mPrefs.edit();

		updatePreferencesForKey(editor, config, PREF_IP_ADDRESS, false);
		updatePreferencesForKey(editor, config, PREF_PORT, false);
		updatePreferencesForKey(editor, config, PREF_RESIDENT_ID, false);

		updatePreferencesForKey(editor, config, PREF_DOOR_OPEN, true);
		updatePreferencesForKey(editor, config, PREF_DOOR_VIDEO, true);
		updatePreferencesForKey(editor, config, PREF_DOOR_PRIVACY, true);
		updatePreferencesForKey(editor, config, PREF_HOME_AWAY, true);
		updatePreferencesForKey(editor, config, PREF_IM_OK, true);
		updatePreferencesForKey(editor, config, PREF_ALARM, true);

		updatePreferencesForKey(editor, config, PREF_WIFI_VISIBILITY, true);
		updatePreferencesForKey(editor, config, PREF_WIFI_SSID, false);
		updatePreferencesForKey(editor, config, PREF_WIFI_PWD, false);

		updatePreferencesForKey(editor, config, PREF_DOOR_VIDEO_TIMEOUT, false);
		updatePreferencesForKey(editor, config, PREF_CONFIG_URL, false);
		updatePreferencesForKey(editor, config, PREF_CONFIG_CHECK_INTERVAL, false);
		updatePreferencesForKey(editor, config, PREF_SETTINGS_PIN, false);

		editor.commit();

		//access was static , created getter to access LogTag from constants static method

		Log.d(getLogTag(), "Preferences updated from remote config file.");
	}

	/**
	 * Sets the default values of the preferences from the default config, in case a remote config has never been downloaded.
	 * This is done so if an edit preference is clicked upon in SettingsActivity, the popup textfield will contain the default value.
	 * Otherwise, if the function below is never executed, the popup textfield will be blank.
	 */
	public void setDefaultValuesIfConfigHasNeverBeenDownloaded() {
		if (getLastConfigMessage().compareTo(LAST_CONFIG_MESSAGE_DEFAULT) == 0) {
			SharedPreferences.Editor editor = mPrefs.edit();

			updatePreferencesForKey(editor, mDefaultConfig, PREF_IP_ADDRESS, false);
			updatePreferencesForKey(editor, mDefaultConfig, PREF_PORT, false);
			updatePreferencesForKey(editor, mDefaultConfig, PREF_RESIDENT_ID, false);

			updatePreferencesForKey(editor, mDefaultConfig, PREF_DOOR_OPEN, true);
			updatePreferencesForKey(editor, mDefaultConfig, PREF_DOOR_VIDEO, true);
			updatePreferencesForKey(editor, mDefaultConfig, PREF_DOOR_PRIVACY, true);
			updatePreferencesForKey(editor, mDefaultConfig, PREF_HOME_AWAY, true);
			updatePreferencesForKey(editor, mDefaultConfig, PREF_IM_OK, true);
			updatePreferencesForKey(editor, mDefaultConfig, PREF_ALARM, true);

			updatePreferencesForKey(editor, mDefaultConfig, PREF_WIFI_VISIBILITY, true);
			updatePreferencesForKey(editor, mDefaultConfig, PREF_WIFI_SSID, false);
			updatePreferencesForKey(editor, mDefaultConfig, PREF_WIFI_PWD, false);

			updatePreferencesForKey(editor, mDefaultConfig, PREF_DOOR_VIDEO_TIMEOUT, false);
			updatePreferencesForKey(editor, mDefaultConfig, PREF_CONFIG_URL, false);
			updatePreferencesForKey(editor, mDefaultConfig, PREF_CONFIG_CHECK_INTERVAL, false);
			updatePreferencesForKey(editor, mDefaultConfig, PREF_SETTINGS_PIN, false);

			editor.commit();
		}
	}

	private void updatePreferencesForKey(SharedPreferences.Editor editor, ConfigFile config, String key, boolean valIsBoolean) {
		ConfigFile.ParamIndex param = getParamForKey(key);

		boolean changeableVal = config.isParamChangeableInUi(param);

		if (valIsBoolean) {
			boolean val = config.getBooleanValueForParam(param);
			editor.putBoolean(key, val);
			Log.d(getLogTag(), String.format("%s updated. New val: %s, changeable: %s", key, val,
					changeableVal));
		} else {
			String val = config.getValueForParam(param);
			editor.putString(key, val);
			Log.d(getLogTag(), String.format("%s updated. New val: %s, changeable: %s", key, val,
					changeableVal));
		}
		editor.putBoolean(key + CHANGEABLE_APPEND, changeableVal);
	}

	public void setLastConfigMessage(String msg){
		SharedPreferences.Editor editor = mPrefs.edit();
		editor.putString(PREF_LAST_CONFIG_MESSAGE, msg);
		editor.commit();
	}

	public String getLastConfigMessage(){
		return mPrefs.getString(PREF_LAST_CONFIG_MESSAGE, LAST_CONFIG_MESSAGE_DEFAULT);
	}

	public String getStoredMacAddress() {
		String macAddress = mPrefs.getString(PREF_MAC_ADDRESS, "");
		if (macAddress.length() == 0) {
			macAddress = getMacAddressFromSystem().replaceAll(":", "").toLowerCase();
			SharedPreferences.Editor editor = mPrefs.edit();
			editor.putString(PREF_MAC_ADDRESS, macAddress);
			editor.commit();
		}
		return macAddress;
	}

	private String getMacAddressFromSystem()
	{
		StringBuffer fileData = new StringBuffer(1000);
		try {
			BufferedReader reader = new BufferedReader(new FileReader("/sys/class/net/eth0/address"));
			char[] buf = new char[1024];
			int numRead = 0;
			while ((numRead = reader.read(buf)) != -1) {
				String readData = String.valueOf(buf, 0, numRead);
				fileData.append(readData);
			}
			reader.close();
			return fileData.toString().replaceAll("\\s","");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}

	


