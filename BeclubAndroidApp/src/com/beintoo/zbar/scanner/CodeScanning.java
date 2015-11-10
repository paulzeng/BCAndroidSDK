package com.beintoo.zbar.scanner;

import android.app.Activity;
import android.content.Intent;


public class CodeScanning {
	public static final int SCAN_RESULT_REQUEST = 10000;	
	private static final CodeScanning instance = new CodeScanning();
	private Activity mActivity;

    public interface CodeScanningCallback{
        public void onCodeScanned(String codeNumber, Integer codeType);
        public void onCodeFailed();
    }

    private CodeScanningCallback mCallback;

	public CodeScanning(){

    }
	
	public void startScanning(Activity activity, CodeScanningCallback callback){
		mActivity = activity;
        mCallback = callback;
		Intent intent = new Intent(activity, ZBarScannerActivity.class);
		activity.startActivityForResult(intent, SCAN_RESULT_REQUEST);
		
	}
	
	public void scanResultHandler(int resultCode, Intent data){
		if (resultCode == -1) 
	    {
	        // Scan result is available by making a call to data.getStringExtra(ZBarConstants.SCAN_RESULT)
	        // Type of the scan result is available by making a call to data.getStringExtra(ZBarConstants.SCAN_RESULT_TYPE)

            mCallback.onCodeScanned(data.getStringExtra(ZBarConstants.SCAN_RESULT), data.getIntExtra(ZBarConstants.SCAN_RESULT_TYPE,0));

	        // The value of type indicates one of the symbols listed in Advanced Options below.
	    } else if(resultCode == 0) {
	        mCallback.onCodeFailed();
	    }
	}

	public static CodeScanning getInstance() {
		return instance;
	}	
}
