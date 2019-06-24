/****************************************************************************
 *
 * FILENAME:        com.grandstream.gxp2200.demo.ContactEdit.java
 *
 * LAST REVISION:   $Revision: 1.0
 * LAST MODIFIED:   $Date: Dec 18, 2012
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

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.base.module.account.Account;
import com.base.module.account.AccountManager;
import com.grandstream.gxp2200.demo.R.id;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.GroupMembership;
import android.provider.ContactsContract.CommonDataKinds.Note;
import android.provider.ContactsContract.CommonDataKinds.Photo;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.CommonDataKinds.Website;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Im;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredPostal;
import android.provider.ContactsContract.Groups;
import android.provider.ContactsContract.RawContacts;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ViewSwitcher.ViewFactory;

public class ContactEdit extends Activity implements ViewFactory {
	
	public static final String CONTACT_ITEM_EDITABLE = "edit_contact";
	public static final String CONTACT_ITEM_EDIT_ID = "edit_contact_id";

	private String TAG = ContactEdit.class.getSimpleName();

	private ImageButton mImageButton;

	private Spinner mPhoneTypeSpinner;
	private Spinner mAccountIDSpinner;
	private Spinner mImTypeSpinner;
	private Spinner mAddressTypeSpinner;
	private Spinner mEmailTypeSpinner;
	private Spinner mGroupSpinner;

	private EditText mFamilyNameEditText;
	private EditText mUserNameEditText;
	private EditText mPhoneNumEditText;
	private EditText mEmailEditText;
	private EditText mImEditText;
	private EditText mStreetEditText;
	private EditText mPostOfficeBoxEditText;
	private EditText mNeighborhoodsEditText;
	private EditText mCityEditText;
	private EditText mProvinceEditText;
	private EditText mZipCodeEditText;
	private EditText mCountryEditText;
	private EditText mNotesEditText;
	private EditText mWebsiteEditText;

	private View mImageChooseView;

	private Gallery mGallery;
	private ImageSwitcher mImageSwitcher;
	private int mCurrentImagePosition;
	private int mPreviousImagePosition;
	private AlertDialog mImageChooseDialog;
	boolean mImageChanged;

	private int[] mImages = new int[] { R.drawable.cu_contacts_user_icon,
			R.drawable.image1, R.drawable.image2, R.drawable.image3,
			R.drawable.image4, R.drawable.image5, R.drawable.image6,
			R.drawable.image7, R.drawable.image8, R.drawable.image9,
			R.drawable.image10 };

	private boolean mEdit = false;
	private String mEditcontactId = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.contactsedit);

		mEdit = getIntent().getBooleanExtra(CONTACT_ITEM_EDITABLE, false);
		mEditcontactId = getIntent().getStringExtra(CONTACT_ITEM_EDIT_ID);

		initView();

		if (mEdit) {

			Log.d(TAG, "already contact to edit");
			initEditView();
		}
	}

	private void initView() {

		/* get EditText instance */

		mFamilyNameEditText = (EditText) findViewById(R.id.familyname);
		mUserNameEditText = (EditText) findViewById(R.id.username);
		mPhoneNumEditText = (EditText) findViewById(R.id.mobilephone);
		mEmailEditText = (EditText) findViewById(R.id.email);
		mImEditText = (EditText) findViewById(R.id.im);
		mStreetEditText = (EditText) findViewById(R.id.street);
		mPostOfficeBoxEditText = (EditText) findViewById(R.id.post_office_box);
		mNeighborhoodsEditText = (EditText) findViewById(R.id.neighborhoods);
		mCityEditText = (EditText) findViewById(R.id.city);
		mProvinceEditText = (EditText) findViewById(R.id.province);
		mZipCodeEditText = (EditText) findViewById(R.id.zip_code);
		mCountryEditText = (EditText) findViewById(R.id.country);
		mNotesEditText = (EditText) findViewById(R.id.note);
		mWebsiteEditText = (EditText) findViewById(R.id.website);

		// photo and save ,cancel button listener
		initButton();

		// get spinners init
		initSpinner();
	}

	private void initEditView() {

		String contactId = mEditcontactId;
		if (null == contactId)
			return;

		// get photo
		Uri photoCursor = ContentUris.withAppendedId(Contacts.CONTENT_URI,
				Integer.valueOf(contactId).intValue());
		if (photoCursor != null) {
			InputStream photostream = Contacts.openContactPhotoInputStream(
					getContentResolver(), photoCursor);
			if (photostream != null) {
				Bitmap photo = BitmapFactory.decodeStream(photostream);
				mImageButton.setImageBitmap(photo);
			}
		}

		// get name
		Cursor nameCursor = getContentResolver().query(
				ContactsContract.Data.CONTENT_URI,
				new String[] { Data.CONTACT_ID, StructuredName.DISPLAY_NAME,
						StructuredName.FAMILY_NAME, StructuredName.GIVEN_NAME,
						StructuredName.MIDDLE_NAME },
				ContactsContract.Data.CONTACT_ID + " = " + contactId, null,
				null);
		if (nameCursor != null) {
			while (nameCursor.moveToNext()) {

				// lookup all list name
				String familyname = nameCursor
						.getString(nameCursor
								.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME));
				mFamilyNameEditText.setText(familyname);

				String usename = nameCursor
						.getString(nameCursor
								.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME));
				mUserNameEditText.setText(usename);

				// we read first name for testing ,break the code
				break;
			}
			nameCursor.close();
		}

		// get phone number
		Cursor hasphoneCursor = getContentResolver().query(
				ContactsContract.Contacts.CONTENT_URI,
				new String[] { Contacts._ID, Contacts.HAS_PHONE_NUMBER },
				Contacts._ID + " = " + contactId, null, null);
		int phoneCount = -1;
		if (hasphoneCursor != null) {
			if (hasphoneCursor.moveToFirst()) {
				phoneCount = hasphoneCursor.getInt(hasphoneCursor
						.getColumnIndex(Contacts.HAS_PHONE_NUMBER));
			}
			hasphoneCursor.close();
		}

		if (phoneCount > 0) {

			Cursor phonesCursor = getContentResolver().query(
					ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
					null,
					ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = "
							+ contactId, null, null);
			if (phonesCursor != null) {
				while (phonesCursor.moveToNext()) {
					// list all number
					String phoneNumber = phonesCursor
							.getString(phonesCursor
									.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
					mPhoneNumEditText.setText(phoneNumber);

					int type = phonesCursor
							.getInt(phonesCursor
									.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));

					mPhoneTypeSpinner.setSelection(type);

					// get accountID
					int accountColumn = phonesCursor
							.getColumnIndex(Phone.GS_ACCOUNT);

					int account = phonesCursor.getInt(accountColumn);
					mAccountIDSpinner.setSelection(account);
					break;
				}
				phonesCursor.close();
			}
		}

		// get email
		Cursor emailCursor = getContentResolver().query(
				ContactsContract.CommonDataKinds.Email.CONTENT_URI,
				null,
				ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = "
						+ contactId, null, null);
		if (emailCursor != null) {
			while (emailCursor.moveToNext()) {
				String email = emailCursor
						.getString(emailCursor
								.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA1));
				mEmailEditText.setText(email);

				int type = emailCursor
						.getInt(emailCursor
								.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));
				mEmailTypeSpinner.setSelection(type);
				break;

			}
			emailCursor.close();
		}

		// get postal
		Cursor postalCursor = getContentResolver().query(
				ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI,
				null,
				ContactsContract.CommonDataKinds.StructuredPostal.CONTACT_ID
						+ " = " + contactId + " AND " + Data.MIMETYPE + "='"
						+ StructuredPostal.CONTENT_ITEM_TYPE + "'", null, null);
		if (postalCursor != null) {
			while (postalCursor.moveToNext()) {
				int type = postalCursor
						.getInt(postalCursor
								.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.TYPE));

				mAddressTypeSpinner.setSelection(type);

				String country = postalCursor
						.getString(postalCursor
								.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY));
				mCountryEditText.setText(country);

				String city = postalCursor
						.getString(postalCursor
								.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.CITY));
				mCityEditText.setText(city);

				String street = postalCursor
						.getString(postalCursor
								.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STREET));
				mStreetEditText.setText(street);
				String postcode = postalCursor
						.getString(postalCursor
								.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE));
				mZipCodeEditText.setText(postcode);
				String neighborhood = postalCursor
						.getString(postalCursor
								.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.NEIGHBORHOOD));
				mNeighborhoodsEditText.setText(neighborhood);
				String region = postalCursor
						.getString(postalCursor
								.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.REGION));
				mProvinceEditText.setText(region);
				String pobox = postalCursor
						.getString(postalCursor
								.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POBOX));
				mPostOfficeBoxEditText.setText(pobox);
				break;
			}

			postalCursor.close();
		}

		// get im
		Cursor imCursor = getContentResolver().query(
				Data.CONTENT_URI,
				null,
				ContactsContract.CommonDataKinds.Im.CONTACT_ID + "="
						+ contactId + " AND " + ContactsContract.Data.MIMETYPE
						+ "='"
						+ ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE
						+ "'", null, null);
		if (imCursor != null) {
			while (imCursor.moveToNext()) {

				String IM = imCursor
						.getString(imCursor
								.getColumnIndex(ContactsContract.CommonDataKinds.Im.DATA));
				mImEditText.setText(IM);

				int type = imCursor
						.getInt(imCursor
								.getColumnIndex(ContactsContract.CommonDataKinds.Im.PROTOCOL));
				mImTypeSpinner.setSelection(type + 1);

				break;
			}

			imCursor.close();
		}

		// get website
		Cursor websiteCursor = getContentResolver()
				.query(ContactsContract.Data.CONTENT_URI,

						new String[] { ContactsContract.CommonDataKinds.Website.URL },

						ContactsContract.CommonDataKinds.Website.CONTACT_ID
								+ " = "
								+ contactId
								+ " AND "
								+ ContactsContract.Data.MIMETYPE
								+ "='"
								+ ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE
								+ "'", null, null);
		if (websiteCursor != null) {
			while (websiteCursor.moveToNext()) {

				String website = websiteCursor
						.getString(websiteCursor
								.getColumnIndex(ContactsContract.CommonDataKinds.Website.URL));
				mWebsiteEditText.setText(website);
				break;
			}

			websiteCursor.close();
		}

		// get note
		Cursor noteCursor = getContentResolver()
				.query(ContactsContract.Data.CONTENT_URI,

						new String[] { ContactsContract.CommonDataKinds.Note.NOTE },

						ContactsContract.CommonDataKinds.Note.CONTACT_ID
								+ " = "
								+ contactId
								+ " AND "
								+ ContactsContract.Data.MIMETYPE
								+ "='"
								+ ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE
								+ "'", null, null);
		if (noteCursor != null) {
			while (noteCursor.moveToNext()) {

				String note = noteCursor
						.getString(noteCursor
								.getColumnIndex(ContactsContract.CommonDataKinds.Note.NOTE));
				mNotesEditText.setText(note);
				break;
			}
			noteCursor.close();
		}

		// get group
		Cursor groupCursor = getContentResolver()
				.query(Data.CONTENT_URI,
						null,
						ContactsContract.CommonDataKinds.GroupMembership.CONTACT_ID
								+ "="
								+ contactId
								+ " AND "
								+ ContactsContract.Data.MIMETYPE
								+ "='"
								+ ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE
								+ "'", null, null);
		if (groupCursor != null) {
			while (groupCursor.moveToNext()) {
				String group_id = groupCursor
						.getString(groupCursor
								.getColumnIndex(ContactsContract.CommonDataKinds.GroupMembership.GROUP_ROW_ID));

				String groupName = getGroupName(Integer.valueOf(group_id)
						.longValue());

				int groupcount = mGroupSpinner.getCount();
				for (int i = 0; i < groupcount; i++) {
					String tag = (String) mGroupSpinner.getItemAtPosition(i);

					if (String.valueOf(groupName).equals(tag)) {
						mGroupSpinner.setSelection(i);
						break;
					}
				}
				break;
			}

			groupCursor.close();
		}
	}

	private String getGroupName(long groupid) {
		if (groupid < 0)
			return null;
		ContentResolver cr = this.getContentResolver();
		final String[] GROUP_PROJECTION = new String[] {
				ContactsContract.Groups._ID, ContactsContract.Groups.TITLE };
		Cursor cursor = cr.query(ContactsContract.Groups.CONTENT_URI,
				GROUP_PROJECTION, null, null, null);
		if (cursor != null) {
			for (cursor.moveToFirst(); !(cursor.isAfterLast()); cursor
					.moveToNext()) {
				cursor.getString(cursor.getColumnIndex(Groups.TITLE));
				if (groupid == cursor
						.getLong(cursor.getColumnIndex(Groups._ID)))
					return cursor
							.getString(cursor.getColumnIndex(Groups.TITLE));
			}
		}
		return null;
	}

	private long getGroupId(String groupname) {
		if (!TextUtils.isEmpty(groupname)) {
			ContentResolver cr = this.getContentResolver();
			final String[] GROUP_PROJECTION = new String[] {
					ContactsContract.Groups._ID, ContactsContract.Groups.TITLE };
			Cursor cursor = cr.query(ContactsContract.Groups.CONTENT_URI,
					GROUP_PROJECTION, null, null, null);
			if (cursor != null) {
				for (cursor.moveToFirst(); !(cursor.isAfterLast()); cursor
						.moveToNext()) {
					String name = cursor.getString(cursor
							.getColumnIndex(Groups.TITLE));
					if (groupname.equals(name))
						return cursor
								.getLong(cursor.getColumnIndex(Groups._ID));
				}
			}
		}
		return -1;
	}

	private void initSpinner() {

		mPhoneTypeSpinner = (Spinner) findViewById(R.id.phonetype);
		mImTypeSpinner = (Spinner) findViewById(R.id.imtype);
		mAddressTypeSpinner = (Spinner) findViewById(R.id.addresstype);
		mEmailTypeSpinner = (Spinner) findViewById(R.id.emailtype);
		mGroupSpinner = (Spinner) findViewById(R.id.group);

		/* account spinner */
		mAccountIDSpinner = (Spinner) findViewById(R.id.accountid);
		Account[] accounts = AccountManager.instance().getActiveAccounts(this);
		int size = accounts.length;
		List<String> mList = new ArrayList<String>();
		ArrayAdapter<String> adapter;
		for (int i = 0; i < size; i++)
			mList.add(accounts[i].getAccountName());
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, mList);
		mAccountIDSpinner.setAdapter(adapter);
		mAccountIDSpinner.setPrompt(this
				.getString(R.string.contacts_edit_spinner_account_id_prompt));

		/* phonetype spinner */
		List<String> phonetypeList = new ArrayList<String>();
		int startphonetype = ContactsContract.CommonDataKinds.Phone.TYPE_CUSTOM;
		int endphonetype = ContactsContract.CommonDataKinds.Phone.TYPE_MMS;
		ArrayAdapter<String> phonetypeadapter;

		for (int i = startphonetype; i <= endphonetype; i++) {
			String s = (String) Phone.getTypeLabel(getResources(), i, "");
			phonetypeList.add(s);
		}

		phonetypeadapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, phonetypeList);

		mPhoneTypeSpinner.setAdapter(phonetypeadapter);
		mPhoneTypeSpinner.setPrompt(this
				.getString(R.string.contacts_edit_spinner_phone_type_prompt));

		/* email type spinner */
		List<String> emailList = new ArrayList<String>();
		int startemail = ContactsContract.CommonDataKinds.Email.TYPE_CUSTOM;
		int endemail = ContactsContract.CommonDataKinds.Email.TYPE_MOBILE;
		ArrayAdapter<String> emailadapter;

		for (int i = startemail; i <= endemail; i++) {
			String s = (String) Email.getTypeLabel(getResources(), i, "");
			emailList.add(s);
		}

		emailadapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, emailList);
		mEmailTypeSpinner.setAdapter(emailadapter);
		mEmailTypeSpinner.setPrompt(this
				.getString(R.string.contacts_edit_spinner_email_type_prompt));

		List<String> imProtocolList = new ArrayList<String>();
		int startimpro = Im.PROTOCOL_CUSTOM; // -1
		int endimpro = Im.PROTOCOL_NETMEETING;
		for (int i = startimpro; i <= endimpro; i++) {
			String s = (String) Im.getProtocolLabel(getResources(), i, "");
			imProtocolList.add(s);
		}
		ArrayAdapter<String> imProtocolAdapter;
		imProtocolAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, imProtocolList);
		mImTypeSpinner.setAdapter(imProtocolAdapter);
		mImTypeSpinner.setPrompt(this
				.getString(R.string.contacts_edit_spinner_im_protocol_prompt));

		/* address type spinner */
		List<String> addressList = new ArrayList<String>();
		int startaddress = StructuredPostal.TYPE_CUSTOM;
		int endaddress = StructuredPostal.TYPE_OTHER;
		for (int i = startaddress; i <= endaddress; i++) {
			String s = (String) StructuredPostal.getTypeLabel(getResources(),
					i, "");
			addressList.add(s);
		}
		ArrayAdapter<String> addressAdapter;
		addressAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, addressList);
		mAddressTypeSpinner.setAdapter(addressAdapter);
		mAddressTypeSpinner.setPrompt(this
				.getString(R.string.contacts_edit_spinner_postal_type_prompt));

		/* group spinner */
		mGroupSpinner.setPrompt(this
				.getString(R.string.contacts_edit_spinner_group_prompt));
		List<String> grouptitlelist = new ArrayList<String>();
		ArrayList<GroupInfo> groupinfolist = getContactGroup();
		int groupinfolistsize = groupinfolist.size();
		if (groupinfolistsize > 0) {

			for (int i = 0; i < groupinfolistsize; i++) {
				String s = groupinfolist.get(i).mTitle;
				grouptitlelist.add(s);
			}

			ArrayAdapter<String> groupAdapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_list_item_1, grouptitlelist);
			mGroupSpinner.setAdapter(groupAdapter);
			mGroupSpinner.setSelection(groupinfolistsize - 1);
		}
	}

	private ArrayList<GroupInfo> getContactGroup() {
		ContentResolver cr = this.getContentResolver();
		final String[] GROUP_PROJECTION = new String[] {
				ContactsContract.Groups._ID, ContactsContract.Groups.TITLE };
		Cursor cursor = cr.query(ContactsContract.Groups.CONTENT_URI,
				GROUP_PROJECTION, null, null, null);
		ArrayList<GroupInfo> islist = new ArrayList<GroupInfo>();
		GroupInfo groupInfo = null;
		if (cursor != null) {
			if (cursor.getCount() > 0) {

				int groudIdIndex = cursor.getColumnIndex("_id");
				int groudTitleIndex = cursor.getColumnIndex("title");
				while (cursor.moveToNext()) {
					groupInfo = new GroupInfo();
					groupInfo.mID = cursor.getInt(groudIdIndex);
					groupInfo.mTitle = cursor.getString(groudTitleIndex);
					islist.add(groupInfo);
				}

				// The default group is "No Group"
				GroupInfo ginfo = new GroupInfo();
				ginfo.mTitle = this
						.getString(R.string.contacts_edit_groupinfo_no_group_title);
				ginfo.mID = (islist.size() + 1);
				islist.add(ginfo);
			}
		}
		return islist;
	}

	private void loadImage() {

		if (mImageChooseView == null) {
			LayoutInflater li = LayoutInflater.from(ContactEdit.this);
			mImageChooseView = li.inflate(R.layout.imageswitch, null);
			mGallery = (Gallery) mImageChooseView.findViewById(R.id.gallery);
			// Gallery to load image
			mGallery.setAdapter(new ImageAdapter(this));
			// set the selectde position is middle
			mGallery.setSelection(mImages.length / 2);
			mImageSwitcher = (ImageSwitcher) mImageChooseView
					.findViewById(id.imageswitch);
			mImageSwitcher.setFactory(this);
			mImageSwitcher.setInAnimation(AnimationUtils.loadAnimation(this,
					android.R.anim.fade_in));
			mImageSwitcher.setOutAnimation(AnimationUtils.loadAnimation(this,
					android.R.anim.fade_out));

			mGallery.setOnItemSelectedListener(new OnItemSelectedListener() {

				public void onItemSelected(AdapterView<?> adapterview,
						View view, int positon, long id) {
					// Current avatar position to the selected location
					mCurrentImagePosition = positon;
					mImageSwitcher.setImageResource(mImages[positon
							% mImages.length]);
				}

				public void onNothingSelected(AdapterView<?> arg0) {

				}
			});
		}
	}

	private void initImageChooseDialog() {

		if (mImageChooseDialog == null) {

			// select photo dialog
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(R.string.select_photo_title)
					.setView(mImageChooseView)
					.setPositiveButton(R.string.btn_save,
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog,
										int which) {
									mImageChanged = true;
									mImageButton
											.setImageResource(mImages[mCurrentImagePosition
													% mImages.length]);
								}
							})
					.setNegativeButton(R.string.btn_cancel,
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog,
										int which) {
									mCurrentImagePosition = mPreviousImagePosition;
								}
							});
			mImageChooseDialog = builder.create();
		}
	}

	class ImageAdapter extends BaseAdapter {
		private Context context;

		public ImageAdapter(Context context) {
			this.context = context;
		}

		public int getCount() {

			return Integer.MAX_VALUE;
		}

		public Object getItem(int position) {

			return position;
		}

		public long getItemId(int position) {

			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView iv = new ImageView(context);
			iv.setImageResource(mImages[position % mImages.length]);
			iv.setAdjustViewBounds(true);
			iv.setLayoutParams(new Gallery.LayoutParams(80, 80));
			iv.setPadding(15, 10, 15, 10);
			return iv;
		}
	}

	private void initButton() {

		/* Avatar Image button to listen to events */
		mImageButton = (ImageButton) findViewById(R.id.image_button);
		mImageButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				loadImage();
				initImageChooseDialog();
				mImageChooseDialog.show();
			}
		});

		// Save the configuration
		Button btnSave = (Button) findViewById(R.id.btn_save);
		btnSave.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (mEdit)
					updateContact();
				else
					saveContacts();
			}
		});

		// Return but not save
		Button btnCancel = (Button) findViewById(R.id.btn_return);
		btnCancel.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				setResult(Activity.RESULT_CANCELED);
				ContactEdit.this.finish();
			}
		});
	}

	private void saveContacts() {

		ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

		ops.add(ContentProviderOperation
				.newInsert(ContactsContract.RawContacts.CONTENT_URI)

				.withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)

				.withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)

				.withValue(ContactsContract.RawContacts.AGGREGATION_MODE,
						ContactsContract.RawContacts.AGGREGATION_MODE_DISABLED)

				.build());

		// add a new record andr RAW_CONTACT_ID=0

		String namefamily = mFamilyNameEditText.getText().toString();
		String nameuser = mUserNameEditText.getText().toString();

		if (!TextUtils.isEmpty(namefamily) || !TextUtils.isEmpty(nameuser)) {

			ops.add(ContentProviderOperation
					.newInsert(ContactsContract.Data.CONTENT_URI)
					.withValueBackReference(
							ContactsContract.Data.RAW_CONTACT_ID, 0)
					.withValue(
							ContactsContract.Data.MIMETYPE,
							ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
					.withValue(
							ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME,
							namefamily)
					.withValue(
							ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME,
							nameuser).build());
		}

		// add phone
		String phone = mPhoneNumEditText.getText().toString().trim();
		int account = mAccountIDSpinner.getSelectedItemPosition();
		int phonetype = mPhoneTypeSpinner.getSelectedItemPosition();
		if (!TextUtils.isEmpty(phone)) {

			ops.add(ContentProviderOperation
					.newInsert(ContactsContract.Data.CONTENT_URI)
					.withValueBackReference(
							ContactsContract.Data.RAW_CONTACT_ID, 0)
					.withValue(
							ContactsContract.Data.MIMETYPE,
							ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
					.withValue(ContactsContract.CommonDataKinds.Phone.NUMBER,
							phone)
					.withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
							phonetype)
					.withValue(
							ContactsContract.CommonDataKinds.Phone.GS_ACCOUNT,
							account).build());
		}

		// add email
		String email = mEmailEditText.getText().toString();
		int emailtype = mEmailTypeSpinner.getSelectedItemPosition();

		if (!TextUtils.isEmpty(email)) {

			ops.add(ContentProviderOperation
					.newInsert(ContactsContract.Data.CONTENT_URI)
					.withValueBackReference(
							ContactsContract.Data.RAW_CONTACT_ID, 0)
					.withValue(
							ContactsContract.Data.MIMETYPE,
							ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
					.withValue(ContactsContract.CommonDataKinds.Email.DATA,
							email)
					.withValue(ContactsContract.CommonDataKinds.Email.TYPE,
							emailtype).build());
		}

		// add website
		String website = mWebsiteEditText.getText().toString();

		if (!TextUtils.isEmpty(website)) {

			ops.add(ContentProviderOperation
					.newInsert(ContactsContract.Data.CONTENT_URI)
					.withValueBackReference(
							ContactsContract.Data.RAW_CONTACT_ID, 0)
					.withValue(
							ContactsContract.Data.MIMETYPE,
							ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE)
					.withValue(ContactsContract.CommonDataKinds.Website.URL,
							website)
					.withValue(ContactsContract.CommonDataKinds.Website.TYPE,
							ContactsContract.CommonDataKinds.Website.TYPE_WORK)
					.build());
		}

		// add IM
		String im = mImEditText.getText().toString();
		int improtocol = mImTypeSpinner.getSelectedItemPosition();

		if (!TextUtils.isEmpty(im)) {
			ops.add(ContentProviderOperation

					.newInsert(ContactsContract.Data.CONTENT_URI)
					.withValueBackReference(
							ContactsContract.Data.RAW_CONTACT_ID, 0)
					.withValue(
							ContactsContract.Data.MIMETYPE,
							ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE)
					.withValue(ContactsContract.CommonDataKinds.Im.DATA1, im)
					.withValue(ContactsContract.CommonDataKinds.Im.PROTOCOL,
							improtocol - 1).build());
		}

		// add address
		String street = mStreetEditText.getText().toString();
		String pobox = mPostOfficeBoxEditText.getText().toString();
		String neighborhoods = mNeighborhoodsEditText.getText().toString();
		String city = mCityEditText.getText().toString();
		String province = mProvinceEditText.getText().toString();
		String zipcode = mZipCodeEditText.getText().toString();
		String country = mCountryEditText.getText().toString();
		int addresstype = mAddressTypeSpinner.getSelectedItemPosition();

		if (!TextUtils.isEmpty(country) || !TextUtils.isEmpty(zipcode)
				|| !TextUtils.isEmpty(province) || !TextUtils.isEmpty(city)
				|| !TextUtils.isEmpty(neighborhoods)
				|| !TextUtils.isEmpty(pobox) || !TextUtils.isEmpty(street)) {
			ops.add(ContentProviderOperation
					.newInsert(ContactsContract.Data.CONTENT_URI)
					.withValueBackReference(
							ContactsContract.Data.RAW_CONTACT_ID, 0)
					.withValue(
							ContactsContract.Data.MIMETYPE,
							ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE)
					.withValue(
							ContactsContract.CommonDataKinds.StructuredPostal.TYPE,
							addresstype)
					.withValue(
							ContactsContract.CommonDataKinds.StructuredPostal.STREET,
							street)
					.withValue(
							ContactsContract.CommonDataKinds.StructuredPostal.POBOX,
							pobox)
					.withValue(
							ContactsContract.CommonDataKinds.StructuredPostal.NEIGHBORHOOD,
							neighborhoods)
					.withValue(
							ContactsContract.CommonDataKinds.StructuredPostal.CITY,
							city)
					.withValue(
							ContactsContract.CommonDataKinds.StructuredPostal.REGION,
							province)
					.withValue(
							ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE,
							zipcode)
					.withValue(
							ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY,
							country).build());
		}

		// add note
		String notes = mNotesEditText.getText().toString();

		if (!TextUtils.isEmpty(notes)) {
			ops.add(ContentProviderOperation
					.newInsert(ContactsContract.Data.CONTENT_URI)
					.withValueBackReference(
							ContactsContract.Data.RAW_CONTACT_ID, 0)
					.withValue(
							ContactsContract.Data.MIMETYPE,
							ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE)
					.withValue(ContactsContract.CommonDataKinds.Note.NOTE,
							notes).build());
		}

		// add photo image
		Bitmap bm = null;
		int imageid;
		if (mImageChanged) {

			imageid = mImages[mCurrentImagePosition % mImages.length];
		} else {

			imageid = mImages[mPreviousImagePosition % mImages.length];
		}

		bm = BitmapFactory.decodeResource(getResources(), imageid);

		if (bm != null) {

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
			byte[] photo = baos.toByteArray();

			if (photo != null) {

				ops.add(ContentProviderOperation
						.newInsert(ContactsContract.Data.CONTENT_URI)

						.withValueBackReference(
								ContactsContract.Data.RAW_CONTACT_ID, 0)
						.withValue(
								ContactsContract.Data.MIMETYPE,
								ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)
						.withValue(
								ContactsContract.CommonDataKinds.Photo.PHOTO,
								photo).build());
			}
		}

		// add group
		String grouptitle = (String) mGroupSpinner.getSelectedItem().toString();
		long group_id = getGroupId(grouptitle);
		boolean noGroup = false;
		if ("No Group".equals(grouptitle)) {
			noGroup = true;
		}

		if (!noGroup) {
			ops.add(ContentProviderOperation
					.newInsert(ContactsContract.Data.CONTENT_URI)
					.withValueBackReference(
							ContactsContract.Data.RAW_CONTACT_ID, 0)
					.withValue(
							ContactsContract.Data.MIMETYPE,
							ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE)
					.withValue(
							ContactsContract.CommonDataKinds.GroupMembership.GROUP_ROW_ID,
							group_id).build());
		}

		// do batch
		try {
			getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
			Toast.makeText(this, "Add contact successful", Toast.LENGTH_SHORT)
					.show();
			setResult(Activity.RESULT_OK);
			finish();
		} catch (Exception e) {
			Toast.makeText(this, "Add contact fail", Toast.LENGTH_SHORT).show();
		}
	}

	private void updateContact() {

		// get contact
		String id = String.valueOf(mEditcontactId);
		Cursor c = getContentResolver().query(RawContacts.CONTENT_URI,
				new String[] { RawContacts._ID },
				RawContacts.CONTACT_ID + "=?",
				new String[] { String.valueOf(id) }, null);
		long rawid = -1;
		if (c != null) {
			if (c.moveToFirst()) {
				rawid = c.getInt(c.getColumnIndex(RawContacts._ID));
			}
			c.close();
		}

		ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

		// update family name and given name
		String family_name = mFamilyNameEditText.getText().toString();
		String given_name = mUserNameEditText.getText().toString();

		Cursor nameCurosr = getContentResolver().query(
				Data.CONTENT_URI,
				null,
				Data.CONTACT_ID + "=" + id + " AND " + Data.MIMETYPE + "='"
						+ StructuredName.CONTENT_ITEM_TYPE + "'", null, null);

		int nameId = -1;
		if (nameCurosr != null) {
			if (nameCurosr.moveToFirst()) {
				int nameIdIdx = nameCurosr.getColumnIndex(Data._ID);
				nameId = nameCurosr.getInt(nameIdIdx);
			}
			nameCurosr.close();
		}

		if (nameId >= 0) {
			ops.add(ContentProviderOperation
					.newUpdate(ContactsContract.Data.CONTENT_URI)
					.withSelection(
							Data.CONTACT_ID + "=?" + " AND "
									+ ContactsContract.Data.MIMETYPE + " = ?",
							new String[] {
									String.valueOf(id),
									ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE })
					.withValue(
							ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME,
							family_name)
					.withValue(
							ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME,
							given_name).build());
		} else {
			ContentValues values = new ContentValues();
			values.put(Data.RAW_CONTACT_ID, rawid);
			values.put(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE);
			values.put(StructuredName.FAMILY_NAME, family_name);
			values.put(StructuredName.GIVEN_NAME, given_name);
			getContentResolver().insert(Data.CONTENT_URI, values);
		}

		// up phone
		String phone_num = mPhoneNumEditText.getText().toString();
		int phone_type = mPhoneTypeSpinner.getSelectedItemPosition();
		int account = mAccountIDSpinner.getSelectedItemPosition();

		Cursor phoneCurosr = getContentResolver().query(
				Data.CONTENT_URI,
				null,
				Data.CONTACT_ID + "=" + id + " AND " + Data.MIMETYPE + "='"
						+ Phone.CONTENT_ITEM_TYPE + "'", null, null);

		int phoneId = -1;
		if (phoneCurosr != null) {
			if (phoneCurosr.moveToFirst()) {
				int phoneIdIdx = phoneCurosr.getColumnIndex(Data._ID);
				phoneId = phoneCurosr.getInt(phoneIdIdx);
			}
			phoneCurosr.close();
		}

		if (phoneId >= 0) {
			ops.add(ContentProviderOperation
					.newUpdate(ContactsContract.Data.CONTENT_URI)

					.withSelection(
							Data.CONTACT_ID + "=?" + " AND "
									+ ContactsContract.Data.MIMETYPE + " = ?",
							new String[] { String.valueOf(id),
									Phone.CONTENT_ITEM_TYPE })

					.withValue(Phone.TYPE, String.valueOf(phone_type))
					.withValue(Phone.NUMBER, phone_num)
					.withValue(Phone.GS_ACCOUNT, String.valueOf(account))
					.build());
		} else {
			ContentValues values = new ContentValues();
			values.put(Data.RAW_CONTACT_ID, rawid);
			values.put(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE);
			values.put(Phone.NUMBER, phone_num);
			values.put(Phone.TYPE, String.valueOf(phone_type));
			values.put(Phone.GS_ACCOUNT, String.valueOf(account));
			getContentResolver().insert(Data.CONTENT_URI, values);
		}

		// up email
		String email = mEmailEditText.getText().toString();
		int email_type = mEmailTypeSpinner.getSelectedItemPosition();

		Cursor emailCurosr = getContentResolver().query(
				Data.CONTENT_URI,
				null,
				Data.CONTACT_ID + "=" + id + " AND " + Data.MIMETYPE + "='"
						+ Email.CONTENT_ITEM_TYPE + "'", null, null);

		int emailId = -1;
		if (emailCurosr != null) {
			if (emailCurosr.moveToFirst()) {
				int emailIdIdx = emailCurosr.getColumnIndex(Data._ID);
				emailId = emailCurosr.getInt(emailIdIdx);
			}
			emailCurosr.close();
		}

		if (emailId >= 0) {
			ops.add(ContentProviderOperation
					.newUpdate(ContactsContract.Data.CONTENT_URI)

					.withSelection(
							Data.CONTACT_ID + "=?" + " AND "
									+ ContactsContract.Data.MIMETYPE + " = ?",

							new String[] { String.valueOf(id),
									Email.CONTENT_ITEM_TYPE })

					.withValue(Email.DATA, email)
					.withValue(Email.TYPE, String.valueOf(email_type)).build());
		} else {
			ContentValues values = new ContentValues();
			values.put(Data.RAW_CONTACT_ID, rawid);
			values.put(Data.MIMETYPE, Email.CONTENT_ITEM_TYPE);
			values.put(Email.TYPE, String.valueOf(email_type));
			values.put(Email.DATA, email);
			getContentResolver().insert(Data.CONTENT_URI, values);
		}

		// up im
		int im_type = mImTypeSpinner.getSelectedItemPosition();
		String im = mImEditText.getText().toString();

		Cursor imCursor = getContentResolver().query(
				Data.CONTENT_URI,
				null,
				Data.CONTACT_ID + "=" + id + " AND " + Data.MIMETYPE + "='"
						+ Im.CONTENT_ITEM_TYPE + "'", null, null);

		int imId = -1;
		if (imCursor != null) {
			if (imCursor.moveToFirst()) {
				int imIdIdx = imCursor.getColumnIndexOrThrow(Data._ID);
				imId = imCursor.getInt(imIdIdx);
			}
			imCursor.close();
		}

		if (imId >= 0) {
			ops.add(ContentProviderOperation
					.newUpdate(Data.CONTENT_URI)
					.withSelection(
							Data.CONTACT_ID + "=?" + " AND "
									+ ContactsContract.Data.MIMETYPE + " = ?",
							new String[] { String.valueOf(id),
									Im.CONTENT_ITEM_TYPE })
					.withValue(Im.PROTOCOL, String.valueOf(im_type - 1))
					.withValue(Im.DATA, im).build());
		} else {
			ContentValues values = new ContentValues();
			values.put(Data.RAW_CONTACT_ID, rawid);
			values.put(Data.MIMETYPE, Im.CONTENT_ITEM_TYPE);
			values.put(Im.PROTOCOL, String.valueOf(im_type - 1));
			values.put(Im.DATA, im); /* load head portrait */
			getContentResolver().insert(Data.CONTENT_URI, values);
		}

		// up postal
		int posttype = mAddressTypeSpinner.getSelectedItemPosition();
		String street = mStreetEditText.getText().toString();
		String post_office_box = mPostOfficeBoxEditText.getText().toString();
		String neightbor_hoods = mNeighborhoodsEditText.getText().toString();
		String city = mCityEditText.getText().toString();
		String province = mProvinceEditText.getText().toString();
		String zip_code = mZipCodeEditText.getText().toString();
		String country = mCountryEditText.getText().toString();

		Cursor postCursor = getContentResolver().query(
				Data.CONTENT_URI,
				null,
				Data.CONTACT_ID + "=" + id + " AND " + Data.MIMETYPE + "='"
						+ StructuredPostal.CONTENT_ITEM_TYPE + "'", null, null);

		int postId = -1;
		if (postCursor != null) {
			if (postCursor.moveToFirst()) {
				int postIdIdx = postCursor.getColumnIndexOrThrow(Data._ID);
				postId = postCursor.getInt(postIdIdx);
			}

			postCursor.close();
		}

		if (postId >= 0) {
			ops.add(ContentProviderOperation
					.newUpdate(Data.CONTENT_URI)
					.withSelection(
							Data.CONTACT_ID + "=?" + " AND "
									+ ContactsContract.Data.MIMETYPE + " = ?",
							new String[] { String.valueOf(id),
									StructuredPostal.CONTENT_ITEM_TYPE })
					.withValue(StructuredPostal.STREET, street)
					.withValue(StructuredPostal.POBOX, post_office_box)
					.withValue(StructuredPostal.NEIGHBORHOOD, neightbor_hoods)
					.withValue(StructuredPostal.CITY, city)
					.withValue(StructuredPostal.REGION, province)
					.withValue(StructuredPostal.POSTCODE, zip_code)
					.withValue(StructuredPostal.COUNTRY, country)
					.withValue(StructuredPostal.TYPE, String.valueOf(posttype))
					.build());

		} else {
			ContentValues values = new ContentValues();
			values.put(Data.RAW_CONTACT_ID, rawid);
			values.put(Data.MIMETYPE, StructuredPostal.CONTENT_ITEM_TYPE);
			values.put(StructuredPostal.STREET, street);
			values.put(StructuredPostal.POBOX, post_office_box);
			values.put(StructuredPostal.NEIGHBORHOOD, neightbor_hoods);
			values.put(StructuredPostal.CITY, city);
			values.put(StructuredPostal.REGION, province);
			values.put(StructuredPostal.POSTCODE, zip_code);
			values.put(StructuredPostal.COUNTRY, country);
			values.put(StructuredPostal.TYPE, String.valueOf(posttype));
			getContentResolver().insert(Data.CONTENT_URI, values);
		}

		// up note
		String note = mNotesEditText.getText().toString();
		Cursor noteCursor = getContentResolver().query(
				Data.CONTENT_URI,
				null,
				Note.CONTACT_ID + "=" + id + " AND " + Data.MIMETYPE + "='"
						+ Note.CONTENT_ITEM_TYPE + "'", null, null);

		int noteRow = -1;
		if (noteCursor != null) {
			if (noteCursor.moveToFirst()) {
				int noteIdIdx = noteCursor
						.getColumnIndexOrThrow(ContactsContract.Data._ID);
				noteRow = noteCursor.getInt(noteIdIdx);
			}

			noteCursor.close();
		}

		if (noteRow >= 0) {
			ops.add(ContentProviderOperation
					.newUpdate(ContactsContract.Data.CONTENT_URI)

					.withSelection(
							Data.CONTACT_ID + "=?" + " AND "
									+ ContactsContract.Data.MIMETYPE + " = ?",

							new String[] { String.valueOf(id),
									Note.CONTENT_ITEM_TYPE })

					.withValue(Note.NOTE, note)

					.build());
		} else {
			ContentValues values = new ContentValues();
			values.put(Data.RAW_CONTACT_ID, rawid);
			values.put(Data.MIMETYPE, Note.CONTENT_ITEM_TYPE);
			values.put(Note.NOTE, note);
			getContentResolver().insert(ContactsContract.Data.CONTENT_URI,
					values);
		}

		// up website
		String website = mWebsiteEditText.getText().toString();

		Cursor websiteCursor = getContentResolver()
				.query(Data.CONTENT_URI,
						null,
						ContactsContract.CommonDataKinds.Website.CONTACT_ID
								+ "="
								+ id
								+ " AND "
								+ ContactsContract.Data.MIMETYPE
								+ "='"
								+ ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE
								+ "'", null, null);

		int webRow = -1;
		if(websiteCursor != null){
			if (websiteCursor.moveToFirst()) {
				int webidIdx = websiteCursor
						.getColumnIndexOrThrow(ContactsContract.Data._ID);
				webRow = websiteCursor.getInt(webidIdx);
			}
			websiteCursor.close();
		}	

		if (webRow >= 0) {
			ops.add(ContentProviderOperation
					.newUpdate(ContactsContract.Data.CONTENT_URI)

					.withSelection(
							Data.CONTACT_ID + "=?" + " AND "
									+ ContactsContract.Data.MIMETYPE + " = ?",

							new String[] { String.valueOf(id),
									Website.CONTENT_ITEM_TYPE })

					.withValue(Website.URL, website)

					.build());
		} else {
			ContentValues values = new ContentValues();
			values.put(ContactsContract.Data.RAW_CONTACT_ID, rawid);
			values.put(ContactsContract.CommonDataKinds.Website.URL, website);
			values.put(ContactsContract.Data.MIMETYPE,
					ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE);
			getContentResolver().insert(ContactsContract.Data.CONTENT_URI,
					values);
		}

		// up group
		String group_title = mGroupSpinner.getSelectedItem().toString();
		boolean noGroup = false;
		if ("No Group".equals(group_title)) {
			noGroup = true;
		}
		long group_id = getGroupId(group_title);

		Cursor groupCursor = getContentResolver()
				.query(Data.CONTENT_URI,
						null,
						ContactsContract.CommonDataKinds.GroupMembership.CONTACT_ID
								+ "="
								+ id
								+ " AND "
								+ ContactsContract.Data.MIMETYPE
								+ "='"
								+ ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE
								+ "'", null, null);

		int groupRow = -1;
		String idgroup = null;
		if(groupCursor != null){
			if (groupCursor.moveToFirst()) {
				int rowidIdx = groupCursor
						.getColumnIndexOrThrow(ContactsContract.Data._ID);
				groupRow = groupCursor.getInt(rowidIdx);
				idgroup = groupCursor.getString(groupCursor
						.getColumnIndex(GroupMembership.GROUP_ROW_ID));
			}
			groupCursor.close();
		}
		
		// already exit ,update
		if (groupRow >= 0) {
			if (noGroup) {
				
				getContentResolver().delete(
						ContactsContract.Data.CONTENT_URI,
						ContactsContract.Data.MIMETYPE + " =?" + " AND "
								+ GroupMembership.GROUP_ROW_ID + "=?",
						new String[] { GroupMembership.CONTENT_ITEM_TYPE,
								idgroup });
			} else {
				ops.add(ContentProviderOperation
						.newUpdate(ContactsContract.Data.CONTENT_URI)
						.withSelection(
								Data.CONTACT_ID + "=?" + " AND "
										+ ContactsContract.Data.MIMETYPE
										+ " =?",
								new String[] { String.valueOf(id),
										GroupMembership.CONTENT_ITEM_TYPE })
						.withValue(GroupMembership.GROUP_ROW_ID, group_id)
						.build());
			}
		} else if (!noGroup) {
			ContentValues values = new ContentValues();
			values.put(ContactsContract.Data.RAW_CONTACT_ID, rawid);
			values.put(
					ContactsContract.CommonDataKinds.GroupMembership.GROUP_ROW_ID,
					group_id);
			values.put(
					ContactsContract.Data.MIMETYPE,
					ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE);
			getContentResolver().insert(ContactsContract.Data.CONTENT_URI,
					values);
		}
		

		// up photo
		Bitmap bm = null;
		int imageid;
		if (mImageChanged) {
			imageid = mImages[mCurrentImagePosition % mImages.length];
			bm = BitmapFactory.decodeResource(getResources(), imageid);

			if (bm != null) {

				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
				byte[] photo = baos.toByteArray();

				if (photo != null) {

					ops.add(ContentProviderOperation
							.newUpdate(ContactsContract.Data.CONTENT_URI)
							.withSelection(
									Data.CONTACT_ID + "=?" + " AND "
											+ ContactsContract.Data.MIMETYPE
											+ " =?",
									new String[] { String.valueOf(id),
											Photo.CONTENT_ITEM_TYPE })
							.withValue(
									ContactsContract.CommonDataKinds.Photo.PHOTO,
									photo).build());
				}
			}
		}

		// do batch
		try {

			getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
			setResult(Activity.RESULT_OK);
			Toast.makeText(ContactEdit.this, "Update contact successful ",
					Toast.LENGTH_SHORT).show();
			Log.d("AddContacts", "update ok");
			finish();
		} catch (Exception e) {
			Toast.makeText(ContactEdit.this, "Update Contact fail ",
					Toast.LENGTH_SHORT).show();
			Log.d("AddContacts", "update fail");
		}
	}

	public View makeView() {

		ImageView view = new ImageView(this);
		view.setBackgroundColor(0xff000000);
		// Control image's size and moving to match the size of this ImageView
		view.setScaleType(ScaleType.FIT_CENTER);
		// Set the picture size is: 90 * 90
		view.setLayoutParams(new ImageSwitcher.LayoutParams(90, 90));
		return view;
	}

	/* recovery resources when exit */
	@Override
	protected void onDestroy() {

		if (mImageSwitcher != null)
			mImageSwitcher = null;
		if (mGallery != null)
			mGallery = null;
		if (mImageChooseDialog != null)
			mImageChooseDialog = null;
		if (mImageChooseView != null)
			mImageChooseView = null;
		if (mImageButton != null)
			mImageButton = null;
		super.onDestroy();
	}
}
