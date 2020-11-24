package com.futurewei.contact_shield_demo.activities;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.huawei.hms.contactshield.ContactDetail;
import com.huawei.hms.contactshield.ContactShield;
import com.huawei.hms.contactshield.ContactShieldEngine;
import com.huawei.hms.contactshield.ContactShieldSetting;
import com.huawei.hms.contactshield.DiagnosisConfiguration;
import com.huawei.hms.contactshield.PeriodicKey;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import com.futurewei.contact_shield_demo.utils.BackgroundContackCheckingIntentService;
import com.futurewei.contact_shield_demo.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "ContactShield_MainActivity";
    // token
    private static String token = "TOKEN_FOR_INDIVIDUAL";

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
            mEngine.startContactShield(ContactShieldSetting.DEFAULT)
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "startContactShield succeed."))
                    .addOnFailureListener(e ->
                            Log.d(TAG, "startContactShield failed, cause: " + e.getMessage()));
        } else {
            mStatusButton.setText("OFF");
            Log.d(TAG, "Stop ContactShield.");
            mEngine.stopContactShield();
        }
    }

    public void reportPeriodicKeys() {

        mEngine.getPeriodicKey().addOnCompleteListener(
                task -> task.addOnSuccessListener(
                        periodicKeys -> {
                            Log.d(TAG, "getPeriodicKey succeeded, getKeySize: " + periodicKeys.size());
                            Toast.makeText(this, "GET "+periodicKeys.size()+" periodic keys", Toast.LENGTH_LONG).show();
                            for (PeriodicKey key : periodicKeys) {
                                Log.d(TAG, "key: " + Arrays.toString(key.getContent()) + ", " + key.toString());
                            }
                        })
                        .addOnFailureListener(e -> {
                            Log.d(TAG, "getPeriodicKey failed, cause: " + e.getMessage());
                        }));

    }

    public void putKeysButtonOnClick() {
        // Shared key list file, Please configure according to the actual situation
        String filename = Environment.getExternalStorageDirectory().toString()
                + File.separator + "sdcard" + File.separator + "Download" + File.separator + "xx.zip";
        File file = new File(filename);
        ArrayList<File> putList = new ArrayList<>();
        putList.add(file);
        DiagnosisConfiguration config = new DiagnosisConfiguration.Builder().build();

        PendingIntent pendingIntent = PendingIntent.getService(this, 0,
                new Intent(this, BackgroundContackCheckingIntentService.class),
                PendingIntent.FLAG_UPDATE_CURRENT);

        mEngine.putSharedKeyFiles(pendingIntent, putList, config, token)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "putSharedKeyFiles succeeded.");
                })
                .addOnFailureListener(e -> {
                    Log.d(TAG, "putSharedKeyFiles failed, cause: " + e.getMessage());
                });
    }

    public void getKeysButtonOnClick() {
        switch (token) {
            case "TOKEN_WINDOW_MODE":
                mEngine.getContactWindow(token).addOnCompleteListener(
                        task -> task.addOnSuccessListener(
                                contactSketch -> Log.d(TAG, "getContactWindow succeeded, summary: " + contactSketch.toString()))
                                .addOnFailureListener(
                                        e -> {
                                            Log.d(TAG, "getContactWindow failed, cause: " + e.getMessage());
                                        }));
                break;
            default:
                mEngine.getContactSketch(token).addOnCompleteListener(
                        task -> task.addOnSuccessListener(
                                contactSketch -> Log.d(TAG, "getContactSketch succeeded, summary: " + contactSketch.toString()))
                                .addOnFailureListener(
                                        e -> {
                                            Log.d(TAG, "getContactSketch failed, cause: " + e.getMessage());
                                        }));
        }
    }

    public void getSketchButtonOnClick() {
        mEngine.getContactSketch(token).addOnCompleteListener(
                task -> task.addOnSuccessListener(
                        contactSketch -> Log.d(TAG, "getContactSketch succeeded, summary: " + contactSketch.toString()))
                        .addOnFailureListener(
                                e -> {
                                    Log.d(TAG, "getContactSketch failed, cause: " + e.getMessage());
                                }));
    }

    public void getDetailButtonOnClick() {
        mEngine.getContactDetail(token).addOnCompleteListener(
                task -> task.addOnSuccessListener(contactDetails -> {
                    Log.d(TAG, "getContactDetail succeeded.");
                    for (ContactDetail detail : contactDetails) {
                        Log.d(TAG, "getContactDetail, detail: " + detail.toString());
                    }
                }).addOnFailureListener(e -> Log.d(TAG, "getContactDetail failed, cause: " + e.getMessage())));
    }
}