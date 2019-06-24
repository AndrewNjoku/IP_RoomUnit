package com.tunstall.grandstream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.base.module.account.Account;

import com.tunstall.com.R;
import com.tunstall.grandstream.Storage.constants;
import com.tunstall.utility.SimpleXmlParser;
import com.tunstall.utility.SimpleXmlParser.AttributeTag;
import com.tunstall.utility.SimpleXmlParser.Entry;


import static com.tunstall.grandstream.Storage.constants.AppSettingConstants.PREF_IP_ADDRESS;
import static com.tunstall.grandstream.Storage.constants.AppSettingConstants.PREF_PORT;
import static com.tunstall.grandstream.Storage.constants.AppSettingConstants.PREF_RESIDENT_ID;
import static com.tunstall.grandstream.Storage.constants.SocketServiceConstants.*;
import static com.tunstall.grandstream.config.ConfigFile.LOG_TAG;



public class SocketService extends Service implements
		OnSharedPreferenceChangeListener {

    public static final String VIDEO_URLS = "videoURLs";


    //private constants have been kept together with the class for simplicity purpouses, no need to use getters

	public static final int MSG_GOT_RESPONSE = 100;
	private static final int MSG_RESPONSE_NOT_RECEIVED = 200;
	private static final int MSG_RESPONSE_BUTTON_CHANGED = 300;
	private static final int MSG_IP_ISSUE = 400;
	private static final int MSG_RESPONSE_ERROR = 500;
	private static final int MSG_START_VIDEO_STREAM = 600;

	private int mTimeOut = 5000;

	private String mIpAddress = "";
	private int mPort = 0;
	private String mResident;
	private String mAppVersion = "";
    private String mCallbackNumber = "0";
    private String mDeviceType = "";

	private boolean mStop = true;
	SocketSender sender = null;

	private static long nextUniqueMsgId = 0;
    private static long lastGetStatusMsgId = -1;
    private static boolean getStatusResponded = false;


	
	private AppSettings mAppSettings;
	
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_GOT_RESPONSE:
				break;
				case MSG_RESPONSE_NOT_RECEIVED:
				Intent intentNoResponse = new Intent(ACTION_SOCKET_DATA_MISSING);
				sendBroadcast(intentNoResponse);
				break;
			case MSG_RESPONSE_BUTTON_CHANGED:
				Bundle b = msg.getData();
				String button = b.getString(BUTTON_KEY);
				String ledState = b.getString(LED_STATE_KEY);
                String ledDuration = b.getString(LED_DURATION_KEY);
                String butEnabled = b.getString(BUTTON_ENABLED_KEY);
                String butVisible = b.getString(BUTTON_VISIBLE_KEY);

				Intent intentButtonChanged = new Intent(
						ACTION_SOCKET_BUTTON_STATE_CHANGED);
				intentButtonChanged.putExtra(BUTTON_KEY, button);
				intentButtonChanged.putExtra(LED_STATE_KEY, ledState);
                intentButtonChanged.putExtra(LED_DURATION_KEY, ledDuration);
                intentButtonChanged.putExtra(BUTTON_ENABLED_KEY, butEnabled);
                intentButtonChanged.putExtra(BUTTON_VISIBLE_KEY, butVisible);
				sendBroadcast(intentButtonChanged);
				break;
			case MSG_IP_ISSUE:
				Intent ipIssue = new Intent(ACTION_SOCKET_IP_ISSUE);
				sendBroadcast(ipIssue);
				break;
			case MSG_RESPONSE_ERROR:
				Bundle bundle = msg.getData();
				String errorCode = bundle.getString(ERROR_CODE);
				String errorMessage = "";

				if (errorCode.toLowerCase().equals(
						ERROR_GENERAL_ERROR.toLowerCase())) {
					errorMessage = getString(R.string.error_general_error);

				} else if (errorCode.toLowerCase().equals(
						ERORR_UNKNOWN_RESIDENT.toLowerCase())) {
					errorMessage = getString(R.string.error_unknown_resident);

				} else if (errorCode.toLowerCase().equals(
						ERORR_NO_ACTIVE_DOOR_CALL.toLowerCase())) {

					errorMessage = getString(R.string.error_no_active_door_call);

				}

				if (!TextUtils.isEmpty(errorMessage)) {
					Intent responseError = new Intent(
							ACTION_SOCKET_RESPONSE_ERROR);
					responseError.putExtra(ERROR_CODE, errorMessage);
					sendBroadcast(responseError);
				}

				break;
			case MSG_START_VIDEO_STREAM:
				Bundle videoBundle = msg.getData();
                ArrayList<String> videoURLs = videoBundle.getStringArrayList(VIDEO_URLS);
				Intent videoIntent = new Intent(SocketService.this,
						MjpegStreamingActivity.class);
				videoIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                videoIntent.putExtra(VIDEO_URLS, videoURLs);
				startActivity(videoIntent);

				break;

			default:
				super.handleMessage(msg);

			}

		}
	};

	private final IBinder mBinder = new LocalBinder();

	@Override
	public IBinder onBind(Intent arg0) {
		return mBinder;
	}

	public class LocalBinder extends Binder {
		SocketService getService() {
			return SocketService.this;
		}
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		mAppSettings = ((MyApplication) getApplicationContext()).getAppSettings();
		mAppSettings.registerListener(this);
		initIPAddress();
		mAppVersion = getVersionName(this);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	};

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);

		return START_STICKY;
	}

	public void initIPAddress() {

		//I am not importing the whole class just incase there are samely named constances for different scopes

		mIpAddress = mAppSettings.getStringForKey(PREF_IP_ADDRESS);

		String port = mAppSettings.getStringForKey(PREF_PORT);
		if (port != "") {
			mPort = Integer.parseInt(port);
		}
		mResident = mAppSettings.getStringForKey(PREF_RESIDENT_ID);

        mDeviceType = Build.MANUFACTURER+Build.BOARD;
	}

	public void sendMessage(final String message)
    {


    	//Heres one!!

    	Log.d(LOG_TAG, message);

        if (message.equals(GET_STATES)) //Is the current message a GetStates request
        {
            getStatusResponded=false;
            lastGetStatusMsgId = nextUniqueMsgId; //Set the counter to the last message ID, this is then used in the RX side to check for a reply.
        }
        else if(!getStatusResponded) //If this is not a GetStatus request and the SC has not replied to a GetStates, resend the GetStates message.
        {
            sendMessage(GET_STATES);
        }

		final OutgoingMessage sendMessage = composeMessage(message);

//TODO RLO check this works when attached to an authenticated PBX.

        Account[] accounts = com.base.module.account.AccountManager.instance().getRegAccounts(this);
        //Account[] accounts = com.base.module.account.AccountManager.instance().getAccounts(this);
        for (int i = 0; i < accounts.length; i++)
        {
            Log.w("Tunstall", accounts[i].getSipAuthID());
        }
        if (accounts.length > 0)
        {
            mCallbackNumber = accounts[0].getSipAuthID();
        }
        else
        {
            mCallbackNumber = "0"; //Set local SIP address reading to zero for pass back to SC
        }

		Log.d(LOG_TAG), "mStop : " + mStop);  //Fix later

    	if (mStop) {
			sender = new SocketSender();

			// add message first
			sender.addMessage(sendMessage);

			// then connect
			sender.connect();
			Log.d(LOG_TAG_Socket, "Created new sender and sent message : " + message);
		
		} else {
			sender.addMessage(sendMessage);
			Log.d(LOG_TAG, "sent message : " + message);
		}
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences arg0, String arg1) {
		System.out.println("onSharedPreferenceChanged");
		initIPAddress();

	}

	private OutgoingMessage composeMessage(String message) {

		OutgoingMessage msg = new OutgoingMessage(message, nextUniqueMsgId,
				mResident, mAppVersion, mCallbackNumber, mDeviceType);

		// add upp messageId
		nextUniqueMsgId++;

		return msg;

	}

	private static String getVersionName(Context context) {
		String versionName;
		try {
			versionName = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			versionName = "Unknown version.";
		}
		return versionName;
	}

	class SocketSender implements Runnable {

		private Vector<OutgoingMessage> outQueue = new Vector<OutgoingMessage>();

		DatagramSocket socket;
		DatagramPacket packet;
		Thread executerThread = null;

		boolean dataStillSent = false;
		TimerTask responseTimerTask;
		Timer timer = new Timer();
		SocketListener listener;

		int reTryCount = 0;

		public synchronized void connect() {

			mStop = false;
			executerThread = new Thread(this);
			executerThread.setPriority(Thread.NORM_PRIORITY);
			executerThread.start();

		}

		public void addMessage(OutgoingMessage message) {
			outQueue.add(message);
		}

		@Override
		public void run() {

			try {
				socket = new DatagramSocket();
				listener = new SocketListener();
				listener.start();
				
				
				while (!mStop) {

					if (!dataStillSent) {

						if (outQueue != null && outQueue.size() > 0)
                        {
							byte[] data = outQueue.firstElement().getData().getBytes();

							initIPAddress();
							InetAddress address = InetAddress.getByName(mIpAddress);
							packet = new DatagramPacket(data, data.length, address, mPort);

							// clear count
							reTryCount = 0;

							Log.d(LOG_TAG, "Sent package to IP: " + mIpAddress + ". Queue size: " + outQueue.size());
							
							socket.send(packet);
							scheduleResponseTimer(mTimeOut);
							dataStillSent = true;
                            String decoded = new String(data, "UTF-8");
                            Log.i(LOG_TAG, "Sent message( " + decoded + " ) to " + mIpAddress);
						} else
                        {
							stop();
						}
					}
				}

			} catch (UnknownHostException e) {

				stop();
				// send message back to user
				Message msg = mHandler.obtainMessage(MSG_IP_ISSUE);
				mHandler.sendMessage(msg);

			} catch (SocketException e) {
				stop();
				// send message back to user
				Log.d(LOG_TAG, "SocketException");
				Message msg = mHandler.obtainMessage(MSG_RESPONSE_NOT_RECEIVED);
				mHandler.sendMessage(msg);

			} catch (IOException e) {
				stop();
				Log.d(LOG_TAG, "IOException");
				// send message back to user
				Message msg = mHandler.obtainMessage(MSG_RESPONSE_NOT_RECEIVED);
				mHandler.sendMessage(msg);

			}
            catch (NoSuchElementException e)
            {
                Log.i(LOG_TAG, "Element missing!");
            //    reTryCount = 0;
            //    dataStillSent = false;
                stop();
            }

		}

		private void resend() throws IOException {

			socket.send(packet);
			reTryCount++;
			scheduleResponseTimer(mTimeOut);

		}

		private synchronized void cancelResponseTimer() {
			if (responseTimerTask != null) {
				try {
					responseTimerTask.cancel();
				} catch (Exception ex) {

				}
				responseTimerTask = null;

			}
		}

		private synchronized void scheduleResponseTimer(int time) {
			cancelResponseTimer();

			responseTimerTask = new TimerTask() {
				public final void run() {
					Log.d(LOG_TAG, "Response timer is running. reTryCount: " + reTryCount);
					try {
						// re-send once else send notification to user
						if (reTryCount == 0) {
							resend();
						} else {

							// remove first element as we did not get any
							// response
							outQueue.remove(0);
							dataStillSent = false;

							// report back to user that message is not
							// received.
							Log.d(LOG_TAG, "report back to user that message is not received.");
	
							Message msg = mHandler
									.obtainMessage(MSG_RESPONSE_NOT_RECEIVED);
							mHandler.sendMessage(msg);

							if (outQueue != null && outQueue.size() == 0) {
								stop();
							}

						}
					} catch (Exception e) {

						Log.e(LOG_TAG, "TimerTask exception " + e.getMessage());
					}
				}
			};

			timer.schedule(responseTimerTask, time);

		}

		private void stop() {
			mStop = true;

			if (socket != null) {
				socket.close();
				socket = null;
			}

			if (executerThread != null) {

				executerThread.interrupt();
				executerThread = null;
			}

			if (sender != null) {
				sender = null;
			}

		}

		private class SocketListener extends Thread {

			public SocketListener() {
				this.setPriority(Thread.MAX_PRIORITY);

			}

			public synchronized void start() {
				super.start();
			}

			@Override
			public void run() {

				try {

					Log.i(LOG_TAG, "Listener started!");
					// DatagramSocket socket = new DatagramSocket(21);
					// socketTest.setSoTimeout(10000);
					byte[] buffer = new byte[2048];
					DatagramPacket packet = new DatagramPacket(buffer, 2048);

					while (!mStop) {

						try {

							Log.i(LOG_TAG, "Listening for packets");

							// blocks until a packet is received
							socket.receive(packet);

							String data = new String(buffer, 0,
									packet.getLength());
							Log.i(LOG_TAG,
									"Packet received from "
											+ packet.getAddress()
											+ " with contents: " + data);

							SimpleXmlParser parser = new SimpleXmlParser();
							Entry entry = null;

							InputStream is = new ByteArrayInputStream(
									data.getBytes());

							try {

								// parse xml
								entry = parser.parse(is);

								if (entry != null) {
									
									Log.d(LOG_TAG, "Received messageId: " + entry.messageId + 
											". First message id in queue: " + outQueue
											.firstElement().getMessageId() + ". Queue size: " + outQueue.size());

									// check if same as
									if (outQueue != null && outQueue.size() > 0) {

								

										if (entry.messageId == outQueue
												.firstElement().getMessageId()) {
											// check that received messageId is
											// what we expect otherwise ignore
											// message

                                            if (lastGetStatusMsgId == entry.messageId)
                                            {
                                                getStatusResponded=true;
                                            }
											dataStillSent = false;
											outQueue.remove(0);

											cancelResponseTimer();
											
											Log.d(LOG_TAG, "Message id " + entry.messageId + " removed from queue and response timer cancelled. Queue size: " + outQueue.size());

								

											if (entry.ack.equals("true")) {
												if (entry.buttonStateList != null) {
													// parse Button list
													for (AttributeTag value : entry.buttonStateList) {
														// attribute corresponds
														// to
														// which button
														// tag is the led
														// action, what
														// is supposed do with
														// led
														String Button = value.attribute;
														String ledState = value.tag;
                                                        String ledDuration = value.duration;
                                                        String butEnabled = value.enabled;
                                                        String butVisible = value.visible;
														Message msg1 = mHandler
																.obtainMessage(MSG_RESPONSE_BUTTON_CHANGED);
														Bundle bundle = new Bundle();
														bundle.putString(
																BUTTON_KEY,
																Button);
														bundle.putString(
																LED_STATE_KEY,
																ledState);
                                                        bundle.putString(
                                                                LED_DURATION_KEY,
                                                                ledDuration);
                                                        bundle.putString(
                                                                BUTTON_ENABLED_KEY,
                                                                butEnabled);
                                                        bundle.putString(
                                                                BUTTON_VISIBLE_KEY,
                                                                butVisible);
														msg1.setData(bundle);
														mHandler.sendMessage(msg1);

													}

												}
                                                if (entry.doorVideoURLs.size() > 0) {

                                                    // we got video location
                                                    // start stream
                                                    Message msg1 = mHandler
                                                            .obtainMessage(MSG_START_VIDEO_STREAM);
                                                    Bundle bundle = new Bundle();

                                                    bundle.putStringArrayList(
                                                            VIDEO_URLS,
                                                            entry.doorVideoURLs);
                                                    Log.d("SocketService", "entry.doorVideoURLs " + entry.doorVideoURLs);

                                                    msg1.setData(bundle);
                                                    mHandler.sendMessage(msg1);

                                                }

											} else if (entry.ack
													.equals("false")) {
												Bundle bundle = new Bundle();
												bundle.putString(ERROR_CODE,
														entry.errorMessage);
												Message msg1 = mHandler
														.obtainMessage(MSG_RESPONSE_ERROR);
												msg1.setData(bundle);
												mHandler.sendMessage(msg1);

											}
										} else {
											Log.d(LOG_TAG, "Not same messageId ignore response");
										}
									}
								}

							} catch (Exception e) {
								Toast.makeText(getApplicationContext(), "Exception reading from socket", Toast.LENGTH_LONG).show();
							}

						} catch (IOException e) {

						}

					}
					Log.i(LOG_TAG, "Listener ending");

					return;
				} catch (Exception e) {

				}

			}

		}

	}

}
