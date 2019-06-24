/*****************************************************************************
 *
 * FILENAME:        com.grandstream.gxp2200.demo.CallDemo.java
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

import java.util.ArrayList;
import java.util.List;

import com.base.module.account.Account;
import com.base.module.account.AccountManager;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class CallDemo extends Activity {

	private EditText mPhoneNumEditText;
	private int mAccountID;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.calldemo);
		mPhoneNumEditText = (EditText) findViewById(R.id.call_phone_num);
		mPhoneNumEditText.setOnTouchListener(onTouchListener);

		getAccountInfo();
		initButton();
	}

	private void initButton() {
		int[] btnGroup;
		Button btn;

		btn = (Button) findViewById(R.id.del);
		btn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				mPhoneNumEditText.dispatchKeyEvent(new KeyEvent(0, KeyEvent.KEYCODE_DEL));
			}
		});

		btnGroup = new int[] { R.id.one, R.id.two, R.id.three, R.id.four, R.id.five, R.id.six,
				R.id.seven, R.id.eight, R.id.nine, R.id.star, R.id.zero, R.id.pound };
		DigitalClickListener listenerNum = new DigitalClickListener();
		int lengthNum = btnGroup.length;
		for (int i = 0; i < lengthNum; i++) {
			btn = (Button) findViewById(btnGroup[i]);
			btn.setOnClickListener(listenerNum);
		}

		btnGroup = new int[] { R.id.btn_opendial, R.id.btn_editcall, R.id.btn_directdial,
				R.id.btn_redial };
		DialActionListener dialAction = new DialActionListener();
		int lengthDial = btnGroup.length;
		for (int i = 0; i < lengthDial; i++) {
			btn = (Button) findViewById(btnGroup[i]);
			btn.setOnClickListener(dialAction);
		}
	}

	/* get the account information from AccountManager */
	private void getAccountInfo() {

		List<String> list = new ArrayList<String>();
		ArrayAdapter<String> adapter;
		Account[] accounts = AccountManager.instance().getActiveAccounts(this);
		int size = accounts.length;
		for (int i = 0; i < size; i++) {
			list.add(accounts[i].getAccountName());
		}
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		Spinner spnAcctId = (Spinner) findViewById(R.id.account_id);
		spnAcctId.setAdapter(adapter);
		spnAcctId.setPrompt(this.getString(R.string.spinner_accountid_prompt));
		spnAcctId.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
				mAccountID = position;
				adapterView.setVisibility(View.VISIBLE);
			}

			public void onNothingSelected(AdapterView<?> adapterView) {
				adapterView.setVisibility(View.VISIBLE);
			}
		});
		spnAcctId.setOnTouchListener(new Spinner.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				return false;
			}
		});
	}

	/* soft key not show */
	private OnTouchListener onTouchListener = new OnTouchListener() {
		public boolean onTouch(View v, MotionEvent event) {
			return true;
		}
	};

	/* Listen for the twelve button events */
	private final class DigitalClickListener implements OnClickListener {
		public void onClick(View v) {
			EditText editor = (EditText) findViewById(R.id.call_phone_num);
			String str = ((Button) v).getText().toString();
			int cursor = editor.getSelectionStart();
			editor.getText().insert(cursor, str);
		}
	}

	private final class DialActionListener implements OnClickListener {
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_opendial:
				startCall(Intent.ACTION_DIAL, 0, "tel:", false);
				break;
			case R.id.btn_editcall:
				startCall(Intent.ACTION_DIAL, mAccountID, "tel:"
						+ mPhoneNumEditText.getText().toString(), true);
				break;
			case R.id.btn_directdial:
				if (!TextUtils.isEmpty(mPhoneNumEditText.getText())) {
					startCall(Intent.ACTION_CALL, mAccountID, "tel:"
							+ mPhoneNumEditText.getText().toString(), true);
				} else {
					Toast.makeText(CallDemo.this, "phone num is null please write again",
							Toast.LENGTH_SHORT).show();
				}
				break;
			case R.id.btn_redial:
				startCall(Intent.ACTION_CALL, 0, "tel:redial", false);
				break;
			default:
				break;
			}
		}

		private void startCall(String intentName, int accountID, String dataValue,
				boolean isNeedAcctId) {
			Intent intent = new Intent(intentName);
			intent.setData(Uri.parse(dataValue));
			if (isNeedAcctId) {
				intent.putExtra(GlobalConfig.ACCOUNT, accountID);
			}
			startActivity(intent);
		}
	}
}
