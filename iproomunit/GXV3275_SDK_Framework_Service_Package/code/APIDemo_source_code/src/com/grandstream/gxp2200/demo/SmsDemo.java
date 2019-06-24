/*****************************************************************************
 *
 * FILENAME:        com.grandstream.gxp2200.demo.SmsDemo.java
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
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class SmsDemo extends Activity {
	private String TAG = SmsDemo.class.getSimpleName();

	public static final String SMS_RECIPIENT_EXTRA = "replay_phone_num";
	public static final String SMS_REPLY_ACCOUNT_ID_EXTRA = "replay_name";
	private static int mReplyAccountID;
	private boolean mCheckReply = false;

	private EditText mPhoneNum;
	private EditText mSmsContentEditText;

	private CheckBox mEnableEditCheckBox;
	private int mAccountID;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.smsdemo);

		if (getIntent().hasExtra(SMS_RECIPIENT_EXTRA)) {
			((TextView) findViewById(R.id.semm_phone_num)).setText(getIntent()
					.getExtras().getString(SMS_RECIPIENT_EXTRA));
		}
		// wheather is reply
		if (getIntent().hasExtra(SMS_REPLY_ACCOUNT_ID_EXTRA)) {
			mReplyAccountID = getIntent().getExtras().getInt(
					SMS_REPLY_ACCOUNT_ID_EXTRA);
			mCheckReply = true;
		}

		mEnableEditCheckBox = (CheckBox) findViewById(R.id.enable_edit);
		mPhoneNum = (EditText) findViewById(R.id.semm_phone_num);
		mSmsContentEditText = (EditText) findViewById(R.id.semm_content);

		// Set whether to receive text messages switch
		setEnableReceiveSms();

		// get account info
		getAccountInfo();

		// send sms or enter the text editing interface
		setAction();
	}

	/* get account info from AccountManager and show */
	private void getAccountInfo() {

		Spinner acctIdSpinner = (Spinner) findViewById(R.id.semm_account_id);
		List<String> list = new ArrayList<String>();

		final Account[] accounts = AccountManager.instance().getActiveAccounts(this);
		int size = accounts.length;
		for (int i = 0; i < size; i++) {
			list.add(accounts[i].getAccountName());
		}

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, list);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		acctIdSpinner.setAdapter(adapter);
		acctIdSpinner.setPrompt("Account Id");

		if (mCheckReply) {
			acctIdSpinner.setSelection(mReplyAccountID);
			mCheckReply = false;
		}
		acctIdSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> av, View v,
					int position, long arg3) {
				mAccountID = accounts[position].getAccountID();
				av.setVisibility(View.VISIBLE);
			}

			public void onNothingSelected(AdapterView<?> av) {
				av.setVisibility(View.VISIBLE);
			}
		});

		acctIdSpinner.setOnTouchListener(new Spinner.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				return false;
			}
		});
	}

	/* Whether receive message */
	private void setEnableReceiveSms() {
		// CheckBox enableReceive;
		CheckBox enableReceive = (CheckBox) findViewById(R.id.sms_enable_receiver);
		final PackageManager pm = this.getPackageManager();
		final ComponentName cn = new ComponentName(
				"com.grandstream.gxp2200.demo",
				"com.grandstream.gxp2200.demo.ReceiveSms");
		enableReceive
				.setChecked(pm.getComponentEnabledSetting(cn) == PackageManager.COMPONENT_ENABLED_STATE_ENABLED);
		enableReceive.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				Log.d(TAG, (isChecked ? "Enabling" : "Disabling")
						+ "SMS receiver");
				pm.setComponentEnabledSetting(
						cn,
						isChecked ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED
								: PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
						PackageManager.DONT_KILL_APP);
			}
		});
	}

	/* Send to SMS or to the sms window */
	private void setAction() {

		Button sendButton = (Button) findViewById(R.id.semm_send);
			sendButton.setOnClickListener(new Button.OnClickListener() {
				public void onClick(View v) {
					if (TextUtils.isEmpty(mPhoneNum.getText().toString())
							|| TextUtils.isEmpty(mSmsContentEditText.getText()
									.toString())
							|| TextUtils.isEmpty(String.valueOf(mAccountID)))
						if (!mEnableEditCheckBox.isChecked()) {
							Toast.makeText(SmsDemo.this, "please edit again",
									Toast.LENGTH_SHORT).show();
							return;
						}

					String number = mPhoneNum.getText().toString().trim();
					String content = mSmsContentEditText
						.getText().toString();
					Uri uri = Uri.parse("smsto:" + number);
					Intent intent = new Intent(Intent.ACTION_SENDTO,uri);
					intent.putExtra("sms_body", content);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent.putExtra(GlobalConfig.ACCOUNT, mAccountID);
					intent.putExtra(GlobalConfig.EDITENABLE,
							mEnableEditCheckBox.isChecked());
					startActivity(intent);
	
					if (!mEnableEditCheckBox.isChecked()) {
						Toast.makeText(SmsDemo.this, "Message send  done!",
								Toast.LENGTH_SHORT).show();
						finish();
					}
				}
			}
		);
	}

	/*
	 * back button event listen to save the draft EDITENABLE priority higher
	 * than DRAFT
	 */

	@Override
	public void onBackPressed() {

		if (!TextUtils.isEmpty(mSmsContentEditText.getText().toString().trim())) {
		    
		    String number = mPhoneNum.getText().toString().trim();
			String content = mSmsContentEditText
				.getText().toString();
			Uri uri = Uri.parse("smsto:" + number);
			Intent intent = new Intent(Intent.ACTION_SENDTO,uri);
			intent.putExtra("sms_body", content);
			intent.putExtra(GlobalConfig.ACCOUNT, mAccountID);
			intent.putExtra(GlobalConfig.EDITENABLE,
					mEnableEditCheckBox.isChecked());
			intent.putExtra(GlobalConfig.DRAFT, true);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			Toast.makeText(SmsDemo.this, "Message save as draft",
					Toast.LENGTH_SHORT).show();
		}
		super.onBackPressed();
	}
}
