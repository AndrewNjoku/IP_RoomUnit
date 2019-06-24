/*****************************************************************************
 *
 * FILENAME:        com.grandstream.gxp2200.demo.ReceiveSms.java
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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ReceiveSms extends BroadcastReceiver {
	
    private String TAG = ReceiveSms.class.getName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(TAG, "Receiver OK");

        final String phoneNum = intent.getStringExtra(GlobalConfig.NUMBER);
        final String content = intent.getStringExtra(GlobalConfig.CONTENT);
        final int accountId = intent.getIntExtra(GlobalConfig.ACCOUNT, -1);

        Intent intentDialog = new Intent();
        intentDialog.setClass(context, ReceiverDialog.class);
        intentDialog.putExtra(ReceiverDialog.SMS_FROM_ADDRESS_EXTRA, phoneNum);
        intentDialog.putExtra(ReceiverDialog.SMS_ACCOUNT_ID_EXTRA, accountId);
        intentDialog.putExtra(ReceiverDialog.SMS_MESSAGE_EXTRA, content);
        intentDialog.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(intentDialog);
    }
}
