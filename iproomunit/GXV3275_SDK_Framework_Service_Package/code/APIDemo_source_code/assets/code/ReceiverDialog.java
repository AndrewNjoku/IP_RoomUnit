/*****************************************************************************
 *
 * FILENAME:        com.grandstream.gxp2200.demo.ReceiverDialog.java
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
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class ReceiverDialog extends Activity {
	private static String TAG = ReceiverDialog.class.getSimpleName();

	private static final int DIALOG_SHOW_MESSAGE = 1;

	public static final String SMS_FROM_ADDRESS_EXTRA = "com.grandstream.gxp2200.demo.SMS_FROM_ADDRESS";
	public static final String SMS_ACCOUNT_ID_EXTRA = "com.com.grandstream.gxp2200.demo.SMS_ACCOUNT_ID_EXTRA";
	public static final String SMS_MESSAGE_EXTRA = "com.grandstream.gxp2200.demo.SMS_MESSAGE";

	private String mFullBodySting;
	private String mFromAddress;

	private int mAccountID;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mFromAddress = getIntent().getExtras()
				.getString(SMS_FROM_ADDRESS_EXTRA);
		mAccountID = getIntent().getExtras().getInt(SMS_ACCOUNT_ID_EXTRA);
		String message = getIntent().getExtras().getString(SMS_MESSAGE_EXTRA);
		mFullBodySting = String.format(
				getResources().getString(R.string.sms_speak_string_format),
				mFromAddress, message);
		showDialog(DIALOG_SHOW_MESSAGE);
	}

	/* show the dialog UI */
	@Override
	protected Dialog onCreateDialog(int id) {

		Log.d(TAG, "show receiver dialog");
		Dialog dialog = null;

		if (id == DIALOG_SHOW_MESSAGE) {
			dialog = new AlertDialog.Builder(this)
					.setIcon(android.R.drawable.ic_dialog_email)
					.setTitle(this.getString(R.string.receiver_dialog_title))
					.setMessage(mFullBodySting)
					.setPositiveButton(R.string.reply,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									Intent intent = new Intent();
									intent.setClass(ReceiverDialog.this,
											SmsDemo.class);
									intent.putExtra(
											SmsDemo.SMS_RECIPIENT_EXTRA,
											mFromAddress);
									intent.putExtra(
											SmsDemo.SMS_REPLY_ACCOUNT_ID_EXTRA,
											mAccountID);
									startActivity(intent);
									dialog.dismiss();
									finish();
								}
							})
					.setNegativeButton(R.string.dismiss,
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
									finish();
								}

							}).create();
		}
		return dialog;
	}
}
