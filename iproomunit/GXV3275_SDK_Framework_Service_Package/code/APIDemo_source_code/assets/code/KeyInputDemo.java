/****************************************************************************
 *
 * FILENAME:        com.grandstream.gxp2200.demo.KeyInputDemo.java
 *
 * LAST REVISION:   $Revision: 1.0
 * LAST MODIFIED:   $Date: 2013/01/22 02:14:08 2013-5-10
 *
 * DESCRIPTION:     The class encapsulates the music ring tone operations.
 *
 * vi: set ts=4:
 *
 * Copyright (c) 2009-2013 by Grandstream Networks, Inc.
 * All rights reserved.
 *
 * This material is proprietary to Grandstream Networks, Inc. and,
 * in addition to the above mentioned Copyright, may be
 * subject to protection under other intellectual property
 * regimes, including patents, trade secrets, designs and/or
 * trademarks.
 *
 * Any use of this material for any purpose, except with an
 * express license from Grandstream Networks, Inc. is strictly
 * prohibited.
 *
 ***************************************************************************/
package com.grandstream.gxp2200.demo;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class KeyInputDemo extends Activity {

	private TextView mKeyText;
	private TextView mKeyState;
	private TextView mKeyCode;
	private SharedPreferences mShare;

	private String TAG = "KeyInputDemo";

	/**
	 * @Description:
	 * @param savedInstanceState
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		Log.d(TAG, "onCreate()");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.keyinputdemo);
		mKeyCode = (TextView) findViewById(R.id.keycode_text);
		mKeyText = (TextView) findViewById(R.id.key_text);
		mKeyState = (TextView) findViewById(R.id.key_state);

		TextView mKeyCodeLabel = (TextView) findViewById(R.id.keycode_label);
		mKeyCodeLabel.setText(getResources().getString(R.string.keycode_label_text));// "Key Code ");
		TextView mKeyCodeColon = (TextView) findViewById(R.id.keycode_colon);
		mKeyCodeColon.setText(" : ");
		TextView mKeyLabel = (TextView) findViewById(R.id.key_label);
		mKeyLabel.setText(getResources().getString(R.string.key_label_text));// "Key Display Value ");
		TextView mKeyColon = (TextView) findViewById(R.id.key_colon);
		mKeyColon.setText(" : ");
		TextView mStateLabel = (TextView) findViewById(R.id.state_label);
		mStateLabel.setText(getResources().getString(R.string.state_label_text));// "Key state ");
		TextView mStateColon = (TextView) findViewById(R.id.state_colon);
		mStateColon.setText(" : ");

		Button mBackButton = (Button) findViewById(R.id.exit);
		mBackButton.setText(getResources().getString(R.string.exit));
		mBackButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		mShare = getSharedPreferences(this.getClass().getSimpleName(), 0);

	}

	/**
	 * @Description:
	 * @param event
	 * @return
	 */
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		mKeyCode.setText(String.valueOf(event.getKeyCode()));
		String value = mShare.getString(String.valueOf(event.getKeyCode()),
				"UNKNOWN");
		mKeyText.setText(value);
		switch (event.getAction()) {
		case KeyEvent.ACTION_DOWN:
			mKeyState.setText(getResources().getString(R.string.action_down));
			break;
		case KeyEvent.ACTION_UP:
			mKeyState.setText(getResources().getString(R.string.action_up));
			break;
		case KeyEvent.ACTION_MULTIPLE:
			mKeyState.setText(getResources().getString(R.string.action_multi));
			break;
		}
		return true;

	}

	@Override
	public void onAttachedToWindow() {
		this.getWindow().setType(
				WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG);
		super.onAttachedToWindow();
	}

}
