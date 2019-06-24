package com.tunstall.grandstream;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.tunstall.com.R;
import com.tunstall.grandstream.Fragments.MVP.AlertDialogFragment;
import com.tunstall.grandstream.Home_Screen_Activity.MVP.Home_Activity;

public class MjpegStreamingActivity extends Activity {
    private static final String TAG = "MjpegActivity";
    
    private SoundPlayer mSoundPlayer;

    private ImageView videoFrame; // This is used to display the Bitmaps that are the individual frames of the streaming video.

    boolean mWasPaused = false;
    private FrameLayout mFrameLayout;

    private SocketService mService;

    private ArrayList<String> mVideoURLs;

    private MjpegStreamer mStreamer = null;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mSoundPlayer = new SoundPlayer(this);

        Intent intent = getIntent();
        if (intent != null) {
            mVideoURLs = intent.getStringArrayListExtra(SocketService.VIDEO_URLS);

            Log.d(TAG, "mVideoURLs " + mVideoURLs);
            
            String toast = "0-";
            for (int i = 0; i < mVideoURLs.size(); i++) {
            	toast += i + ":" + mVideoURLs.get(i) + " - ";
            }
            Toast.makeText(this, toast, Toast.LENGTH_LONG).show();
        }

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Intent serviceIntent = new Intent(this, SocketService.class);
        bindService(serviceIntent, mServiceConnection, BIND_AUTO_CREATE);
        registerReceiver(mSocketBroadcastReceiver,
                makeSocketUpdateIntentFilter());

        createUi();
    }
    
    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

        Home_Activity.SHOW_IP_FALIUR = false;

        if (mWasPaused) {
        	createUi();
        }
        mWasPaused = false;
    }
    
    private void createUi() {
    	mFrameLayout = new FrameLayout(this);
        LinearLayout linearLayout = new LinearLayout(this);

        Button doorOpen = new Button(this);
        // set button size according to screen size
        setButtonSize(doorOpen);
        //doorOpen.setText(getString(R.string.door_open));
        doorOpen.setBackgroundResource(R.drawable.dooropen);
        doorOpen.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mSoundPlayer.playButtonClick();
				if (mService != null) {
					mService.sendMessage(SocketService.DOOR_OPEN);
				}
			}
		});

        linearLayout.addView(doorOpen);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        params.gravity = Gravity.BOTTOM | Gravity.RIGHT;

        videoFrame = new ImageView(this);
        mFrameLayout.addView(videoFrame);

        mFrameLayout.addView(linearLayout, params);

        setContentView(mFrameLayout);

        mFrameLayout.setBackgroundColor(Color.BLACK);

        startStreaming();
    }

    /**
	 * AsyncTask instances can only be executed once, thus recreate the streamer
	 * and execute it. mStream is used as a local variable so it can be stopped
	 * in onPause.
	 */
    private void startStreaming() {
    	mStreamer = new MjpegStreamer();
//      streamer.execute(mVideoUri);
    	mStreamer.execute(mVideoURLs);
    }

    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName componentName,
                                       IBinder service) {
            mService = ((SocketService.LocalBinder) service).getService();
        }

        public void onServiceDisconnected(ComponentName componentName) { }
    };

    private static IntentFilter makeSocketUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SocketService.ACTION_SOCKET_DATA_MISSING);
        intentFilter.addAction(SocketService.ACTION_SOCKET_RESPONSE_ERROR);
        return intentFilter;
    }

    private final BroadcastReceiver mSocketBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (SocketService.ACTION_SOCKET_DATA_MISSING.equals(action)) {
            	showAlert(AlertDialogFragment.DIALOG_DATA_NOT_RECEIVED);

            } else if (SocketService.ACTION_SOCKET_RESPONSE_ERROR
                    .equals(action)) {
            	showAlert(AlertDialogFragment.DIALOG_RESPONSE_ERROR, intent.getStringExtra(SocketService.ERROR_CODE));

            }

        }
    };

    public void onPause() {
        super.onPause();

        // @@@ do something similar here for the MjpegStreamer task
//        mv.stopPlayback();

        mStreamer.keepDecodingFrames.set(false);

        mWasPaused = true;
        Home_Activity.SHOW_IP_FALIUR = true;

    }

    public class MjpegStreamer extends AsyncTask<ArrayList<String>, Bitmap, Long> {

        protected AtomicBoolean keepDecodingFrames = new AtomicBoolean(true);

        protected Long doInBackground(ArrayList<String>... urls) {

            Log.d(TAG, "urls[0] " + urls[0]);

            int errorId = 0;
            HttpResponse response = null;
            DefaultHttpClient httpclient = new DefaultHttpClient();

            ArrayList<String> theURLs = urls[0];

            Log.d(TAG, "theURLs " + theURLs);

            for (int i = 0; i < theURLs.size(); i++) {
                
            	boolean lastItem = (i == (theURLs.size() - 1));
            	String url = theURLs.get(i);

                try {
                    Log.d(TAG, "1. Sending http request:" + url);

                    HttpGet httpget = new HttpGet(url);

                    response = httpclient.execute(httpget);
                    Log.d(TAG, "2. Request finished, status = "
                            + response.getStatusLine().getStatusCode());
                    
                    if ((response.getStatusLine().getStatusCode() == 401) && lastItem) {
                        // error might be authentication problem
                    	errorId = AlertDialogFragment.DIALOG_ERROR_AUTH_VIDEO_SERVER;
                    }
                } catch (Exception e) {
                	if (lastItem) {
                		// Error connecting to camera
                		errorId = AlertDialogFragment.DIALOG_SOMETHING_WENT_WRONG;
                	}
                }
            }
            
            if (errorId == 0) {
            	try {
                    // We get the stream of bytes from the HTTP server and wrap it in a BufferedInputStream
                    // to give us a bit of flexibility when we need time to decode a complete JPEG.
                    HttpEntity entity = response.getEntity();
                    InputStream is = entity.getContent();
                    BufferedInputStream bufferedInputStream = new BufferedInputStream(is);

                    // This does the heavy lifting of decoding the JPEG data for each video frame.
                    BitmapFactory bitmapFactory = new BitmapFactory();

                    // Every byte in the stream passes through these two variables so we can detect the
                    // two-byte SOI and EOI markers (see below).
                    int prevByte = 0;
                    int thisByte = 0;

                    // The 320x240 images from the IP Video 9100A Plus encoder/server boxes we use are generally about 13.5kB each.
                    // If we move to support other encoder/server boxes then it might be an idea to make this buffer dynamically
                    // expand if a larger frame is detected.
                    byte[] jpegBuffer = new byte[32 * 1024];

                    int index = 0;

                    Boolean bufferJPEGData = false;

                    while (keepDecodingFrames.get()) {
                        prevByte = thisByte;

                        try {
                            thisByte = bufferedInputStream.read();
                        } catch (Exception e) {
                            Log.d(TAG, "bufferedInputStream.read() exception " + e.toString());
                        }

                        // The stream is just complete JPEG images interspersed with a few lines of junk text.
                        // The JPEG images are bracketed by Start Of Image (SOI, 0xff followed by 0xd8) and
                        // End Of Image (EOI, 0xff followed by 0xd9) markers.
                        // Since any actual values of 0xff are stuffed to 0xff, 0x00 pairs it's really easy to
                        // find the bounds of each JPEG. We can then use Android's BitmapFactory class to
                        // decode the JPEG image so that it can be displayed.
                        if (0xff == prevByte) {
                            if (0xd8 == thisByte) {

                                // Start Of Image (SOI) detected.

                                index = 0;

                                jpegBuffer[index++] = (byte) prevByte;

                                bufferJPEGData = true;

                            } else if (0xd9 == thisByte) {

                                // End Of Image (EOI) detected.

                                jpegBuffer[index++] = (byte) thisByte; // Don't miss off the second half of the EOI marker!

                                Bitmap newVideoFrame = bitmapFactory.decodeByteArray(jpegBuffer, 0, index);

                                publishProgress(newVideoFrame); // This will call the onProgressUpdate() method below from the context of the UI thread.

                                bufferJPEGData = false;
                            }
                        }

                        if (bufferJPEGData) {
                            jpegBuffer[index++] = (byte) thisByte;
                        }
                    }

            	} catch (Exception e) {
                    // Error connecting to camera
                	errorId = AlertDialogFragment.DIALOG_SOMETHING_WENT_WRONG;
                }
            
            } 

            if (errorId > 0) {
            	showAlert(errorId);
            	return Long.valueOf(-1);
            	
            } else {
            	Log.d(TAG, "MjpegStreamer exited normally.");
                return Long.valueOf(0);
            }
        }

        protected void onProgressUpdate(Bitmap... newVideoFrame) {
            // NB: This method runs in the context of the UI thread; any changes to the UI must only be done from the UI thread!

            videoFrame.setImageBitmap(newVideoFrame[0]);
        }

    }
    
    private void showAlert(int dialogId) {
		DialogFragment frag = AlertDialogFragment.newInstance(dialogId);
		frag.show(getFragmentManager(), "alert");
	}
    
    private void showAlert(int dialogId, String message) {
		DialogFragment frag = AlertDialogFragment
				.newInstance(dialogId, message);
		frag.show(getFragmentManager(), "alert");
	}

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);

        if (mSocketBroadcastReceiver != null) {
            unregisterReceiver(mSocketBroadcastReceiver);
        }

    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void setButtonSize(Button doorOpen) {
        if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE) {
            doorOpen.setWidth(300);
            doorOpen.setHeight(100);
            doorOpen.setTextSize(30);

        } else if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_NORMAL) {

            doorOpen.setWidth(120);
            doorOpen.setHeight(50);

        } else if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_SMALL) {

            doorOpen.setWidth(120);
            doorOpen.setHeight(50);
        } else if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_XLARGE) {

            doorOpen.setWidth(300);
            doorOpen.setHeight(100);
            doorOpen.setTextSize(30);
        } else {

            doorOpen.setWidth(120);
            doorOpen.setHeight(50);
        }
    }
}
