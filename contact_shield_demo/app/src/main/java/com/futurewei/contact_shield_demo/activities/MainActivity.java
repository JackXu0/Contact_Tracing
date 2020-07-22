package com.futurewei.contact_shield_demo.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.futurewei.contact_shield_demo.BackgroundContactCheckingIntentService;
import com.futurewei.contact_shield_demo.R;
import com.huawei.hmf.tasks.OnCompleteListener;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.contactshield.ContactDetail;
import com.huawei.hms.contactshield.ContactShield;
import com.huawei.hms.contactshield.ContactShieldEngine;
import com.huawei.hms.contactshield.ContactShieldSetting;
import com.huawei.hms.contactshield.ContactSketch;
import com.huawei.hms.contactshield.DiagnosisConfiguration;
import com.huawei.hms.contactshield.PeriodicKey;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "ContactShield_MainActivity";
    // token
    private static String token = "3bdd528fd98947bcaffa0d8fda68ca54";

    private ContactShieldEngine mEngine;

    private Switch mStatusButton;
    private Button mReportButton;
    private Button mCheckButton;
    private Button mSummaryButton;
    private Button mDetailButton;
    private TextView mSumResult;
    private TextView mDetailResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    940);
        }
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    940);
        }
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH},
                    940);
        }
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_ADMIN},
                    940);
        }
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET},
                    940);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        mEngine = ContactShield.getContactShieldEngine(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.reportButton:
                reportPeriodicKeys();
                break;
            case R.id.checkButton:
                putKeysButtonOnClick();
                break;
            case R.id.summary:
                getSketchButtonOnClick();
                break;
            case R.id.detail:
                getDetailButtonOnClick();
                break;
        }
    }

    private void initView() {
        mStatusButton = findViewById(R.id.ContactShieldStatus);
        mStatusButton.setOnCheckedChangeListener((buttonView, isChecked) -> startContactCtrl(isChecked));

        mReportButton = findViewById(R.id.reportButton);
        mReportButton.setOnClickListener(this);

        mCheckButton = findViewById(R.id.checkButton);
        mCheckButton.setOnClickListener(this);

        mSummaryButton = findViewById(R.id.summary);
        mSummaryButton.setOnClickListener(this);

        mDetailButton = findViewById(R.id.detail);
        mDetailButton.setOnClickListener(this);

        mSumResult = findViewById(R.id.sumResult);
        mDetailResult = findViewById(R.id.detailResult);
    }

    private void startContactCtrl(boolean onOff) {
        if (onOff) {
            mStatusButton.setText("ON");
            PendingIntent pendingIntent = PendingIntent.getService(this, 0,
                    new Intent(this, BackgroundContactCheckingIntentService.class),
                    PendingIntent.FLAG_UPDATE_CURRENT);
            mEngine.startContactShield(pendingIntent, ContactShieldSetting.DEFAULT)
                    .addOnSuccessListener(aVoid -> Log.e(TAG, "startContactShield succeed."))
                    .addOnFailureListener(e ->
                            Log.e(TAG, "startContactShield failed, cause: " + e.getMessage()));
        } else {
            mStatusButton.setText("OFF");
            Log.e(TAG, "Stop ContactShield.");
            mEngine.stopContactShield();
        }
    }

    public void reportPeriodicKeys() {
        getKeysButtonOnClick();
    }

    public void putKeysButtonOnClick() {
        // Shared key list file, Please configure according to the actual situation
        String destFilePath = "/storage/emulated/0/Android/data/periodic_key.zip";
        File file = new File(destFilePath);
        Log.e(TAG, "is exist: "+file.exists());
        ArrayList<File> putList = new ArrayList<>();
        putList.add(file);
        DiagnosisConfiguration config = new DiagnosisConfiguration.Builder().build();
        mEngine.putSharedKeyFiles(putList, config, token)
                .addOnSuccessListener(aVoid -> {
                    Log.e(TAG, "putSharedKeyFiles succeeded.");
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "putSharedKeyFiles failed, cause: " + e.getMessage());
                });
    }

    public void getKeysButtonOnClick() {
        mEngine.getPeriodicKey().addOnCompleteListener(
                task -> task.addOnSuccessListener(
                        periodicKeys -> {
                            Log.e(TAG, "getPeriodicKey succeeded, getKeySize: " + periodicKeys.size());
                            for (PeriodicKey key : periodicKeys) {
                                Log.e(TAG, "key: " + Arrays.toString(key.getContent()) + ", " + key.toString());
                            }
                        })
                        .addOnFailureListener(e -> {
                            Log.e(TAG, "getPeriodicKey failed, cause: " + e.getMessage());
                        }));
    }

    public void getSketchButtonOnClick() {
        mEngine.getContactSketch(token).addOnCompleteListener(
                task -> task.addOnSuccessListener(
                        contactSketch -> Log.e(TAG, "getContactSketch succeeded, summary: " + contactSketch.toString()))
                        .addOnFailureListener(
                                e -> {
                                    Log.e(TAG, "getContactSketch failed, cause: " + e.getMessage());
                                }));
    }

    public void getDetailButtonOnClick() {
        mEngine.getContactDetail(token).addOnCompleteListener(
                task -> task.addOnSuccessListener(contactDetails -> {
                    Log.e(TAG, "getContactDetail succeeded.");
                    for (ContactDetail detail : contactDetails) {
                        Log.e(TAG, "getContactDetail, detail: " + detail.toString());
                    }
                }).addOnFailureListener(e -> Log.e(TAG, "getContactDetail failed, cause: " + e.getMessage())));
    }
}