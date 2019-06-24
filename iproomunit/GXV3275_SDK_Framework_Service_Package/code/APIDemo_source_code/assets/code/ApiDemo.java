/*****************************************************************************
 *
 * FILENAME:        com.grandstream.gxp2200.demo.ApiDemo.java
 *
 * LAST REVISION:   $Revision: 1.0
 * LAST MODIFIED:   $Date: 2012-12-4
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

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ApiDemo extends Activity {

	private SparseArray<Class<?>> mButtonSparseArray = new SparseArray<Class<?>>();
	private String TAG = ApiDemo.class.getName();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.apidemo);
		initButtonMap();
		initButton();
	}

	private void initButtonMap() {
		mButtonSparseArray.put(R.id.btn_calldemo, CallDemo.class);
		mButtonSparseArray.put(R.id.btn_smsdemo, SmsDemo.class);
		mButtonSparseArray.put(R.id.btn_accountdemo, AccountDemo.class);
		mButtonSparseArray.put(R.id.click_to_call, ClickToCall.class);
		mButtonSparseArray.put(R.id.btn_source_code_view, SourceCodeView.class);
		mButtonSparseArray.put(R.id.btn_contactdemo, ContactDemo.class);
		mButtonSparseArray.put(R.id.btn_calllogdemo, CallLogDemo.class);
	}

	/* init all the button */
	private void initButton() {
		Button btn;
		ButtonListener listener = new ButtonListener();
		int size = mButtonSparseArray.size();
		for (int i = 0; i < size; i++) {
			int btnId = mButtonSparseArray.keyAt(i);
			btn = (Button) findViewById(btnId);
			btn.setOnClickListener(listener);
		}
	}

	/* create ButtonListener implements OnClickListener */
	private final class ButtonListener implements OnClickListener {

		public void onClick(View v) {
			Class<?> cls = mButtonSparseArray.get(v.getId());
			if (cls == null) {
				Log.d(TAG, "class name is null with id " + v.getId());
			} else {
				handleIntent(cls);
			}
		}

		private void handleIntent(Class<?> classname) {
			Intent intent = new Intent(ApiDemo.this, classname);
			startActivity(intent);
		}
	}
}
