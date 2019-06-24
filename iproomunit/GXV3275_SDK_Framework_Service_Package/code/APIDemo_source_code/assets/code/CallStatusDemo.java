/****************************************************************************
 *
 * FILENAME:        com.grandstream.gxp2200.demo.CallStatusDemo.java
 *
 * LAST REVISION:   $Revision: 1.0
 * LAST MODIFIED:   $Date: 2013/01/22 02:14:08 2013-2-25
 *
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

import com.base.module.phone.service.CallStatusManager;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class CallStatusDemo extends Activity {

	private static final String TAG = "CallStatusManagerTestActivity";
	private CallStatusManager mCallStatusManager;
	private final int MAX_LINE_COUNT = 6;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.callstatusdemo);
		init();
	}

	private void init() {
		mCallStatusManager = CallStatusManager.instance();
		if (mCallStatusManager == null) {
			Log.d(TAG, "CallStatusManager.instance() is wrong");
			return;
		}
		mCallStatusManager.bindPhoneService(this);

		Button isViewShowButton = (Button) findViewById(R.id.btn_isCallViewShow);
		isViewShowButton.setOnClickListener(mListener);
		Button isBusyButton = (Button) findViewById(R.id.btn_isBusy);
		isBusyButton.setOnClickListener(mListener);
		Button getStatusButton = (Button) findViewById(R.id.btn_getLineStatus);
		getStatusButton.setOnClickListener(mListener);
	}

	private OnClickListener mListener = new OnClickListener() {

		@Override
		public void onClick(View view) {
			switch (view.getId()) {
			case R.id.btn_isCallViewShow:
				isViewShow();
				break;
			case R.id.btn_isBusy:
				isBusy();
				break;
			case R.id.btn_getLineStatus:
				getStatus();
				break;
			default:
				break;
			}
		}
	};

	private int getStatus() {
		int status = 0;
		int line = 0;
		for (int i = 0; i < MAX_LINE_COUNT; i++) {
			status = mCallStatusManager.getLineStatus(i);
			if (0 != status) {
				Toast.makeText(
						this,
						getResources().getString(R.string.toast_call_line)
								+ " " + i + " "
								+ getResources().getString(
										R.string.toast_status_is) + " " + status,
						Toast.LENGTH_SHORT).show();
				line++;
			}
		}
		if (0 == line) {
			Toast.makeText(
					this,
					getResources().getString(
							R.string.toast_there_is_no_active_line),
					Toast.LENGTH_SHORT).show();
		}
		return line;
	}

	private boolean isBusy() {
		boolean is = mCallStatusManager.isBusy();
		if (is) {
			Toast.makeText(this,
					getResources().getString(R.string.toast_call_line_is_busy),
					Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(
					this,
					getResources().getString(R.string.toast_call_line_not_busy),
					Toast.LENGTH_SHORT).show();
		}
		return is;
	}

	private boolean isViewShow() {
		boolean is = mCallStatusManager.isCallViewShow();
		if (is) {
			Toast.makeText(this,
					getResources().getString(R.string.toast_call_view_show),
					Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(
					this,
					getResources().getString(R.string.toast_call_view_not_show),
					Toast.LENGTH_SHORT).show();
		}
		return is;
	}

	@Override
	protected void onDestroy() {
		mCallStatusManager.unbindPhoneService(this);
		Log.d(TAG, "mCallStatusManager has unbind service");
		super.onDestroy();
	}
}
