/*****************************************************************************
 *
 * FILENAME:        com.grandstream.gxp2200.demo.CallLogDemo.java
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

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import com.base.module.account.Account;
import com.base.module.account.AccountManager;

import android.os.Bundle;
import android.provider.CallLog;
import android.provider.CallLog.Calls;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class CallLogDemo extends Activity {

	private final int MENU_TYPE_OPTIONS = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.calllogdemo);
		setCallLogListAdapte();
	}

	public void setCallLogListAdapte() {

		Cursor c = getCallLog(null);

		if (c != null) {
			ListAdapter adapter = new MyAdapter(this, R.layout.calllogitem, c,
					new String[] { CallLog.Calls.CACHED_NAME,
							CallLog.Calls.NUMBER, CallLog.Calls._ID },
					new int[] { R.id.calllog_name, R.id.calllog_phone });
			ListView list = (ListView) findViewById(R.id.listView);
			list.setCacheColorHint(Color.TRANSPARENT);
			list.setAdapter(adapter);
		}
	}

	public Cursor getCallLog(String[] projection) {

		Cursor cursor = managedQuery(CallLog.Calls.CONTENT_URI, projection,
				null, null, CallLog.Calls.DEFAULT_SORT_ORDER);
		return cursor;
	}

	// Clear CallLog
	public void ClearCallLog() {
		ContentResolver resolver = getContentResolver();
		resolver.delete(CallLog.Calls.CONTENT_URI, null, null);
	}

	@SuppressLint("SimpleDateFormat")
	private String getFormattedTime(long time) {
		SimpleDateFormat sfd = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
		return sfd.format(new Date(time));
	}

	@SuppressLint("SimpleDateFormat")
	private String getFormattedDurationTime(long duration) {
		String pattern = "mm:ss";
		if (duration > 3600) {
			pattern = "HH:mm:ss";
		}
		SimpleDateFormat sfd = new SimpleDateFormat(pattern);
		return sfd.format(new Date(duration * 1000));
	}

	class MyAdapter extends SimpleCursorAdapter {

		private LayoutInflater mInflater = LayoutInflater
				.from(CallLogDemo.this);

		public MyAdapter(Context context, int layout, Cursor c, String[] from,
				int[] to) {
			super(context, layout, c, from, to);
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {

			RelativeLayout rl = null;
			ArrayList<String> list = new ArrayList<String>();

			if (view == null)
				rl = (RelativeLayout) mInflater.inflate(R.layout.calllogitem,
						null);
			else
				rl = (RelativeLayout) view;

			TextView tvAccount = (TextView) rl
					.findViewById(R.id.calllog_account);
			TextView tvDuration = (TextView) rl.findViewById(R.id.calllog_time);
			TextView tvType = (TextView) rl
					.findViewById(R.id.calllog_type_duration);

			String gsAccount = cursor.getString(cursor
					.getColumnIndex(CallLog.Calls.GS_ACCOUNT));
			String accountName = getAccountNameFromId(Integer
					.valueOf(gsAccount).intValue());
			tvAccount.setText("-" + accountName);

			long time = cursor.getLong(cursor
					.getColumnIndex(CallLog.Calls.DATE));
			String timefomat = getFormattedTime(time);
			tvDuration.setText(timefomat);

			String calllogid = cursor.getString(cursor
					.getColumnIndex(CallLog.Calls._ID));
			list.add(calllogid);

			int tid = cursor.getInt(cursor
					.getColumnIndex(android.provider.CallLog.Calls.TYPE));
			String tempType = null;
			switch (tid) {
			case Calls.INCOMING_TYPE:
				tempType = getString(R.string.call_log_type_incoming);
				break;
			case Calls.OUTGOING_TYPE:
				tempType = getString(R.string.call_log_type_outgoing);
				break;
			case Calls.MISSED_TYPE:
				tempType = getString(R.string.call_log_type_missed);
				break;
			}

			long duration = cursor.getLong(cursor
					.getColumnIndex(CallLog.Calls.DURATION));
			String durationformat = getFormattedDurationTime(duration);
			if ("0".equals(duration)) {
				tvType.setText(tempType + ": "
						+ getString(R.string.call_log_not_connected));
			} else {
				tvType.setText(tempType + ": " + durationformat);
			}

			super.bindView(view, context, cursor);
		}
	}

	private String getAccountNameFromId(int accountID) {

		AccountManager am = AccountManager.instance();
		Account ac = am.getAccountByAccountID(this, accountID);
		return ac.getAccountName();
	}

	// /////////////////////////Menu Process////////////////////////////////

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		CreateOptionMenu(menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return MenuOptionsChoice(item);
	}

	private void CreateOptionMenu(Menu menu) {
		menu.add(Menu.NONE, MENU_TYPE_OPTIONS, Menu.NONE,
				this.getString(R.string.call_log_option_menu_title));
	}

	private boolean MenuOptionsChoice(MenuItem item) {

		if (MENU_TYPE_OPTIONS == item.getItemId()) {

			new AlertDialog.Builder(this)
					.setTitle(
							this.getString(R.string.call_log_option_memu_dialog_title))
					.setPositiveButton(this.getString(R.string.save),
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog,
										int which) {
									ClearCallLog();
								}
							})
					.setNegativeButton(this.getString(R.string.cancel),
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog,
										int which) {
								}
							}).create().show();
			return true;
		}
		return false;
	}
}
