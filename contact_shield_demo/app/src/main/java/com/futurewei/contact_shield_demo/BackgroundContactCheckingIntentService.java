package com.futurewei.contact_shield_demo;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.huawei.hms.contactshield.ContactShield;
import com.huawei.hms.contactshield.ContactShieldCallback;
import com.huawei.hms.contactshield.ContactShieldEngine;

import java.io.FileDescriptor;

public class BackgroundContactCheckingIntentService extends IntentService {

    private static final String TAG = "ContactShield_BackgroundContackCheckingIntentService";
    private ContactShieldEngine contactEngine;

    public BackgroundContactCheckingIntentService() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        contactEngine = ContactShield.getContactShieldEngine(BackgroundContactCheckingIntentService.this);
        Log.d(TAG, "BackgroundContackCheckingIntentService onCreate");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "BackgroundContackCheckingIntentService onDestroy");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            contactEngine.handleIntent(intent, new ContactShieldCallback() {
                @Override
                public void onHasContact() {
                    Log.d(TAG, "onHasContact");
                }
            });
        }
    }
}
