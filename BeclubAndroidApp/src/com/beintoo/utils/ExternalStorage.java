package com.beintoo.utils;


import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ExternalStorage {

    final static String BEINTOO_DIR = ".beclub";

    private static ExternalStorage instance = null;

    public static ExternalStorage getInstance(){
        if(instance == null)
            instance = new ExternalStorage();

        return instance;
    }

    public String getExternalStorageFile(String fileName) throws Exception{
        File file = new File(getBeintooStorageDir(), fileName);
        if(!file.exists())
            file.createNewFile();
        InputStream inputStream = new FileInputStream(file);
        String fileContent = null;
        if (inputStream != null ) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String receiveString = "";
            StringBuilder stringBuilder = new StringBuilder();

            while ( (receiveString = bufferedReader.readLine()) != null ) {
                stringBuilder.append(receiveString);
            }

            inputStream.close();
            fileContent = stringBuilder.toString();
        }

        return fileContent;
    }

    public void writeFileToExternalStorage(String fileName, String content) throws Exception{
        File file = new File(getBeintooStorageDir(), fileName);

        if(!file.exists())
            file.createNewFile();

        FileOutputStream outputStream;
        outputStream = new FileOutputStream(file);
        outputStream.write(content.getBytes());
        outputStream.close();

        DebugUtility.showLog("WROTE "+file);
    }


    public File getBeintooStorageDir() {
        File dir = new File(Environment.getExternalStorageDirectory(), BEINTOO_DIR);
        if(!dir.exists()){
            if (!dir.mkdirs()) {
                DebugUtility.showLog("CAN'T CREATE DIR");
            }
        }
        return dir;
    }

    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    private boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }
}
