package com.tunstall.grandstream.Home_Screen_Activity.MVP;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;

import com.tunstall.com.R;
import com.tunstall.grandstream.AppSettings;
import com.tunstall.grandstream.Fragments.MVP.AlertDialogFragment;
import com.tunstall.grandstream.MyApplication;
import com.tunstall.grandstream.SocketService;
import com.tunstall.grandstream.SoundPlayer;
import com.tunstall.grandstream.WifiActivity;


// SocketService Constants
import static com.tunstall.grandstream.Storage.constants.AppSettingConstants.PREF_WIFI_VISIBILITY;
import static com.tunstall.grandstream.Storage.constants.SocketServiceConstants.ACTION_SOCKET_BUTTON_STATE_CHANGED;
import static com.tunstall.grandstream.Storage.constants.SocketServiceConstants.ACTION_SOCKET_DATA_MISSING;
import static com.tunstall.grandstream.Storage.constants.SocketServiceConstants.ACTION_SOCKET_IP_ISSUE;
import static com.tunstall.grandstream.Storage.constants.SocketServiceConstants.ACTION_SOCKET_RESPONSE_ERROR;
import static com.tunstall.grandstream.Storage.constants.SocketServiceConstants.ALARM;
import static com.tunstall.grandstream.Storage.constants.SocketServiceConstants.BUTTON_KEY;
import static com.tunstall.grandstream.Storage.constants.SocketServiceConstants.DOOR_OPEN;
import static com.tunstall.grandstream.Storage.constants.SocketServiceConstants.DOOR_PRIVACY;
import static com.tunstall.grandstream.Storage.constants.SocketServiceConstants.DOOR_VIDEO;
import static com.tunstall.grandstream.Storage.constants.SocketServiceConstants.ERROR_CODE;
import static com.tunstall.grandstream.Storage.constants.SocketServiceConstants.GET_STATES;
import static com.tunstall.grandstream.Storage.constants.SocketServiceConstants.HOME_AWAY;
import static com.tunstall.grandstream.Storage.constants.SocketServiceConstants.IM_OK;
import static com.tunstall.grandstream.Storage.constants.SocketServiceConstants.LED_DURATION_KEY;
import static com.tunstall.grandstream.Storage.constants.SocketServiceConstants.LED_FLASH;
import static com.tunstall.grandstream.Storage.constants.SocketServiceConstants.LED_OFF;
import static com.tunstall.grandstream.Storage.constants.SocketServiceConstants.LED_ON;
import static com.tunstall.grandstream.Storage.constants.SocketServiceConstants.LED_STATE_KEY;


//AppSettings Constants



public class Home_Activity extends Activity implements OnClickListener,
		OnSharedPreferenceChangeListener,Home_Activity_Contract.View {


	//Presenter
	Home_Activity_Presenter myHomePresenter;
	
	private static final int MENU_SETTINGS_ID = Menu.FIRST;
	private static final int MENU_WIFI_ID = Menu.FIRST + 1;
	
	private Button mDoorOpen;
	private Button mHomeAway;
	private Button mDoorPrivacy;
	private Button mImOK;
	private Button mDoorVideo;
	private Button mAlarmButton;
	
	private SoundPlayer mSoundPlayer;

	// this is used to control if this activity should show error dialog or not.
	public static boolean SHOW_IP_FALIUR = true;

	private Handler handler = new Handler();

	private SocketService mService;

	private boolean mUpdatePhoneUi = false;
	
	private AppSettings mAppSettings;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);



		//TODO In future using Dagger injection to inject presenter here and fill dependencies, but for now will create presenter
		//the normal way


		//initialise presenter
		myHomePresenter = new Home_Activity_Presenter(this);

		// App settings is the settings instance which is saving the application configuration.

		mAppSettings = ((MyApplication) getApplicationContext()).getAppSettings();
		mAppSettings.registerListener(this);
		mSoundPlayer = new SoundPlayer(this);
		mDoorOpen = (Button) findViewById(R.id.doorOpen);
		mDoorOpen.setTag(DOOR_OPEN.toLowerCase());

		mHomeAway = (Button) findViewById(R.id.homeAway);
		mHomeAway.setTag(HOME_AWAY.toLowerCase());

		mDoorPrivacy = (Button) findViewById(R.id.doorPrivacy);
		mDoorPrivacy.setTag(DOOR_PRIVACY.toLowerCase());

		mImOK = (Button) findViewById(R.id.imOk);
		mImOK.setTag(IM_OK.toLowerCase());

		mAlarmButton = (Button) findViewById(R.id.alarm);
		mAlarmButton.setTag(ALARM.toLowerCase());

		mDoorVideo = (Button) findViewById(R.id.doorVideo);
		mDoorVideo.setTag(DOOR_VIDEO.toLowerCase());
		
		mDoorVideo.setOnClickListener(this);
		mAlarmButton.setOnClickListener(this);
		mDoorOpen.setOnClickListener(this);
		mHomeAway.setOnClickListener(this);
		mDoorPrivacy.setOnClickListener(this);
		mImOK.setOnClickListener(this);

		Intent serviceIntent = new Intent(this, SocketService.class);
		bindService(serviceIntent, mServiceConnection, BIND_AUTO_CREATE);

		// // TODO - This is a bodge (Plaster) to bring up the video streaming activity
		// immediately.
		// Intent videoIntent = new Intent(Home_Activity.this,
		// MjpegStr streamingActivity.class);
		// videoIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// videoIntent.putExtra("videoLocation",
		// "http://192.168.0.41/GetData.cgi");
		// startActivity(videoIntent);
	}

	@Override
	protected void onPause() {
		super.onPause();

		// Update the phone ui when resumed so the ui reflects the server state
		// (in case changes has been made on the server)



		//TODO registering broadcast receiver and unregistering via the onPause and onResume overriden methods
		mUpdatePhoneUi = true;
		unregisterReceiver(mSocketBroadcastReceiver);
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		registerReceiver(mSocketBroadcastReceiver,
				makeSocketUpdateIntentFilter());

		String ipAddress = mAppSettings.getStringForKey(PREF_IP_ADDRESS);
		String port = mAppSettings.getStringForKey(PREF_PORT);
		String residentId = mAppSettings.getStringForKey(PREF_RESIDENT_ID);
		
		setButtonsVisibility();

		boolean missingParameter = false;
		if (TextUtils.isEmpty(residentId)) {
			missingParameter = true;
		} else if (TextUtils.isEmpty(ipAddress)) {
			missingParameter = true;
		} else if (TextUtils.isEmpty(port)) {
			missingParameter = true;
		}

		if (missingParameter) {
			showAlert(AlertDialogFragment.DIALOG_CONFIGURE_SETTINGS);
		} else if (mUpdatePhoneUi) {
			mService.sendMessage(GET_STATES);
			mUpdatePhoneUi = false;
		}

	}
	
	private void setButtonsVisibility() {
		mDoorOpen.setVisibility(mAppSettings.getButtonVisibility(PREF_DOOR_OPEN));
		mDoorVideo.setVisibility(mAppSettings.getButtonVisibility(PREF_DOOR_VIDEO));
		mDoorPrivacy.setVisibility(mAppSettings.getButtonVisibility(PREF_DOOR_PRIVACY));
		mHomeAway.setVisibility(mAppSettings.getButtonVisibility(PREF_HOME_AWAY));
		mImOK.setVisibility(mAppSettings.getButtonVisibility(PREF_IM_OK));
		mAlarmButton.setVisibility(mAppSettings.getButtonVisibility(PREF_ALARM));
	}

	private static IntentFilter makeSocketUpdateIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();

		intentFilter.addAction(ACTION_SOCKET_DATA_MISSINGTA_MISSING);
		intentFilter
				.addAction(ACTION_SOCKET_BUTTON_STATE_CHANGED);
		intentFilter.addAction(ACTION_SOCKET_IP_ISSUE);
		intentFilter.addAction(ACTION_SOCKET_RESPONSE_ERROR);

		return intentFilter;
	}

	private final BroadcastReceiver mSocketBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			if (ACTION_SOCKET_DATA_MISSING.equals(action)) {
				if (SHOW_IP_FALIUR) {
					showAlert(AlertDialogFragment.DIALOG_DATA_NOT_RECEIVED);
				}

			} else if (ACTION_SOCKET_BUTTON_STATE_CHANGED
					.equals(action)) {

				String button = intent.getStringExtra(BUTTON_KEY);
				String ledState = intent
						.getStringExtra(LED_STATE_KEY);
				long duration = Integer.parseInt(intent
						.getStringExtra(LED_DURATION_KEY));

				if (ledState.equals(LED_FLASH)) {
					onButtonFlash(button);
				} else if (ledState.equals(LED_ON)) {
					if (duration > 0) {
						onButtonTimed(button, duration);
					} else {
						onButtonOn(button);
					}
				} else if (ledState.equals(LED_OFF)) {
					onButtonOff(button);
				}

			} else if (ACTION_SOCKET_IP_ISSUE.equals(action)) {

				showAlert(AlertDialogFragment.DIALOG_IP_ISSUE);

			} else if (ACTION_SOCKET_RESPONSE_ERROR
					.equals(action)) {
				String errorMessage = intent
						.getStringExtra(ERROR_CODE);

				if (SHOW_IP_FALIUR) {
					showAlert(AlertDialogFragment.DIALOG_RESPONSE_ERROR,
							errorMessage);
				}

			}

		}
	};

	private final ServiceConnection mServiceConnection = new ServiceConnection() {

		public void onServiceConnected(ComponentName componentName,
				IBinder service) {
			mService = ((SocketService.LocalBinder) service).getService();
    		mService.sendMessage(GET_STATES);
		}

		public void onServiceDisconnected(ComponentName componentName) {
		}
	};
	
	/**
     * Gets called every time the user presses the menu button.
     * Use if your menu is dynamic.
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        
        if (mAppSettings.getButtonVisibility(PREF_WIFI_VISIBILITY) == View.VISIBLE) {
        	menu.add(0, MENU_WIFI_ID, Menu.NONE, R.string.wifi);
        }
        
        menu.add(0, MENU_SETTINGS_ID, Menu.NONE, R.string.action_settings);
        return super.onPrepareOptionsMenu(menu);
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case MENU_SETTINGS_ID:

			myHomePresenter.createPinCodeFragment()

			showAlert(AlertDialogFragment.DIALOG_SETTINGS_PIN_CODE, mAppSettings);
			break;
			
		case MENU_WIFI_ID:
			Intent i = new Intent(Home_Activity.this, WifiActivity.class);
			startActivity(i);
			break;
		}

		return true;
	}




	// these methods oerride the view to show alert based on which constructor is overloaded
	//The presenter method is called which will take the depedents feed them into the view methods which using a callback will update
	//the view accordingly

	@Override
	public void showAlert(int dialogId) {
		DialogFragment frag = AlertDialogFragment.newInstance(dialogId);
		frag.show(getFragmentManager(), "alert");
	}

	@Override
	public void showAlert(int dialogId, AppSettings appSettings) {
		DialogFragment frag = AlertDialogFragment.newInstanceSettings(dialogId, appSettings);
		frag.show(getFragmentManager(), "alert");
	}

    @Override
	public void showAlert(int dialogId, String message) {
		DialogFragment frag = AlertDialogFragment
				.newInstance(dialogId, message);
		frag.show(getFragmentManager(), "alert");
	}

	@Override
	public void onClick(View v) {
		mSoundPlayer.playButtonClick();
		String message = "";

		switch (v.getId()) {
		case R.id.doorOpen:
			message = SocketService.DOOR_OPEN;
			break;

		case R.id.doorVideo:
			message = SocketService.DOOR_VIDEO;
			break;
			
		case R.id.homeAway:
			message = SocketService.HOME_AWAY;
			break;

		case R.id.doorPrivacy:
			message = SocketService.DOOR_PRIVACY;
			break;
			
		case R.id.imOk:
			message = SocketService.IM_OK;
			break;
			
		case R.id.alarm:
			message = SocketService.ALARM;
			break;
			
		default:
			break;
		}

		if (mService != null) {
			try {
				if (!TextUtils.isEmpty(message)) {
					mService.sendMessage(message);
				}

			} catch (Exception e) {

			}
		}

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unbindService(mServiceConnection);
	}

	private void onButtonFlash(final String button) {

		handler.post(new Runnable() {
			public void run() {

				String buttonName = button.toLowerCase();

				// Change alpha from fully visible to invisible
				final Animation animation = new AlphaAnimation(1, 0);
				animation.setDuration(1000); // duration -a second
				animation.setInterpolator(new LinearInterpolator());
				// Repeat animation infinitely
				animation.setRepeatCount(Animation.INFINITE);

				// Reverse animation at the end so the button will fade back in
				animation.setRepeatMode(Animation.REVERSE);

				if (buttonName.equals(mDoorOpen.getTag())) {
					mDoorOpen.startAnimation(animation);
				} else if (buttonName.equals(mHomeAway.getTag())) {
					mHomeAway.startAnimation(animation);
				} else if (buttonName.equals(mDoorPrivacy.getTag())) {
					mDoorPrivacy.startAnimation(animation);
				} else if (buttonName.equals(mImOK.getTag())) {
					mImOK.startAnimation(animation);
				} else if (buttonName.equals(mDoorVideo.getTag())) {
					mDoorVideo.startAnimation(animation);
				} else if (buttonName.equals(mAlarmButton.getTag())) {
					mAlarmButton.startAnimation(animation);
				}

			}
		});

	}

	private void onButtonTimed(final String button, final long duration) {

		handler.post(new Runnable() {
			public void run() {

				String buttonName = button.toLowerCase();

				if (buttonName.equals((mDoorOpen.getTag()))) {

					mDoorOpen.clearAnimation();
					mDoorOpen.setBackgroundResource(R.drawable.dooropenpic);
					Handler handler = new Handler();
					handler.postDelayed(new Runnable() {
						public void run() {
							mDoorOpen
									.setBackgroundResource(R.drawable.dooropenpic);
						}
					}, duration);

				} else if (buttonName.equals(mHomeAway.getTag())) {
					mHomeAway.clearAnimation();
					mHomeAway.setBackgroundResource(R.drawable.away);
					Handler handler = new Handler();
					handler.postDelayed(new Runnable() {
						public void run() {
							mHomeAway.setBackgroundResource(R.drawable.home);
						}
					}, duration);

				} else if (buttonName.equals(mDoorPrivacy.getTag())) {
					mDoorPrivacy.clearAnimation();
					mDoorPrivacy
							.setBackgroundResource(R.drawable.doorprivactive);
					Handler handler = new Handler();
					handler.postDelayed(new Runnable() {
						public void run() {
							mDoorPrivacy
									.setBackgroundResource(R.drawable.doorpriv);
						}
					}, duration);

				} else if (buttonName.equals(mImOK.getTag())) {
					mImOK.clearAnimation();
					mImOK.setBackgroundResource(R.drawable.imokpic);
					Handler handler = new Handler();
					handler.postDelayed(new Runnable() {
						public void run() {
							mImOK.setBackgroundResource(R.drawable.imokpic);
						}
					}, duration);

				} else if (buttonName.equals(mDoorVideo.getTag())) {
					mDoorVideo.clearAnimation();
					mDoorVideo.setBackgroundResource(R.drawable.doorvideopic);
					Handler handler = new Handler();
					handler.postDelayed(new Runnable() {
						public void run() {
							mDoorVideo
									.setBackgroundResource(R.drawable.doorvideopic);
						}
					}, duration);

				} else if (buttonName.equals(mAlarmButton.getTag())) {
					mAlarmButton.clearAnimation();
					mAlarmButton
							.setBackgroundResource(R.drawable.alarmpicactive);
					Handler handler = new Handler();
					handler.postDelayed(new Runnable() {
						public void run() {
							mAlarmButton
									.setBackgroundResource(R.drawable.alarmpic);
						}
					}, duration);
				}

			}
		});

	}

	private void onButtonOff(final String button) {

		handler.post(new Runnable() {
			public void run() {
				String buttonName = button.toLowerCase();
				// clear animation in case there was an animation, before
				// applying image

				if (buttonName.equals(mDoorOpen.getTag())) {

					mDoorOpen.clearAnimation();
					mDoorOpen.setBackgroundResource(R.drawable.dooropen);

				} else if (buttonName.equals(mHomeAway.getTag())) {
					mHomeAway.clearAnimation();
					mHomeAway.setBackgroundResource(R.drawable.homeaway_off);

				} else if (buttonName.equals(mDoorPrivacy.getTag())) {
					mDoorPrivacy.clearAnimation();
					mDoorPrivacy.setBackgroundResource(R.drawable.doorprivacy);

				} else if (buttonName.equals(mImOK.getTag())) {
					mImOK.clearAnimation();
					mImOK.setBackgroundResource(R.drawable.imok);

				} else if (buttonName.equals(mDoorVideo.getTag())) {
					mDoorVideo.clearAnimation();
					mDoorVideo.setBackgroundResource(R.drawable.doorvideo);

				} else if (buttonName.equals(mAlarmButton.getTag())) {
					mAlarmButton.clearAnimation();
					mAlarmButton.setBackgroundResource(R.drawable.alarm);
				}

			}
		});

	}

	private void onButtonOn(final String button) {

		handler.post(new Runnable() {
			public void run() {

				String buttonName = button.toLowerCase();

				if (buttonName.equals((mDoorOpen.getTag()))) {

					mDoorOpen.clearAnimation();
					mDoorOpen.setBackgroundResource(R.drawable.dooropen);

				} else if (buttonName.equals(mHomeAway.getTag())) {
					mHomeAway.clearAnimation();
					mHomeAway.setBackgroundResource(R.drawable.homeaway_on);

				} else if (buttonName.equals(mDoorPrivacy.getTag())) {
					mDoorPrivacy.clearAnimation();
					mDoorPrivacy.setBackgroundResource(R.drawable.doorprivacy);

				} else if (buttonName.equals(mImOK.getTag())) {
					mImOK.clearAnimation();
					mImOK.setBackgroundResource(R.drawable.imok);

				} else if (buttonName.equals(mDoorVideo.getTag())) {
					mDoorVideo.clearAnimation();
					mDoorVideo.setBackgroundResource(R.drawable.doorvideo);

				} else if (buttonName.equals(mAlarmButton.getTag())) {
					mAlarmButton.clearAnimation();
					mAlarmButton.setBackgroundResource(R.drawable.alarm);
				}

			}
		});

	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences arg0, String arg1) {


    	//This needs to happen through the presenter

		mUpdatePhoneUi = true;
		setButtonsVisibility();
	}


	//Since there is a fragment class in order to show errors anyway there is no need for this callback to show an error
	//actually this is a good way to launch the creation of the fragment and pass in the error code message
	@Override
	public void showError() {

	}
}
