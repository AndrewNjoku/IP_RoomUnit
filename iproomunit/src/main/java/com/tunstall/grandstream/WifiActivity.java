package com.tunstall.grandstream;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.tunstall.com.R;

public class WifiActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wifi_hotspot);
		
		AppSettings appSettings = ((MyApplication) getApplicationContext()).getAppSettings();
		final WiFiApManager wiFiApManager = ((MyApplication) getApplicationContext()).getWiFiApManager();
		final ToggleButton wifiToggle = (ToggleButton) findViewById(R.id.wifi_toggle);
		wifiToggle.setChecked(wiFiApManager.isApOn());
		
		
		TextView wifiSSID = (TextView) findViewById(R.id.wifi_ssid);
		wifiSSID.setText(getString(R.string.SSID_with_arg, appSettings.getStringForKey(AppSettings.PREF_WIFI_SSID)));
		
		TextView wifiPwd = (TextView) findViewById(R.id.wifi_pwd);
		wifiPwd.setText(getString(R.string.password_with_arg, appSettings.getStringForKey(AppSettings.PREF_WIFI_PWD)));
		wifiToggle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				wiFiApManager.enableHotspot(wifiToggle.isChecked());
			}
		});
	}

}
