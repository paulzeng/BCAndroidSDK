package com.beintoo.utils;

import android.util.Base64;

import com.beintoo.wrappers.FraudWrapper;
import com.google.gson.Gson;

public class FraudManager {

    private final static int MAX_USERS_ALLOWED = 3;
    private final static String BECLUB_FILE_NAME = ".beclub.json";
    public final static int BANNED_ACTIVITY_RESULT = 2014;

    public static void incrementUsersCount(){
        try {
            String content = ExternalStorage.getInstance().getExternalStorageFile(BECLUB_FILE_NAME);
            FraudWrapper fw = new Gson().fromJson(content, FraudWrapper.class);

            if(fw == null){
                fw = new FraudWrapper();
                fw.setSignupCount(Base64.encodeToString("1".getBytes(), Base64.DEFAULT));
            }if(fw != null && fw.getSignupCount() == null){
                fw.setSignupCount(Base64.encodeToString("1".getBytes(), Base64.DEFAULT));
            }else{
                String signupCountCrypted = fw.getSignupCount();
                Integer signupCount = Integer.parseInt(new String(Base64.decode(signupCountCrypted, Base64.DEFAULT ))) + 1;
                fw.setSignupCount(Base64.encodeToString(signupCount.toString().getBytes(), Base64.DEFAULT));
            }

            ExternalStorage.getInstance().writeFileToExternalStorage(BECLUB_FILE_NAME, new Gson().toJson(fw));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static boolean checkAllowedToSignup(){
        try {
            String content = ExternalStorage.getInstance().getExternalStorageFile(BECLUB_FILE_NAME);
            FraudWrapper fw = new Gson().fromJson(content, FraudWrapper.class);

            if(fw == null){
                return true;
            }else{
                String signupCountCrypted = fw.getSignupCount();
                Integer signupCount = null;
                if(signupCountCrypted != null){
                    signupCount = Integer.parseInt(new String(Base64.decode(signupCountCrypted, Base64.DEFAULT ))) + 1;
                }
                if(signupCountCrypted == null){
                    return true;
                }else if(signupCount != null && signupCount <= MAX_USERS_ALLOWED){
                    return true;
                }else{
                    return false;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        /**
         * We go safe in case of an error to avoid blocking the app
         */
        return true;
    }
}
