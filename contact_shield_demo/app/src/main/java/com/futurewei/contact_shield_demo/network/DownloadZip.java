package com.futurewei.contact_shield_demo.network;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import java.nio.file.Path;

import static android.content.Context.MODE_PRIVATE;

public class DownloadZip extends Thread {
    private static final String TAG = "download ZIP";
    Context context;
    Handler handler;
    Message msg=new Message();
    String userId;
//    String projectId = "contact-tracing-demo-281120";
//    String bucketName = "zip001_futurewei";
//    String objectName = "";
    String projectId = "contact-shield-demo";
    String bucketName = "contact_shield_demp";
    String objectName = "zips/";

    // The path to which the file should be downloaded
    Path destFilePath;

    public DownloadZip(Context context, Handler handler){
        this.context = context;
        this.handler = handler;
        userId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        objectName = objectName+userId +".zip";

        destFilePath = Paths.get(new File(context.getExternalCacheDir(),userId +".zip").getAbsolutePath());
        File myFile = new File(destFilePath.toString());

        Log.e(TAG, "path: "+destFilePath.toString());
        Log.e(TAG, "is directory: "+ myFile.isDirectory());

        if(myFile.exists()){
            if(myFile.delete())
                Log.e(TAG, destFilePath+" delete success");
            else
                Log.e(TAG, destFilePath+" delete failed");
        }

        try {
            if(myFile.createNewFile())
                Log.e(TAG, destFilePath+" creation success");
            else
                Log.e(TAG, destFilePath+" creation failed");

        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }
    }

    @Override
    public void run() {

        try{
            Log.e(TAG, "try to download from google");
            Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();

            Blob blob = storage.get(BlobId.of(bucketName, objectName));
            blob.downloadTo(destFilePath);
            Log.e(TAG, destFilePath.toString());

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
}
