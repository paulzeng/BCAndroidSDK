package com.beintoo.utils;

import android.app.Activity;
import android.content.Intent;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.beintoo.activities.ExternalWebView;

import java.util.Locale;

public class ExternalWebViewJavascriptInterface {
    private WebView mWebView;
    private Activity mActivity;
    public ExternalWebViewJavascriptInterface(WebView webView, Activity activity){
        mWebView = webView;
        mActivity = activity;
    }

    @JavascriptInterface
    public void openHelpUrl(String url, String pageTitle){
        DebugUtility.showLog("-------> url "+url + " tutle "+pageTitle);
        try {
            Intent intent = new Intent(mActivity, ExternalWebView.class);
            intent.putExtra("url", url+"?appversion="+mActivity.getPackageManager().getPackageInfo(mActivity.getPackageName(), 0).versionName.toString()+"&os=android&lang="+ Locale.getDefault().getCountry());
            if(pageTitle != null)
                intent.putExtra("title", pageTitle);
            mActivity.startActivity(intent);
        }catch (Exception e){
            e.printStackTrace();
        }

        DebugUtility.showLog("openHelpUrl "+ url + " "+pageTitle);
    }

    @JavascriptInterface
    public void openContactUs(){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/html");
        intent.putExtra(Intent.EXTRA_EMAIL, "support@beclub.com");
        intent.putExtra(Intent.EXTRA_SUBJECT, "BeClub Android support");
        intent.putExtra(Intent.EXTRA_TEXT, "");
        mActivity.startActivity(Intent.createChooser(intent, "Send Email"));
    }
}