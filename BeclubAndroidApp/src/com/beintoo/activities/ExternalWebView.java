package com.beintoo.activities;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.beintoo.beclubapp.R;
import com.beintoo.utils.DebugUtility;
import com.beintoo.utils.ExternalWebViewJavascriptInterface;

public class ExternalWebView extends BeNotificationActivity {

	private WebView mWebView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setDisplayUseLogoEnabled(true);
		setContentView(R.layout.external_webview);
		mWebView = (WebView) findViewById(R.id.webView);

		mWebView.setWebViewClient(new webViewClient());
		mWebView.setWebChromeClient(new webChromeClient());
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		mWebView.addJavascriptInterface(new ExternalWebViewJavascriptInterface(
				mWebView, this), "Beclub");

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			if (extras.getString("url") != null) {
				mWebView.loadUrl(extras.getString("url"));
				DebugUtility.showLog("got extras url "
						+ extras.getString("url"));
			}
			if (extras.getString("title") != null) {
				setTitle(extras.getString("title"));
			}
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home)
			finish();
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onStart() {
		super.onStart();

	}

	@Override
	public void onStop() {
		super.onStop();

	}

	public class webViewClient extends WebViewClient {
		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
			if (!url.contains("&fromSdk=true")) {
				url += "&fromSdk=true";
				mWebView.loadUrl(url);
				return;
			}
			DebugUtility.showLog("LOADING URL " + url);
			super.onPageStarted(view, url, favicon);
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			findViewById(R.id.progressBar).setVisibility(View.GONE);
			super.onPageFinished(view, url);
		}

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {

			return super.shouldOverrideUrlLoading(view, url);
		}

		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			super.onReceivedError(view, errorCode, description, failingUrl);
			DebugUtility.showLog("ERRORCODE " + errorCode + " description "
					+ description);
		}
	}

	public class webChromeClient extends WebChromeClient {
		@Override
		public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
			DebugUtility.showLog("consoleMessage " + consoleMessage);
			return super.onConsoleMessage(consoleMessage);
		}

		@Override
		public void onConsoleMessage(String message, int lineNumber,
				String sourceID) {
			DebugUtility.showLog("message " + message);
			super.onConsoleMessage(message, lineNumber, sourceID);
		}
	}
}
