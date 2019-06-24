package com.tunstall.grandstream;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;

import com.tunstall.com.R;

public class SoundPlayer {
	
	private SoundPool mSpool;
	private int mButtonClickId;
	private AudioManager mAudioManager;

	public SoundPlayer(Activity a) {
		mSpool = new SoundPool(40, AudioManager.STREAM_ALARM, 0);
		mButtonClickId = mSpool.load(a, R.raw.button, 1);
		mAudioManager = (AudioManager) a.getSystemService(Context.AUDIO_SERVICE);
	}
	
	public void playButtonClick() {
		play(mButtonClickId);
	}
	
	private void play(int soundId) {
		final float volume = (float) mAudioManager
				.getStreamVolume(AudioManager.STREAM_ALARM);
		System.out.println(volume);

		mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM, 3, 0);

		float newVolume = (float) mAudioManager
				.getStreamVolume(AudioManager.STREAM_ALARM);

		System.out.println(newVolume);
		mSpool.play(soundId, newVolume, newVolume, 1, 0, 1f);

		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM,
						(int) volume, 0);

			}
		}, 1000);
	}
}
