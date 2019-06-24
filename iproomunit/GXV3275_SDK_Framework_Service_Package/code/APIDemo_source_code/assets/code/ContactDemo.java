/*****************************************************************************
 *
 * FILENAME:        com.grandstream.gxp2200.demo.ContactDemo.java
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

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

public class ContactDemo extends ListActivity {

	private static String TAG = ContactDemo.class.getSimpleName();;

	private ArrayList<String> mContactsName = new ArrayList<String>();
	private ArrayList<String> mContactsNumber = new ArrayList<String>();
	private ArrayList<Bitmap> mContactsPhonto = new ArrayList<Bitmap>();
	private ArrayList<Integer> mContactsAccount = new ArrayList<Integer>();
	private ArrayList<String> mContactsContactID = new ArrayList<String>();

	private MyListAdapter mMyAdapter;
	private MyHandler mMyHandler;

	private static final int ADD_OPTION_MENU = 0;
	private static final int DEL_ALL_OPTION_MENU = 1;
	private static final int GROUP_OPTION_MENU = 2;

	private static final int VIEW_CONTEXT_MENU = 3;
	private static final int CALL_CONTEXT_MENU = 4;
	private static final int SMS_CONTEXT_MENU = 5;
	private static final int EDIT_CONTEXT_MENU = 6;
	private static final int DELETE_CONTEXT_MENU = 7;

	private static final int INSERT_CONTACT_INTENT_RESULT = 8;
	private static final int EDIT_CONTACT_INTENT_RESULT = 9;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		ListView listview = this.getListView();
		listview.setCacheColorHint(Color.TRANSPARENT);
		mMyHandler = new MyHandler(this);
		mMyAdapter = new MyListAdapter(this);
		MyThread thread = new MyThread();
		new Thread(thread).start();
		
		listview.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> adapterview, View view,
					int position, long id) {
				Uri uri = ContentUris.withAppendedId(
						ContactsContract.Contacts.CONTENT_URI,
						Integer.parseInt(mContactsContactID.get(position)));
				Intent intentView = new Intent(Intent.ACTION_VIEW, uri);
				startActivity(intentView);
			}
		});

		registerForContextMenu(listview);
		super.onCreate(savedInstanceState);
	}

	/* get all the list contacts from contact provide */
	private void getContact() {
		Log.d(TAG, "getContact begin");
		ContentResolver contentResolver = getContentResolver();
		final String[] CONTACT_PROJECTION = new String[] {
				ContactsContract.Contacts._ID,
				ContactsContract.Contacts.HAS_PHONE_NUMBER,
				ContactsContract.Contacts.PHOTO_ID,
				ContactsContract.Contacts.DISPLAY_NAME };

		// get the contact cursor
		Cursor cursor = contentResolver.query(
				ContactsContract.Contacts.CONTENT_URI, CONTACT_PROJECTION,
				null, null, null);

		if (cursor != null) {
			int idColumn = cursor.getColumnIndex(ContactsContract.Contacts._ID);
			int displayNameColumn = cursor
					.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
			int photoColumn = cursor
					.getColumnIndex(ContactsContract.Contacts.PHOTO_ID);
			int hasPhoneNumber = cursor
					.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER);

			while (cursor.moveToNext()) {
				// get contact_id
				String contactId = cursor.getString(idColumn);
				mContactsContactID.add(contactId);

				// get photo
				Long photoId = cursor.getLong(photoColumn);
				Bitmap contactPhoto = null;
				if (photoId > 0) {
					Uri uri = ContentUris.withAppendedId(
							ContactsContract.Contacts.CONTENT_URI,
							Integer.parseInt(contactId));
					InputStream input = ContactsContract.Contacts
							.openContactPhotoInputStream(contentResolver, uri);
					contactPhoto = BitmapFactory.decodeStream(input);
				} else {
					contactPhoto = BitmapFactory.decodeResource(getResources(),
							R.drawable.cu_contacts_user_icon);
				}
				mContactsPhonto.add(contactPhoto);
				// get name
				String disPlayName = cursor.getString(displayNameColumn);
				mContactsName.add(disPlayName);

				// get phone number
				int phoneCount = cursor.getInt(hasPhoneNumber);
				String phoneNumber = null;
				int account = 0;

				if (phoneCount > 0) {
					Cursor phonesCursor = getContentResolver().query(
							ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
							null,
							ContactsContract.CommonDataKinds.Phone.CONTACT_ID
									+ " = " + contactId, null, null);
					if (phonesCursor != null) {
						while (phonesCursor.moveToNext()) {
							phoneNumber = phonesCursor
									.getString(phonesCursor
											.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
							int accountColumn = phonesCursor
									.getColumnIndex(Phone.GS_ACCOUNT);
							account = phonesCursor.getInt(accountColumn);
							break;
						}
						phonesCursor.close();
					}
				}
				mContactsNumber.add(phoneNumber);
				mContactsAccount.add(Integer.valueOf(account));
			}
			cursor.close();
		}
		Log.d(TAG, "getContact end");
	}

	class MyListAdapter extends BaseAdapter {

		public MyListAdapter(Context context) {
		}

		public int getCount() {

			return mContactsContactID.size();
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

			ImageView image = null;
			TextView title = null;
			TextView text = null;

			if (convertView == null || position < mContactsContactID.size()) {
				convertView = LayoutInflater.from(ContactDemo.this).inflate(
						R.layout.contactdemo, null);
				image = (ImageView) convertView.findViewById(R.id.color_image);
				title = (TextView) convertView.findViewById(R.id.list_name);
				text = (TextView) convertView.findViewById(R.id.list_phone);
			}

			title.setText(mContactsName.get(position));
			text.setText(mContactsNumber.get(position));
			image.setImageBitmap(mContactsPhonto.get(position));
			return convertView;
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		int selectedPosition = ((AdapterView.AdapterContextMenuInfo) menuInfo).position;
		menu.setHeaderTitle(mContactsName.get(selectedPosition));
		menu.add(Menu.NONE, VIEW_CONTEXT_MENU, Menu.NONE,
				this.getString(R.string.contact_demo_context_menu_0));
		menu.add(Menu.NONE, CALL_CONTEXT_MENU, Menu.NONE,
				this.getString(R.string.contact_demo_context_menu_1));
		menu.add(Menu.NONE, SMS_CONTEXT_MENU, Menu.NONE,
				this.getString(R.string.contact_demo_context_menu_2));
		menu.add(Menu.NONE, EDIT_CONTEXT_MENU, Menu.NONE,
				this.getString(R.string.contact_demo_context_menu_3));
		menu.add(Menu.NONE, DELETE_CONTEXT_MENU, Menu.NONE,
				this.getString(R.string.contact_demo_context_menu_4));
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {

		final int selectedPosition = ((AdapterView.AdapterContextMenuInfo) item
				.getMenuInfo()).position;

		switch (item.getItemId()) {
		case VIEW_CONTEXT_MENU:
			Uri uriview = ContentUris.withAppendedId(
					ContactsContract.Contacts.CONTENT_URI,
					Integer.parseInt(mContactsContactID.get(selectedPosition)));
			Intent intentView = new Intent(Intent.ACTION_VIEW, uriview);
			startActivity(intentView);
			break;
		case CALL_CONTEXT_MENU:
			Intent intentCall = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
					+ mContactsNumber.get(selectedPosition)));
			intentCall.putExtra(GlobalConfig.ACCOUNT,
					mContactsAccount.get(selectedPosition).intValue());
			startActivity(intentCall);
			break;
		case SMS_CONTEXT_MENU:
		    Uri uri = Uri.parse("smsto:" + mContactsNumber.get(selectedPosition));
			Intent intentSms = new Intent(Intent.ACTION_SENDTO,uri);
			intentSms.putExtra("sms_body","");
			intentSms.putExtra(GlobalConfig.EDITENABLE, true);
			intentSms.putExtra(GlobalConfig.ACCOUNT,
					mContactsAccount.get(selectedPosition).intValue());
			startActivity(intentSms);
			break;
		case EDIT_CONTEXT_MENU:
			Intent intent = new Intent();
			intent.setClass(this, ContactEdit.class);
			intent.putExtra(ContactEdit.CONTACT_ITEM_EDIT_ID,
					mContactsContactID.get(selectedPosition));
			intent.putExtra(ContactEdit.CONTACT_ITEM_EDITABLE, true);
			startActivityForResult(intent, EDIT_CONTACT_INTENT_RESULT);
			break;
		case DELETE_CONTEXT_MENU:
			new AlertDialog.Builder(ContactDemo.this)
					.setTitle(
							this.getString(R.string.contact_demo_context_item_menu_4_title))
					.setPositiveButton(this.getString(R.string.save),
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog,
										int which) {
									Uri uridel = ContentUris
											.withAppendedId(
													ContactsContract.Contacts.CONTENT_URI,
													Integer.parseInt(mContactsContactID
															.get(selectedPosition)));
									delAction(uridel, null, null);
								}
							})
					.setNegativeButton(this.getString(R.string.cancel), null)
					.create().show();
			break;
		default:
			break;
		}

		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, ADD_OPTION_MENU, Menu.NONE,
				this.getString(R.string.contact_demo_options_menu_0));
		menu.add(Menu.NONE, DEL_ALL_OPTION_MENU, Menu.NONE,
				this.getString(R.string.contact_demo_options_menu_1));
		menu.add(Menu.NONE, GROUP_OPTION_MENU, Menu.NONE,
				this.getString(R.string.contact_demo_options_menu_2));

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case ADD_OPTION_MENU:
			insertAction();
			break;
		case DEL_ALL_OPTION_MENU:
			new AlertDialog.Builder(ContactDemo.this)
					.setTitle(
							this.getString(R.string.contact_demo_options_item_menu_1_title))
					.setPositiveButton(this.getString(R.string.save),
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog,
										int which) {
									delActionAll();
								}
							})
					.setNegativeButton(this.getString(R.string.cancel), null)
					.create().show();
			break;
		case GROUP_OPTION_MENU:
			groupManager();
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void groupManager() {

		Intent intent = new Intent();
		intent.setClass(this, GroupManager.class);
		startActivity(intent);
	}

	private void delAction(Uri uri, String where, String[] selectionArgs) {
		ContentResolver resolver = getContentResolver();
		resolver.delete(uri, where, selectionArgs);
		reviewContacts();
	}

	private void reviewContacts() {
		mContactsName.clear();
		mContactsNumber.clear();
		mContactsPhonto.clear();
		mContactsAccount.clear();
		mContactsContactID.clear();
		getContact();
		setListAdapter(mMyAdapter);
	}

	private void delActionAll() {
		ContentResolver cr = getContentResolver();
		Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null,
				null, null, null);
		if (cur != null) {
			while (cur.moveToNext()) {
				try {
					String lookupKey = cur
							.getString(cur
									.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
					Uri uri = Uri.withAppendedPath(
							ContactsContract.Contacts.CONTENT_LOOKUP_URI,
							lookupKey);
					System.out.println("The uri is " + uri.toString());
					cr.delete(uri, null, null);
				} catch (Exception e) {
					System.out.println(e.getStackTrace());
				}
			}
		}
		reviewContacts();
	}

	private void insertAction() {
		Intent intent = new Intent();
		intent.setClass(this, ContactEdit.class);
		startActivityForResult(intent, INSERT_CONTACT_INTENT_RESULT);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		switch (requestCode) {
		case INSERT_CONTACT_INTENT_RESULT:
			if (Activity.RESULT_OK == resultCode) {
				reviewContacts();

			} else if (Activity.RESULT_CANCELED == resultCode)
				Log.d(TAG, "create cancel");
		case EDIT_CONTACT_INTENT_RESULT:
			if (Activity.RESULT_OK == resultCode) {
				reviewContacts();
				Log.d(TAG, "update OK");
			} else if (Activity.RESULT_CANCELED == resultCode)
				Log.d(TAG, "update cancel");
		default:
			return;
		}
	}

	/* Asynchronous add contacts */

	static class MyHandler extends Handler {

		// to solve "This Handler class should be static or leaks might occur"
		// problem

		WeakReference<ContactDemo> mActivity;

		MyHandler(ContactDemo activity) {
			mActivity = new WeakReference<ContactDemo>(activity);
		}

		@Override
		public void handleMessage(Message msg) {

			super.handleMessage(msg);
			if (msg.what == 0) {
				ContactDemo theActivity = mActivity.get();
				if (theActivity != null) {
					theActivity.setListAdapter(theActivity.mMyAdapter);
				}
			}
		}
	}

	class MyThread implements Runnable {
		public void run() {
			getContact();
			ContactDemo.this.mMyHandler.sendEmptyMessage(0);
		}
	}
}
