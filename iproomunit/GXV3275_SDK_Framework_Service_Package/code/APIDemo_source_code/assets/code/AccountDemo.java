/****************************************************************************
 *
 * FILENAME:        com.grandstream.gxp2200.demo.AccountDemo.java
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

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.base.module.account.Account;
import com.base.module.account.AccountManager;

public class AccountDemo extends Activity {

	private ListView mAccountDemoListView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		// create layout
		LinearLayout layout;
		layout = new LinearLayout(this);
		layout.setOrientation(LinearLayout.VERTICAL);

		// create listview
		mAccountDemoListView = new ListView(this);
		mAccountDemoListView.setCacheColorHint(Color.TRANSPARENT);
		LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT);

		// add listview to layout
		layout.addView(mAccountDemoListView, param);

		// show layout
		setContentView(layout);
	}

	@Override
	protected void onResume() {
		super.onResume();
		getAccountInfo();
	}

	private void getAccountInfo() {

		List<String> list = new ArrayList<String>();
		Account[] accounts = AccountManager.instance().getAccounts(this);
		int size = accounts.length;
		for (int i = 0; i < size; i++) {
			list.add(accounts[i].getAccountName());
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, list);
		mAccountDemoListView.setAdapter(adapter);

		mAccountDemoListView.setOnItemClickListener(new Listener());
	}

	private final class Listener implements OnItemClickListener {

		public void onItemClick(AdapterView<?> adapterView, View view, int postion, long id) {
			Intent intent = new Intent(AccountDemo.this, AccountInfo.class);
			intent.putExtra(GlobalConfig.ACCOUNT, postion);
			startActivity(intent);
		}
	}
}
