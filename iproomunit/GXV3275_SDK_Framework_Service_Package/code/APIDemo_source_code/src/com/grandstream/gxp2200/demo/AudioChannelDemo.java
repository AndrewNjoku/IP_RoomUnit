/****************************************************************************
 *
 * FILENAME:        com.grandstream.gxp2200.demo.AudioChannelDemo.java
 *
 * LAST REVISION:   $Revision: 1.0
 * LAST MODIFIED:   $Date: 2013/01/22 02:14:08 2013-2-25
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

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class AudioChannelDemo extends Activity {
	private AudioManager mAM;

	private OnCheckedChangeListener mListener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			switch (checkedId) {
			case R.id.headset_radioButton:
				mAM.setWiredHeadsetOn(true);
				break;
			case R.id.speaker_radioButton:
				mAM.setSpeakerphoneOn(true);
				break;
			case R.id.handset_radioButton:
				mAM.setHandsetOn(true);
				break;
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.audiochanneldemo);
		mAM = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
		RadioGroup rg = (RadioGroup) findViewById(R.id.hw_interface_group);
		RadioButton headsetRg = (RadioButton) findViewById(R.id.headset_radioButton);
		RadioButton speakerRg = (RadioButton) findViewById(R.id.speaker_radioButton);
		RadioButton handsetRg = (RadioButton) findViewById(R.id.handset_radioButton);

		if (mAM.isWiredHeadsetOn()) {
			headsetRg.setChecked(true);
		} else if (mAM.isSpeakerphoneOn()) {
			speakerRg.setChecked(true);
		} else {
			handsetRg.setChecked(true);
		}
		rg.setOnCheckedChangeListener(mListener);
	}
}
