/****************************************************************************
 *
 * FILENAME:        com.grandstream.gxp2200.demo.GroupManager.java
 *
 * LAST REVISION:   $Revision: 1.0
 * LAST MODIFIED:   $Date: Dec 26, 2012
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

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Groups;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class GroupManager extends ListActivity {
	private static final String TAG = GroupManager.class.getSimpleName();

	private GroupAdapter myAdapter = null;

	private ArrayList<String> mGroupTitle = new ArrayList<String>();
	private ArrayList<GroupInfo> mGroupInfo = new ArrayList<GroupInfo>();

	private static final int CHANGE_GROUP_NAME_MENU = 0;
	private static final int DEL_GROUP_MENU = 1;
	private static final int DIALOG_ADD_GROUP = 2;
	private static final int ADD_GROUP_MENU = 3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		ListView listview = getListView();
		initGroupList();
		myAdapter = new GroupManager.GroupAdapter(GroupManager.this);
		setListAdapter(myAdapter);

		registerForContextMenu(listview);

		super.onCreate(savedInstanceState);
	}

	private void initGroupList() {
		mGroupInfo = getContactGroup();
		initGroupTitle(mGroupInfo);
	}

	private void initGroupTitle(ArrayList<GroupInfo> mGroupInfo) {
		int size = mGroupInfo.size();
		for (int i = 0; i < size; i++) {
			mGroupTitle.add(mGroupInfo.get(i).mTitle);
		}
	}

	/* show add group dialog */
	@Override
	protected Dialog onCreateDialog(int id) {

		if (DIALOG_ADD_GROUP == id) {
			LayoutInflater addfactory = LayoutInflater.from(this);
			final View addgroup = addfactory.inflate(R.layout.addgroup, null);
			return new AlertDialog.Builder(this)
					.setTitle(this.getString(R.string.add_group_dialog_title))
					.setView(addgroup)
					.setPositiveButton(this.getString(R.string.save),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									final EditText groupNameEditText = (EditText) addgroup
											.findViewById(R.id.group_name_edit);
									String name = null;
									name = groupNameEditText.getText()
											.toString();
									addGroup(name);
								}
							})
					.setNegativeButton(this.getString(R.string.cancel),
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog,
										int which) {
									final EditText groupNameEditText = (EditText) addgroup
											.findViewById(R.id.group_name_edit);
									groupNameEditText.setText(null);
								}
							}).create();
		}
		return null;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {

		int selectedPosition = ((AdapterView.AdapterContextMenuInfo) menuInfo).position;
		menu.setHeaderTitle(mGroupTitle.get(selectedPosition));
		menu.add(Menu.NONE, CHANGE_GROUP_NAME_MENU, Menu.NONE,
				this.getString(R.string.contact_group_manager_context_menu_0));
		menu.add(Menu.NONE, DEL_GROUP_MENU, Menu.NONE,
				this.getString(R.string.contact_group_manager_context_menu_1));
	}

	public boolean onContextItemSelected(MenuItem item) {

		final int selectedPosition = ((AdapterView.AdapterContextMenuInfo) item
				.getMenuInfo()).position;
		switch (item.getItemId()) {
		case CHANGE_GROUP_NAME_MENU:
			LayoutInflater changenamefactory = LayoutInflater.from(this);
			final View changegroup = changenamefactory.inflate(
					R.layout.changegroupname, null);
			new AlertDialog.Builder(this)
					.setTitle(
							this.getString(R.string.contact_group_manager_context_item_0_dialog_title))
					.setView(changegroup)
					.setPositiveButton(this.getString(R.string.save),
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog,
										int which) {
									String new_name = ((EditText) changegroup
											.findViewById(R.id.change_group_name_edit))
											.getText().toString();
									changeGroupName(new_name, selectedPosition);
								}
							})
					.setNegativeButton(this.getString(R.string.cancel),
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog,
										int which) {

								}
							}).create().show();
			break;
		case DEL_GROUP_MENU:
			new AlertDialog.Builder(this)
					.setTitle(
							this.getString(R.string.contact_group_manager_context_item_1_dialog_title))
					.setPositiveButton(this.getString(R.string.save),
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog,
										int which) {
									deleteGroup(selectedPosition);
								}
							})
					.setNegativeButton(this.getString(R.string.cancel), null)
					.create().show();
			break;
		}
		return false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		menu.add(Menu.NONE, ADD_GROUP_MENU, Menu.NONE,
				this.getString(R.string.contact_group_manager_options_menu_0));
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (item.getItemId() == ADD_GROUP_MENU) {
			showDialog(DIALOG_ADD_GROUP);
			return true;
		}
		return false;
	}

	private void addGroup(String name) {
		if (TextUtils.isEmpty(name)) {
			Toast.makeText(this, "The group name can't be null",
					Toast.LENGTH_SHORT).show();
			return;
		}
		ContentValues values = new ContentValues();
		values.put(Groups.TITLE, name);
		getContentResolver().insert(Groups.CONTENT_URI, values);
		reloadGroup();
	}

	/* change group name function */
	private void changeGroupName(String name, int selectedPosition) {
		if (TextUtils.isEmpty(name)) {
			Toast.makeText(this, "The new group name can't be null",
					Toast.LENGTH_SHORT).show();
			return;
		}

		Uri uri = ContentUris.withAppendedId(Groups.CONTENT_URI,
				mGroupInfo.get(selectedPosition).mID);
		ContentValues values = new ContentValues();
		values.put(Groups.TITLE, name);
		getContentResolver().update(uri, values, null, null);
		reloadGroup();
	}

	/* delete the selected group */
	protected void deleteGroup(int selectedPosition) {
		getContentResolver().delete(
				Uri.parse(Groups.CONTENT_URI + "?"
						+ ContactsContract.CALLER_IS_SYNCADAPTER + "=true"),
				Groups._ID + "=" + mGroupInfo.get(selectedPosition).mID, null);
		reloadGroup();
	}

	private void reloadGroup() {
		mGroupInfo.clear();
		mGroupTitle.clear();

		initGroupList();
		setListAdapter(myAdapter);
		Log.d(TAG, "reload done");
	}

	private ArrayList<GroupInfo> getContactGroup() {
		ContentResolver cr = this.getContentResolver();
		final String[] GROUP_PROJECTION = new String[] {
				ContactsContract.Groups._ID, ContactsContract.Groups.TITLE };

		Cursor cursor = cr.query(ContactsContract.Groups.CONTENT_URI,
				GROUP_PROJECTION, null, null, null);
		ArrayList<GroupInfo> groupList = new ArrayList<GroupInfo>();
		GroupInfo groupInfo = null;

		if (cursor != null) {
			while (cursor.moveToNext()) {
				groupInfo = new GroupInfo();
				groupInfo.mID = cursor.getInt(cursor.getColumnIndex("_id"));
				groupInfo.mTitle = cursor.getString(cursor
						.getColumnIndex("title"));
				groupList.add(groupInfo);
			}
		}
		cursor.close();
		return groupList;
	}

	class GroupAdapter extends BaseAdapter {
		public GroupAdapter(Context context) {

		}

		public int getCount() {

			return mGroupTitle.size();
		}

		public Object getItem(int position) {

			return position;
		}

		public long getItemId(int position) {

			return position;
		}

		@Override
		public boolean areAllItemsEnabled() {
			return false;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			TextView title = null;

			if (convertView == null || position < mGroupTitle.size()) {
				convertView = LayoutInflater.from(GroupManager.this).inflate(
						R.layout.groupmanager, null);
				title = (TextView) convertView.findViewById(R.id.group_title);
				title.setText(mGroupTitle.get(position));
			}

			return convertView;
		}
	}
}
