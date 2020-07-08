package com.futurewei.contact_shield_demo;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.contactshield.ContactShield;
import com.huawei.hms.contactshield.ContactShieldCallback;
import com.huawei.hms.contactshield.ContactShieldEngine;
import com.huawei.hms.contactshield.ContactSketch;

import java.io.FileDescriptor;

public class BackgroundContactCheckingIntentService extends IntentService {

    private static final String TAG = "ContactShielddd";
    private ContactShieldEngine contactEngine;

    public BackgroundContactCheckingIntentService() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
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
                public void onHasContact() {
                    Log.e(TAG, "onHasContact");
                    getContactSketch();
                }
            });
        }
    }

    void getContactSketch(){
        Task<ContactSketch> contactSketchTask = contactEngine.getContactSketch();
        contactSketchTask.addOnSuccessListener(new OnSuccessListener<ContactSketch>() {
            @Override
            public void onSuccess(ContactSketch contactSketch) {
                Log.e("sketch", contactSketch.toString());
            }
        });
    }
}
