package com.futurewei.contact_shield_demo.network;


import android.app.DownloadManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Paths;
import java.nio.file.Path;

import static android.content.Context.MODE_PRIVATE;

/**
 * This downloader is used to download ZIP file from Google Storage. This ZIP file will then be put into contact shield SDK
 */
public class DownloadZip extends Thread {
    private static final String TAG = "download ZIP";
    Context context;
    Handler handler;
    Message msg=new Message();
    String userId;
    String projectId = "contact-shield-demo";
    String bucketName = "contact_shield_demo";
    String objectName = "zips/";



    // The path to which the file should be downloaded
    Path destFilePath;

    public DownloadZip(Context context, Handler handler){
        this.context = context;
        this.handler = handler;
        userId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        objectName = objectName+userId +".zip";

        destFilePath = Paths.get(new File(context.getExternalCacheDir(),userId +".zip").getAbsolutePath());
    }

    @Override
    public void run() {



        try{

            String zip_url = "https://contact-tracing-demo.s3.us-east-2.amazonaws.com/zips/"+userId +".zip";
            File destination_file = new File(context.getExternalCacheDir(),userId +".zip");
            downloadFile(zip_url, destination_file);

            final SharedPreferences sharedPreferences = context.getSharedPreferences("last_download_timeStamp",MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putLong("last_download_timeStamp", System.currentTimeMillis());
            editor.commit();
        }finally {
            msg.what = 6;
            Bundle b =new Bundle();
            b.putInt("response_code",1);
            b.putString("response_body", destFilePath.toAbsolutePath().toString());
            msg.setData(b);
            handler.sendMessage(msg);
        }
    }

    private static void downloadFile(String url, File outputFile) {
        try {
            URL u = new URL(url);
            URLConnection conn = u.openConnection();
            int contentLength = conn.getContentLength();

            DataInputStream stream = new DataInputStream(u.openStream());

            byte[] buffer = new byte[contentLength];
            stream.readFully(buffer);
            stream.close();

            DataOutputStream fos = new DataOutputStream(new FileOutputStream(outputFile));
            fos.write(buffer);
            fos.flush();
            fos.close();
        } catch(FileNotFoundException e) {
            return;
        } catch (IOException e) {
            return;
        }
    }
}