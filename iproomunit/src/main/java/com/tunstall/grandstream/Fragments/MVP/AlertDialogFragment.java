package com.tunstall.grandstream.Fragments.MVP;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tunstall.com.R;
import com.tunstall.grandstream.AppSettings;
import com.tunstall.grandstream.SettingsActivity;

//import constants

import static com.tunstall.grandstream.Storage.constants.PREF_SETTINGS_PIN;

public class AlertDialogFragment extends DialogFragment implements AlertDialogue_Contract.View {

	private static final String SETTINGS_PIN_CODE = "3041";

	public static final int DIALOG_DATA_NOT_RECEIVED = 1;
	public static final int DIALOG_CONFIGURE_SETTINGS = 2;
	public static final int DIALOG_IP_ISSUE = 3;
	public static final int DIALOG_RESPONSE_ERROR = 4;
	public static final int DIALOG_SETTINGS_PIN_CODE = 5;
	public static final int DIALOG_SOMETHING_WENT_WRONG = 6;
	public static final int DIALOG_ERROR_AUTH_VIDEO_SERVER = 7;
	
	private static AppSettings mAppSettings;


	// Can create a new presenter each time a new AlertFragment is created in the main activity.
	//This presenter will build the dialogue and use a callback in order to show the callback.
	//i have taken away the logic from the fragment itself and have contained it inside of the presenter instead

	public static AlertDialogFragment newInstance(int dialogId) {
		AlertDialogFragment frag = new AlertDialogFragment();
		Bundle args = new Bundle();
		args.putInt("dialogId", dialogId);
		frag.setArguments(args);
		return frag;
	}
	
	public static AlertDialogFragment newInstanceSettings(int dialogId, AppSettings test) {
		mAppSettings = test;
		AlertDialogFragment frag = new AlertDialogFragment();
		Bundle args = new Bundle();
		args.putInt("dialogId", dialogId);
		frag.setArguments(args);
		return frag;
	}

	public static AlertDialogFragment newInstance(int dialogId, String message) {
		AlertDialogFragment frag = new AlertDialogFragment();
		Bundle args = new Bundle();
		args.putInt("dialogId", dialogId);
		args.putString("message", message);
		frag.setArguments(args);
		return frag;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

//		int dialogId = getArguments().getInt("dialogId");
//		// Message is usually null, only used for a few dialogs
//		String message = getArguments().getString("message");
//
//		Dialog dialog = null;
//		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//
//		switch (dialogId) {
//		case DIALOG_DATA_NOT_RECEIVED:
//			builder.setTitle(R.string.error)
//					.setMessage(R.string.error_no_connectivity)
//					.setCancelable(true)
//					.setPositiveButton(R.string.buttonok,
//							new DialogInterface.OnClickListener() {
//								public void onClick(DialogInterface dialog,
//										int whichButton) {
//									dialog.dismiss();
//								}
//							});
//			dialog = builder.create();
//			dialog.setCanceledOnTouchOutside(false);
//			break;
//
//		case DIALOG_IP_ISSUE:
//			builder.setTitle(R.string.error)
//					.setMessage(R.string.error_chek_ip)
//					.setCancelable(false)
//					.setPositiveButton(R.string.buttonok,
//							new DialogInterface.OnClickListener() {
//								public void onClick(DialogInterface dialog,
//										int id) {
//									dialog.dismiss();
//
//								}
//							});
//
//			dialog = builder.create();
//			dialog.setCanceledOnTouchOutside(false);
//			break;
//
//		case DIALOG_CONFIGURE_SETTINGS:
//			builder.setTitle(R.string.error)
//					.setMessage(R.string.error_missing_parameters)
//					.setCancelable(false)
//					.setPositiveButton(R.string.buttonok,
//							new DialogInterface.OnClickListener() {
//								public void onClick(DialogInterface dialog,
//										int id) {
//									dialog.dismiss();
//									Intent i = new Intent(getActivity(),
//											SettingsActivity.class);
//									startActivity(i);
//
//								}
//							});
//
//			dialog = builder.create();
//			dialog.setCanceledOnTouchOutside(false);
//			break;
//
//		case DIALOG_RESPONSE_ERROR:
//			builder.setTitle(R.string.error)
//					.setMessage(message)
//					.setCancelable(false)
//					.setPositiveButton(R.string.buttonok,
//							new DialogInterface.OnClickListener() {
//								public void onClick(DialogInterface dialog,
//										int id) {
//									dialog.dismiss();
//								}
//							});
//
//			dialog = builder.create();
//			dialog.setCanceledOnTouchOutside(false);
//			break;
//
//		case DIALOG_SOMETHING_WENT_WRONG:
//			builder.setTitle(R.string.error)
//					.setMessage(R.string.error_video_streaming)
//					.setCancelable(false)
//					.setPositiveButton(getText(R.string.buttonok),
//							new DialogInterface.OnClickListener() {
//								public void onClick(DialogInterface dialog,
//										int id) {
//									dialog.dismiss();
//								}
//							});
//
//			dialog = builder.create();
//			dialog.setCanceledOnTouchOutside(false);
//			break;
//
//		case DIALOG_ERROR_AUTH_VIDEO_SERVER:
//			builder.setTitle(getText(R.string.error))
//					.setMessage(R.string.error_video_authentication)
//					.setCancelable(false)
//					.setPositiveButton(getText(R.string.buttonok),
//							new DialogInterface.OnClickListener() {
//								public void onClick(DialogInterface dialog,
//										int id) {
//									dialog.dismiss();
//								}
//							});
//
//			dialog = builder.create();
//			dialog.setCanceledOnTouchOutside(false);
//			break;
//
//		case DIALOG_SETTINGS_PIN_CODE:
//			final EditText input = new EditText(getActivity());
//			// input.setInputType(EditorInfo.TYPE_NUMBER_VARIATION_PASSWORD);
//			input.setInputType(InputType.TYPE_CLASS_NUMBER);
//			input.setTransformationMethod(PasswordTransformationMethod
//					.getInstance());
//
//			final AlertDialog pinDialog = builder
//					.setTitle(R.string.enter_pin_code)
//					.setView(input)
//					.setCancelable(true)
//					.setPositiveButton(R.string.buttonok, null)
//					// Handling is done in pinDialog.setOnShowListener below
//					.setNegativeButton(R.string.button_cancel,
//							new DialogInterface.OnClickListener() {
//								public void onClick(DialogInterface dialog,
//										int whichButton) {
//									dialog.dismiss();
//								}
//							}).create();
//
//			pinDialog.setCanceledOnTouchOutside(false);
//
//			input.setOnEditorActionListener(new EditText.OnEditorActionListener() {
//				@Override
//				public boolean onEditorAction(TextView v, int actionId,
//						KeyEvent event) {
//					if (actionId == EditorInfo.IME_ACTION_DONE) {
//						verifyPinCode(pinDialog, input);
//						// The keyboard will disappear when the dialog is
//						// dismissed, that is if the correct pin was entered, so
//						// no need to return true here
//					}
//					return false;
//				}
//			});
//
//			// Handling the positive button press here to avoid the alert from
//			// being dismissed if the entered pin code is wrong
//			pinDialog.setOnShowListener(new DialogInterface.OnShowListener() {
//
//				@Override
//				public void onShow(final DialogInterface d) {
//
//					Button btnOk = pinDialog
//							.getButton(AlertDialog.BUTTON_POSITIVE);
//					btnOk.setOnClickListener(new View.OnClickListener() {
//
//						@Override
//						public void onClick(View view) {
//							verifyPinCode(pinDialog, input);
//						}
//					});
//				}
//			});
//
//			dialog = pinDialog;
//
//			break;
		}

		return dialog;
	}


	//needs encryption!!

	private void verifyPinCode(AlertDialog dialog, EditText input) {
		Editable value = input.getText();


		// here we need to use a Shared Preferences interactor in order to check if pin entered is correct

		if (value.toString().equals(mAppSettings.getStringForKey(PREF_SETTINGS_PIN))) {
			dialog.dismiss();
			Intent i = new Intent(getActivity(), SettingsActivity.class);
			startActivity(i);
		} else {
			input.setText("");
			Toast.makeText(getActivity(), R.string.wrong_pin_code,
					Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void showError() {

	}
}