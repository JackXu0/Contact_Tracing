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

package com.futurewei.contact_shield_demo;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.futurewei.contact_shield_demo.activities.NotificationsActivity;
import com.futurewei.contact_shield_demo.fragments.FragmentHome;
import com.futurewei.contact_shield_demo.utils.RiskLevelCalculator;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.contactshield.ContactDetail;
import com.huawei.hms.contactshield.ContactShield;
import com.huawei.hms.contactshield.ContactShieldCallback;
import com.huawei.hms.contactshield.ContactShieldEngine;
import com.huawei.hms.contactshield.ContactSketch;
import com.huawei.hms.contactshield.ContactWindow;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This service will be activated after putShareKeyFile method has been executed successfully
 */
public class BackgroundContactCheckingIntentService extends IntentService {

    String token = "3bdd528fd98947bcaffa0d8fda68ca54";
    private static final String TAG = "ContactShielddd";
    private ContactShieldEngine contactEngine;
    private SharedPreferences sharedPreferences;
    Map<Integer, String> riskLevelMap;

    public BackgroundContactCheckingIntentService(){
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initRiskLevelMap();
        contactEngine = ContactShield.getContactShieldEngine(BackgroundContactCheckingIntentService.this);
        Log.e(TAG, "BackgroundContackCheckingIntentService onCreate");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "BackgroundContackCheckingIntentService onDestroy");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            contactEngine.handleIntent(intent, new ContactShieldCallback() {
                @Override
                public void onHasContact(String s) {
                    Log.e(TAG, "onHasContact");
                    getContactSketch();
                    getContactDetails();
                }

                @Override
                public void onNoContact(String s) {
                    Log.e(TAG, "onNoContact");
                    getContactSketch();
                    getContactDetails();

                }
            });
        }
    }

    void initRiskLevelMap(){
        riskLevelMap = new HashMap<>();
        riskLevelMap.put(0, "NO RISK");
        riskLevelMap.put(1, "LOWEST");
        riskLevelMap.put(2, "LOW");
        riskLevelMap.put(3, "MEDIUM LOW");
        riskLevelMap.put(4, "MEDIUM");
        riskLevelMap.put(5, "MEDIUM_HIGH");
        riskLevelMap.put(6, "HIGH");
        riskLevelMap.put(7, "EXTRA HIGH");
        riskLevelMap.put(8, "HIGHEST");
    }

    void getContactSketch(){
        Task<ContactSketch> contactSketchTask = contactEngine.getContactSketch(token);
        contactSketchTask.addOnSuccessListener((ContactSketch contactSketch) -> Log.e(TAG, "sketch: "+contactSketch.toString()));
    }

    void getContactDetails(){
        Task<List<ContactDetail>> contactSketchTask = contactEngine.getContactDetail(token);
        contactSketchTask.addOnSuccessListener((List<ContactDetail> contactDetails) ->{

                int riskLevel = RiskLevelCalculator.getRiskLevel(contactDetails);
                sharedPreferences = getApplicationContext().getSharedPreferences("dashboard_info",MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("number_of_hits", contactDetails.size());
                editor.putInt("risk_level", riskLevel);
                editor.commit();
                FragmentHome.numberOfHitsTv.setText(""+contactDetails.size());
                FragmentHome.riskLevelTv.setText(riskLevelMap.get(riskLevel));

                sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);
                boolean isNotificationDisabled = sharedPreferences.getBoolean("is_notification_disabled", false);
                if(!isNotificationDisabled && riskLevel >= 4){
                    makeAlertWindow();
                }

                for(ContactDetail cd : contactDetails){
                    Log.e(TAG, "contact detail: "+cd.toString());
                }
            });
    }

    /**
     * A notification will be sent to users if the risk level is greater than or equal to medium
     */
    void makeAlertWindow(){

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Intent notificationIntent = new Intent(getApplicationContext(), NotificationsActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent = PendingIntent.getActivity(getApplicationContext(), 0,
                notificationIntent, 0);
        NotificationCompat.Builder notificationBuilder;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String chanelId = "3000";
            CharSequence name = "Channel Name";
            String description = "Chanel Description";
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel mChannel = new NotificationChannel(chanelId, name, importance);
            mChannel.setDescription(description);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.BLUE);
            notificationManager.createNotificationChannel(mChannel);
            notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), chanelId);
        } else {
            notificationBuilder = new NotificationCompat.Builder(getApplicationContext());
        }
        notificationBuilder.setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("corona virus alert")
                .setContentText("You have been exposed to a corona virus patient recently. Please practice self quarantine and contact your doctor if you are not feeling fine.")
                .setAutoCancel(true)
                .setContentIntent(intent);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());

        // notificationId is a unique int for each notification that you must define
        notificationManagerCompat.notify(1, notificationBuilder.build());


    }
}
