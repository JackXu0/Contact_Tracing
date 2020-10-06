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

/**
 * This class functions as the download handler
 * Step 1: Request the server to generate corresponding ZIP file and upload this ZIP file to Google Storage
 * Step 2: Use Google Storage APIs to download this ZIP, and put to contact shield SDK
 */
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
        }

        else if (responseCode == 2){
            Toast.makeText(context, "Download Failed!", Toast.LENGTH_SHORT).show();
            ((Activity)context).finish();
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
        File file = new File(destFilePath);
        Log.e(TAG, file.getAbsolutePath());
        Log.e(TAG, "if file exists: "+file.exists());
        ArrayList<File> putList = new ArrayList<>();
        putList.add(file);
        DiagnosisConfiguration config = new DiagnosisConfiguration.Builder()
                .setInitialRiskLevelRiskValues(1,2,3,4,5,6,7,8)
                .setDurationRiskValues(1,2,3,4,5,6,7,8)
                .setDaysAfterContactedRiskValues(1,2,3,4,5,6,7,8)
                .setAttenuationRiskValues(1,2,3,4,5,6,7,8)
                .build();


        ContactShield.getContactShieldEngine(context).putSharedKeyFiles(putList, config, token)
                .addOnSuccessListener(aVoid -> {
                    Log.e(TAG, "putSharedKeyFiles succeeded.");
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "putSharedKeyFiles failed, cause: " + e.getMessage());
                });
    }

}
