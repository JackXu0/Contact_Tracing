package com.futurewei.contact_shield_demo.activities;

import android.content.Intent;
import android.util.Log;

import com.huawei.hms.push.HmsMessageService;

/**
 * This class is used to integrate the push kit
 */
public class MyPushService extends HmsMessageService {
    private static final String TAG = "PushDemoLog";
    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        Log.i(TAG, "receive token:" + token);
        sendTokenToDisplay(token);
    }

    private void sendTokenToDisplay(String token) {
        Intent intent = new Intent("com.huawei.push.codelab.ON_NEW_TOKEN");
        intent.putExtra("token", token);
        sendBroadcast(intent);
    }

}
