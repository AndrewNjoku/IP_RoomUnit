package com.tunstall.grandstream.config;

import java.util.ArrayList;

import android.util.Log;

public class ConfigFile {
	
	public static final String LOG_TAG = "ConfigFile";
	
	private ArrayList<Param> mParams;
	
	public enum ParamIndex{
		ALARM_BUTTON, IM_OK_BUTTON, HOME_AWAY_BUTTON, DOOR_OPEN_BUTTON, DOOR_VIDEO_BUTTON, 
		DOOR_PRIVACY_BUTTON, WIFI_HOTSPOT_BUTTON, IP_ADDRESS, PORT, RESIDENT_ID, DOOR_VIDEO_TIMEOUT, 
		WIFI_HOTSPOT_SSID, WIFI_HOTSPOT_PWD, CONFIG_FILE_URL, CONFIG_FILE_INTERVAL, SETTINGS_PIN 
	}
	
	private class Param {
		public String Number;
		public String Value;
		public boolean ChangebleInUi;
		
		public Param(String num, String val, String show) {
			Number = num;
			Value = val;
			if (show.equals("Y")) {
				ChangebleInUi = true;
			}
		}
	}
	
	public ConfigFile() {
		mParams = new ArrayList<Param>();
	}

	public void addParam(String number, String value, String showInUi) {
		Param param = new Param(number, value, showInUi);
		mParams.add(param);
		Log.d(LOG_TAG, "Added param: " + number + "," + value + "," + showInUi);
		Log.d(LOG_TAG, "Params size is " + mParams.size());
	}
	
	public void updateParam(ParamIndex param, String value) {
		mParams.get(param.ordinal()).Value = value;
	}
	
	public String getValueForParam(ParamIndex param) {
		return mParams.get(param.ordinal()).Value;
	}
	
	public boolean getBooleanValueForParam(ParamIndex param) {
		if (param == null) {
			return false;
		} else {
			String val = mParams.get(param.ordinal()).Value;
			if (val.equals("0")) {
				return false;
			} else {
				return true;
			}
		}
	}
	
	public boolean isParamChangeableInUi(ParamIndex param) {
		return mParams.get(param.ordinal()).ChangebleInUi;
	}
}
