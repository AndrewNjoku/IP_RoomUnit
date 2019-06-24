package com.grandstream.gxp2200.demo;

import android.app.Activity;
import android.content.Context;
import android.hardware.LightsManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class LedDemo extends Activity implements OnClickListener{

	private Button mStartRed,mEndRed;
	private Button mStartGreen,mEndGreen;
	
	private int mRedFlag = 0;
	private int mGreenFlag = 0;
	private LightsManager mLightManager;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.leddemo);
		initData();
		initView();		
	}

	private void initData() {
		
		mLightManager = (LightsManager)this.getSystemService(Context.LIGHTS_SERVICE);
	}

	private void initView() {
		
		mStartRed = (Button)findViewById(R.id.btn_start_red);
		mStartRed.setOnClickListener(this);
		
		mEndRed = (Button)findViewById(R.id.btn_end_red);
		mEndRed.setOnClickListener(this);
		
		mStartGreen = (Button)findViewById(R.id.btn_start_green);
		mStartGreen.setOnClickListener(this);
		
		mEndGreen = (Button)findViewById(R.id.btn_end_green);
		mEndGreen.setOnClickListener(this);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View arg0) {
		
		switch (arg0.getId()) {	
		case R.id.btn_start_red:
			mRedFlag = mLightManager.startLedLight(4,LightsManager.COLOR_RED,1000,0);
			break;			
		case R.id.btn_end_red:			
			mLightManager.closeLight(mRedFlag);
			break;			
		case R.id.btn_start_green:
			mGreenFlag = mLightManager.startLedLight(1,LightsManager.COLOR_GREEN,1000,0);
			break;			
		case R.id.btn_end_green:
			mLightManager.closeLight(mGreenFlag);
			break;
		default:
			break;
		}
		
	}
}
