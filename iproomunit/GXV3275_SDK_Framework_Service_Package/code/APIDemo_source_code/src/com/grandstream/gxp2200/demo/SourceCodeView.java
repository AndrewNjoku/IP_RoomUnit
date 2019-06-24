/*****************************************************************************
 *
 * FILENAME:        com.grandstream.gxp2200.demo.SourceCodeView.java
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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SourceCodeView extends Activity {
	
	private SparseArray<String> mButtonSparseArray = new SparseArray<String>();
	private String TAG = SourceCodeView.class.getName();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sourcecodeview);

		initButtonMap();
		initButton();
	}

	private void initButtonMap() {
		mButtonSparseArray.put(R.id.btn_apidemo_code, "ApiDemo.java");
		mButtonSparseArray.put(R.id.btn_calldemo_code, "CallDemo.java");
		mButtonSparseArray.put(R.id.btn_smsdemo_code, "SmsDemo.java");
		mButtonSparseArray.put(R.id.btn_receivesms_code,"ReceiveSms.java");
		mButtonSparseArray.put(R.id.btn_receiverdialog_code, "ReceiverDialog.java");
		mButtonSparseArray.put(R.id.btn_clicktocall_code, "clicktodial.html");
		mButtonSparseArray.put(R.id.btn_accountdemo_code, "AccountDemo.java");
		mButtonSparseArray.put(R.id.btn_accountinfo_code, "AccountInfo.java");
		mButtonSparseArray.put(R.id.btn_contactdemo_code, "ContactDemo.java");
		mButtonSparseArray.put(R.id.btn_groupmanager_code, "GroupManager.java");
		mButtonSparseArray.put(R.id.btn_calllogdemo_code, "CallLogDemo.java");
		mButtonSparseArray.put(R.id.btn_globalconfig_code, "GlobalConfig.java");
		mButtonSparseArray.put(R.id.btn_audiochangedemo_code, "AudioChannelDemo.java");
		mButtonSparseArray.put(R.id.btn_callstatusdemo_code, "CallStatusDemo.java");
		mButtonSparseArray.put(R.id.btn_keyinputdemo_code, "KeyInputDemo.java");
		
	}
	
	private void initButton() {

		Button btn;
		ButtonListener listener = new ButtonListener();
		int size = mButtonSparseArray.size();
		for(int i = 0; i < size; i++) {
			int btnId = mButtonSparseArray.keyAt(i);
			btn = (Button) findViewById(btnId);
			btn.setText(mButtonSparseArray.valueAt(i));
			btn.setOnClickListener(listener);
			}
	}

	private final class ButtonListener implements OnClickListener {
		public void onClick(View v) {
			
			String classname = mButtonSparseArray.get(v.getId());
			if (classname == null) {
				Log.d(TAG, "class name is null and return");
				return;
			}
			startCodeView(classname);
		}

		private void startCodeView(String codeName) {
			Intent intent = new Intent(SourceCodeView.this, CodeBrowser.class);
			intent.putExtra(CodeBrowser.VIEW_CODE_DATAEXTRA, codeName);
			startActivity(intent);
		}
	}
}
