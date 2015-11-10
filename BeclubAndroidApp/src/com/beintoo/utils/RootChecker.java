package com.beintoo.utils;

import java.io.OutputStream;

/**
 * Created by Giulio Bider on 19/09/14.
 * Copyright (c) 2014 Beintoo. All rights reserved.
 */
public class RootChecker {

    public static boolean isRooted() {
        try {
            Process processo = Runtime.getRuntime().exec("su");
            OutputStream stream = processo.getOutputStream();
            stream.flush();
            stream.close();
            return true;
        } catch(Exception e) {
//            e.printStackTrace();
        }
        return false;
    }
}
