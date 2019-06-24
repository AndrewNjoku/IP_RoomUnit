/****************************************************************************
 *
 * FILENAME:        com.grandstream.gxp2200.demo.ClickToCall.java
 *
 * LAST REVISION:   $Revision: 1.0
 * LAST MODIFIED:   $Date: Dec 5, 2012
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
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class ClickToCall extends Activity {

	final static String SCHEME_WTAI_MC = "wtai://wp/mc;";
	final static String SCHEME_TEL = "tel:";

	private WebView mWebView;

	private static String mPath = "file:///android_asset/code/clicktodial.html";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.clicktocall);
		mWebView = (WebView) findViewById(R.id.wv1);
		mWebView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
		mWebView.setHorizontalScrollBarEnabled(true);
		mWebView.setHorizontalScrollbarOverlay(true);
		mWebView.setVerticalScrollBarEnabled(true);
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.resumeTimers();
		mWebView.setWebViewClient(new myWebClient());
		mWebView.loadUrl(mPath);
	}

	public class myWebClient extends WebViewClient {

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(mPath);
			boolean needOverride = false;

			// wtai://wp/mc;
			if (url.startsWith(SCHEME_WTAI_MC)) {
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(WebView.SCHEME_TEL
						+ url.substring(SCHEME_WTAI_MC.length())));
				startActivity(intent);
				needOverride = true;
			} else if (url.startsWith(WebView.SCHEME_TEL)) {
				// tel:number
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(WebView.SCHEME_TEL
						+ url.substring(WebView.SCHEME_TEL.length())));
				startActivity(intent);
				needOverride = true;
			}
			return needOverride;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
			mWebView.goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
