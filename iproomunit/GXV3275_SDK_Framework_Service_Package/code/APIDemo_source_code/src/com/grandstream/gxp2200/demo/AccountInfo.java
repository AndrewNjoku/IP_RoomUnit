/****************************************************************************
 *
 * FILENAME:        com.grandstream.gxp2200.demo.AccountInfo.java
 *
 * LAST REVISION:   $Revision: 1.0
 * LAST MODIFIED:   $Date: Dec 11, 2012
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

import com.base.module.account.Account;
import com.base.module.account.AccountManager;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class AccountInfo extends Activity {

	private int mAccountID = -1;
	private Account mAccount;

	private EditText mAccountNameEditText;
	private EditText mSipServerEditText;
	private EditText mOutBoundProxyEditText;
	private EditText mSipUserIdEditText;
	private EditText mSipAuthIdEditText;
	private EditText mSipAuthPasswordEditText;
	private EditText mVoiceMailUserIdEditText;
	private EditText mDisplayNameEditText;
	private CheckBox mActiveCheckBox;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.accountinfo);
		mAccountID = getIntent().getExtras().getInt(GlobalConfig.ACCOUNT, -1);
		initAccountInfo();
		initButton();
	}

	private void initButton() {
		Button btnSave = (Button) findViewById(R.id.btn_save_acctinfo);
		btnSave.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				saveAccountInfo();
			}
		});

		Button btnCancel = (Button) findViewById(R.id.btn_cancel_acctinfo);
		btnCancel.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
	}

	private void saveAccountInfo() {

		mAccount.setAccountName(mAccountNameEditText.getText().toString().trim());
		mAccount.setSipServer(mSipServerEditText.getText().toString().trim());
		mAccount.setOutboundProxy(mOutBoundProxyEditText.getText().toString().trim());
		mAccount.setSipUserID(mSipUserIdEditText.getText().toString().trim());
		mAccount.setSipAuthID(mSipAuthIdEditText.getText().toString().trim());
		mAccount.setSipAuthPassword(mSipAuthPasswordEditText.getText().toString().trim());
		mAccount.setVoiceMailUserID(mVoiceMailUserIdEditText.getText().toString().trim());
		mAccount.setDisplayName(mDisplayNameEditText.getText().toString().trim());
		mAccount.setActive(mActiveCheckBox.isChecked());

		AccountManager.instance().updateAccount(this, mAccountID, mAccount);

		finish();
	}

	private void initAccountInfo() {
		if (!GlobalConfig.isAccountAvailable(mAccountID)) {
			return;
		}

		mAccount = AccountManager.instance().getAccountByAccountID(this, mAccountID);

		mAccountNameEditText = (EditText) findViewById(R.id.et_account_name);
		mAccountNameEditText.setText(mAccount.getAccountName());

		mSipServerEditText = (EditText) findViewById(R.id.et_sip_service);
		mSipServerEditText.setText(mAccount.getSipServer());

		mOutBoundProxyEditText = (EditText) findViewById(R.id.et_out_bound_proxy);
		mOutBoundProxyEditText.setText(mAccount.getOutboundProxy());

		mSipUserIdEditText = (EditText) findViewById(R.id.et_sip_user_id);
		mSipUserIdEditText.setText(mAccount.getSipUserID());

		mSipAuthIdEditText = (EditText) findViewById(R.id.et_sip_auth_id);
		mSipAuthIdEditText.setText(mAccount.getSipAuthID());

		mSipAuthPasswordEditText = (EditText) findViewById(R.id.et_sip_auth_password);
		mSipAuthPasswordEditText.setText(mAccount.getSipAuthPassword());

		mVoiceMailUserIdEditText = (EditText) findViewById(R.id.et_voice_mail_user_id);
		mVoiceMailUserIdEditText.setText(mAccount.getVoiceMailUserID());

		mDisplayNameEditText = (EditText) findViewById(R.id.et_display_name);
		mDisplayNameEditText.setText(mAccount.getDisplayName());

		mActiveCheckBox = (CheckBox) findViewById(R.id.ck_account_active);
		mActiveCheckBox.setChecked(mAccount.getActive());
	}
}
