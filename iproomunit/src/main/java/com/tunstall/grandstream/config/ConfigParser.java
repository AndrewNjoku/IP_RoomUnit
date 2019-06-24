package com.tunstall.grandstream.config;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Context;
import android.util.Log;

import com.tunstall.com.R;

public class ConfigParser {

	public static final String LOG_TAG = "ConfigParser";
	
	private Context mContext;

	public ConfigParser(Context context) {
		mContext = context;
	}

	public ConfigFile parseDefaultConfig() {
		ConfigFile defaultConfig = new ConfigFile();
		parseConfig(
				mContext.getResources().openRawResource(R.raw.default_config),
				defaultConfig);
		return defaultConfig;
	}

	public void parseConfig(InputStream is, ConfigFile file) {
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";

		try {

			br = new BufferedReader(new InputStreamReader(is));
			while ((line = br.readLine()) != null) {
				String[] param = line.split(cvsSplitBy);
				String number = param[0];
				String value = param[1];
				String changeableInUi = param[2];		
				file.addParam(number, value, changeableInUi);
			}

		} catch (FileNotFoundException e) {
			Log.e(LOG_TAG, "FileNotFoundException.", e);
		} catch (IOException e) {
			Log.e(LOG_TAG, "IOException.", e);
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
