package com.futurewei.contact_shield_demo.handlers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.futurewei.contact_shield_demo.activities.InternetConnectionErrorActivity;
import com.futurewei.contact_shield_demo.network.DownloadZip;
import com.huawei.hms.contactshield.ContactShield;
import com.huawei.hms.contactshield.DiagnosisConfiguration;

import java.io.File;
import java.util.ArrayList;

public class DownloadHandler extends Handler {

    String token = "3bdd528fd98947bcaffa0d8fda68ca54";
    Context context;
    String TAG = "Download Handler";
    Handler handler;


    public DownloadHandler(Context context, String tag){
        this.context = context;
        this.TAG = tag;
        this.handler = this;
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        Bundle b = msg.getData();
        int responseCode = b.getInt("response_code");
        String responseBody = b.getString("response_body");

        if(responseCode == 0){
            context.startActivity(new Intent(context, InternetConnectionErrorActivity.class));
            ((Activity)context).finish();
            return;
//                Toast.makeText(getApplicationContext(), "No Internet Connection!", Toast.LENGTH_SHORT).show();
        }
        //TODO: whether to put this here
        //TODO: whether to display detail errors
        else if (responseCode == 2){
            Toast.makeText(context, "Download Failed!", Toast.LENGTH_SHORT).show();
            return;
        }

        switch (msg.what){

            // handler for generating PK ZIP
            case 5:
                Log.e(TAG, "Generate PK ZIP handler activated");

                // If PK ZIP generated successfully, download zip from google storage
                new DownloadZip(context, handler).start();

                break;

            //handler for downloading ZIP
            case 6:
                Log.e(TAG, "Download ZIP handler activated");

                // If ZIP downloaded successfully, put zip to contact shield SDK
                String destFilePath = responseBody;
                putSharedKey(destFilePath);

                break;

            default:
                Log.e(TAG, "default handler triggered");
                break;
        }
    }

    void putSharedKey(String destFilePath){
        File file = new File(destFilePath.toString());
        Log.e(TAG, file.getAbsolutePath());
        Log.e(TAG, "if file exists: "+file.exists());
        ArrayList<File> putList = new ArrayList<>();
        putList.add(file);
        DiagnosisConfiguration config = new DiagnosisConfiguration.Builder().build();
        ContactShield.getContactShieldEngine(context).putSharedKeyFiles(putList, config, token)
                .addOnSuccessListener(aVoid -> {
                    Log.e(TAG, "putSharedKeyFiles succeeded.");
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "putSharedKeyFiles failed, cause: " + e.getMessage());
                });
    }

}
