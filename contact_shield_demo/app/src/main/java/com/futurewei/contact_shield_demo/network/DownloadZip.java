/**
 * Copyright © 2020  Futurewei Technologies, Inc. All rights reserved.
 *
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *
 *
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *
 *
 * Unless required by applicable law or agreed to in writing, software
 *
 * distributed under the License is distributed on an "AS IS" BASIS,
 *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the License for the specific language governing permissions and
 *
 * limitations under the License.
 */

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
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
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
//        File myFile = new File(destFilePath.toString());
//
//        Log.e(TAG, "path: "+destFilePath.toString());
//        Log.e(TAG, "is directory: "+ myFile.isDirectory());
//
//        if(myFile.exists()){
//            if(myFile.delete())
//                Log.e(TAG, destFilePath+" delete success");
//            else
//                Log.e(TAG, destFilePath+" delete failed");
//        }
//
//        try {
//            if(myFile.createNewFile())
//                Log.e(TAG, destFilePath+" creation success");
//            else
//                Log.e(TAG, destFilePath+" creation failed");
//
//        } catch (IOException e) {
//            Log.e(TAG, e.toString());
//        }
    }

    @Override
    public void run() {



        try{

            String zip_url = "https://contact-tracing-demo.s3.us-east-2.amazonaws.com/zips/"+userId +".zip";
            File destination_file = new File(context.getExternalCacheDir(),userId +".zip");
            downloadFile(zip_url, destination_file);

//            DownloadManager downloadmanager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
//            Uri uri = Uri.parse("https://contact-tracing-demo.s3.us-east-2.amazonaws.com/zips/"+userId +".zip");
//
//            DownloadManager.Request request = new DownloadManager.Request(uri);
//            request.setTitle("My File");
//            request.setDescription("Downloading");
//            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
//            request.setDestinationInExternalPublicDir("/Android/data/com.futurewei.contact_shield_demo/cache", userId +".zip");
//            downloadmanager.enqueue(request);
//            Log.e("external dir path", context.getExternalCacheDir().getAbsolutePath());

//            Log.e(TAG, "try to download from google");
//            Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();
//
//            Blob blob = storage.get(BlobId.of(bucketName, objectName));
//            blob.downloadTo(destFilePath);
//            Log.e(TAG, destFilePath.toString());

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
            return; // swallow a 404
        } catch (IOException e) {
            return; // swallow a 404
        }
    }
}
