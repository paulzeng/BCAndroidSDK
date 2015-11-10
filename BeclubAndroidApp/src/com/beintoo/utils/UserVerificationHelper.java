package com.beintoo.utils;

import android.content.Context;
import android.support.v4.app.FragmentManager;

import com.beintoo.dialogs.EmailNotVerifiedDialog;
import com.beintoo.wrappers.MissionWrapper;

public class UserVerificationHelper {
    public static boolean isUserVerified(Context context, FragmentManager fragmentManager, MissionWrapper mission){
        if(mission != null && mission.getReward() != null && !MemberAuthStore.isUserVerified(context)){
            new EmailNotVerifiedDialog(context).show(fragmentManager, "email-not-verified");
            return false;
        }

        return true;
    }
}
