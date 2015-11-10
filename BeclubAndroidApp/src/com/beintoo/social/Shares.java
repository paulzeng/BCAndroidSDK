package com.beintoo.social;


import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Telephony;
import android.widget.Toast;

import java.util.List;

public class Shares {
    public static void smsShare(Context context, String text){
        Intent intent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) // Android 4.4 and up
        {
            String defaultSmsPackageName = Telephony.Sms.getDefaultSmsPackage(context);

            intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, text);

            if (defaultSmsPackageName != null) // Can be null in case that there is no default, then the user would be able to choose any app that supports this intent.
            {
                intent.setPackage(defaultSmsPackageName);
            }
        }
        else
        {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setType("vnd.android-dir/mms-sms");
            intent.putExtra("sms_body", text);
        }
        context.startActivity(intent);
    }

    public static void groupSmsShare(Context context, String text, List<String> recipients){
        String recips = "";
        for(String s : recipients) {
            recips += s + ";";
        }

        Intent intent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) // Android 4.4 and up
        {
            String defaultSmsPackageName = Telephony.Sms.getDefaultSmsPackage(context);

            intent = new Intent(Intent.ACTION_SENDTO);
            intent.setType("text/plain");
            intent.putExtra("sms_body", text);
            intent.setData(Uri.parse("smsto:" + Uri.encode(recips)));
            //intent.putExtra("address", "3487816158; 3392232959");

            if (defaultSmsPackageName != null) // Can be null in case that there is no default, then the user would be able to choose any app that supports this intent.
            {
                intent.setPackage(defaultSmsPackageName);
            }
        }
        else
        {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setType("vnd.android-dir/mms-sms");
            intent.putExtra("sms_body", text);
            intent.setData(Uri.parse("smsto:" + Uri.encode(recips)));
        }
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(context, "Unable to open SMS app.", Toast.LENGTH_SHORT).show();
        }
    }

    public static void emailShare(Context context, String message, String subject){
        Intent email = new Intent(Intent.ACTION_SEND);
        email.putExtra(Intent.EXTRA_SUBJECT, subject);
        email.putExtra(Intent.EXTRA_TEXT, message);
        email.setType("text/html");
        // need this to prompts email client only
        //email.setType("message/rfc822");
        context.startActivity(email);
    }
}
