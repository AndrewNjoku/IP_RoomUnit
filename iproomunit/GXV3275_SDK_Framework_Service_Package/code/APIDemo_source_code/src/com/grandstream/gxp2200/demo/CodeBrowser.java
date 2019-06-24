/*****************************************************************************
 *
 * FILENAME:        com.grandstream.gxp2200.demo.CodeBrowser.java
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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;

public class CodeBrowser extends Activity {

	public static final String VIEW_CODE_DATAEXTRA = "code_name";

	private WebView mWebView;
	private Map<Character, String> mTransMap = new HashMap<Character, String>();
	private String mHtml;
	private String mCodeType;
	private String mEnCode;
	private String mFileName;
	private final static String PATH_JS = "file:///android_asset/js/";
	private final static String PATH_CODE = "code/";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		mTransMap.put(' ', "&nbsp;");
		mTransMap.put('<', "&lt;");
		mTransMap.put('&', "&amp;");
		mTransMap.put('>', "&gt;");

		mFileName = getIntent().getExtras().getString(VIEW_CODE_DATAEXTRA);
		initView();
		handleViewContent();
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void initView() {
		mWebView = new WebView(this);
		mWebView.getSettings().setBuiltInZoomControls(true);
		mWebView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
		mWebView.setHorizontalScrollBarEnabled(true);
		mWebView.setHorizontalScrollbarOverlay(true);
		mWebView.setVerticalScrollBarEnabled(true);
		mWebView.resumeTimers();
		mWebView.getSettings().setSupportZoom(true);
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.getSettings().setAllowFileAccess(true);
		mWebView.getSettings().setUseWideViewPort(true);
		setContentView(mWebView);
		mEnCode = "UTF-8";
		mCodeType = " lang-java";
	}

	private void handleViewContent() {
		new AsyncTask<Object, Object, Object>() {
			@Override
			protected Object doInBackground(Object... arg0) {
				setLoad(mFileName);
				return null;
			}

			protected void onPostExecute(Object result) {
				mWebView.loadDataWithBaseURL(PATH_JS, mHtml, "text/html", mEnCode, null);
			}

		}.execute();
	}

	private void setLoad(String filename) {
		if (mCodeTempStyle == null) {
			InputStream inptemp = this.getResources().openRawResource(R.raw.htmltemp);
			Scanner aScanner = new Scanner(inptemp);
			StringBuffer tempBuffer = new StringBuffer();
			while (aScanner.hasNextLine()) {
				tempBuffer.append(aScanner.nextLine());
				tempBuffer.append("\n");
			}
			mCodeTempStyle = tempBuffer.toString();
		}

		// Build a buffered character input stream
		String line = null;
		StringBuffer buffer = new StringBuffer();

		Reader reader = null;
		try {
			reader = new InputStreamReader(getResources().getAssets().open(PATH_CODE + filename));
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		Scanner scanner;
		scanner = new Scanner(reader);
		while (scanner.hasNextLine()) {
			line = scanner.nextLine();
			char chars[] = line.toCharArray();
			for (char c : chars) {
				buffer.append(((mTransMap.get(c) == null) ? c : mTransMap.get(c)));
			}
			buffer.append("\n");
		}

		String codeBoday;
		codeBoday = buffer.toString();
		mHtml = String.format(mCodeTempStyle, mEnCode, "prettify-day.css", mCodeType, codeBoday);
	}

	@Override
	protected void onDestroy() {
		if (mWebView != null) {
			mWebView.pauseTimers();
			mWebView.stopLoading();
			mWebView.clearView();
		}
		super.onDestroy();
	}

	private static String mCodeTempStyle = null;
}
