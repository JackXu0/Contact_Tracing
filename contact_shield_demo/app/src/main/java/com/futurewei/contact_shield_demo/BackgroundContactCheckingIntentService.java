package com.futurewei.contact_shield_demo;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.futurewei.contact_shield_demo.utils.RiskLevelCalculator;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.contactshield.ContactDetail;
import com.huawei.hms.contactshield.ContactShield;
import com.huawei.hms.contactshield.ContactShieldCallback;
import com.huawei.hms.contactshield.ContactShieldEngine;
import com.huawei.hms.contactshield.ContactSketch;

import java.io.FileDescriptor;
import java.util.List;

import static com.futurewei.contact_shield_demo.fragments.fragment_home.number_of_hits_tv;
import static com.futurewei.contact_shield_demo.fragments.fragment_home.risk_level_map;
import static com.futurewei.contact_shield_demo.fragments.fragment_home.risk_level_tv;

public class BackgroundContactCheckingIntentService extends IntentService {

    String token = "3bdd528fd98947bcaffa0d8fda68ca54";
    private static final String TAG = "ContactShielddd";
    private ContactShieldEngine contactEngine;
    private SharedPreferences sharedPreferences;

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

    void getContactSketch(){
        Task<ContactSketch> contactSketchTask = contactEngine.getContactSketch(token);
        contactSketchTask.addOnSuccessListener(new OnSuccessListener<ContactSketch>() {
            @Override
            public void onSuccess(ContactSketch contactSketch) {
                Log.e(TAG, "sketch: "+contactSketch.toString());
                
            }
        });
    }

    void getContactDetails(){
        Task<List<ContactDetail>> contactSketchTask = contactEngine.getContactDetail(token);
        contactSketchTask.addOnSuccessListener(new OnSuccessListener<List<ContactDetail>>() {
            @Override
            public void onSuccess(List<ContactDetail> contactDetails) {
                for(ContactDetail cd : contactDetails){
                    int risk_level = RiskLevelCalculator.getRiskLevel(contactDetails);
                    sharedPreferences = getApplicationContext().getSharedPreferences("dashboard_info",MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("number_of_hits", contactDetails.size());
                    editor.putInt("risk_level", risk_level);
                    editor.commit();
                    number_of_hits_tv.setText(""+contactDetails.size());
                    risk_level_tv.setText(risk_level_map.get(risk_level));
                    Log.e(TAG, "contact detail: "+cd.toString());
                }
            }
        });
    }
}
