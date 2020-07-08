package com.futurewei.contact_shield_demo.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
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
import com.huawei.hms.contactshield.PeriodicKey;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "ContactShield_MainActivity";

    private static final byte[] CONTENT_BYTE =
            new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16};

    private static final int VALID_TIME = 100;

    private static final int LIFE_TIME = 100;

    private static final int INITIALRISKLEVEL = 0;

    private ContactShieldEngine mEngine;

    private Switch mStatusButton;
    private Button mReportButton;
    private Button mCheckButton;
    private Button mSummaryButton;
    private Button mDetailButton;
    private TextView mSumResult;
    private TextView mDetailResult;

    private  List<PeriodicKey> reportedList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                    940);
        }
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_COARSE_LOCATION},
                    940);
        }
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.BLUETOOTH},
                    940);
        }
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.BLUETOOTH_ADMIN},
                    940);
        }
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.INTERNET},
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
        mStatusButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                startContactCtrl(isChecked);
            }
        });

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
            mEngine.startContactShield(pendingIntent,
                    ContactShieldSetting.DEFAULT).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception e) {
                    e.printStackTrace();
//                    Log.d(TAG, "startNearbyContactChecking onFailure," + e.getMessage());
                }
            });
        } else {
            mStatusButton.setText("OFF");
            mEngine.stopContactShield().addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception e) {
                    Log.d(TAG, "stopNearbyContactChecking onFailure," + e.getMessage());
                }
            });
            resetAllButtonOnClick();
        }
    }

    public void reportPeriodicKeys() {
        getKeysButtonOnClick();
    }

    public void putKeysButtonOnClick() {
        List<PeriodicKey> putList = new ArrayList<>();
        PeriodicKey periodicKey = new PeriodicKey.Builder()
                .setContent(CONTENT_BYTE)
                .setPeriodKeyValidTime(VALID_TIME)
                .setPeriodKeyLifeTime(LIFE_TIME)
                .setInitialRiskLevel(INITIALRISKLEVEL)
                .build();
        putList.add(periodicKey);
        mEngine.putSharedKey(putList);
    }

    public void getKeysButtonOnClick() {
        mEngine.getPeriodicKey().addOnCompleteListener(new OnCompleteListener<List<PeriodicKey>>() {
            @Override
            public void onComplete(Task<List<PeriodicKey>> task) {
                reportedList = task.getResult();
                Log.d(TAG, "getKeysButtonOnClick getKeySize: " + reportedList.size());
            }
        });
    }

    public void getSketchButtonOnClick() {
        mEngine.getContactSketch().addOnCompleteListener(new OnCompleteListener<ContactSketch>() {
            @Override
            public void onComplete(Task<ContactSketch> task) {
                ContactSketch sketch = task.getResult();
                mSumResult.setText(sketch.toString());
                Log.d(TAG, "getSketchButtonOnClick " + sketch.toString());
            }
        });
    }

    public void getDetailButtonOnClick() {
        mEngine.getContactDetail().addOnCompleteListener(new OnCompleteListener<List<ContactDetail>>() {
            @Override
            public void onComplete(Task<List<ContactDetail>> task) {
                List<ContactDetail> details = task.getResult();
                for (ContactDetail detail : details) {
                    Log.d(TAG, "getDetailButtonOnClick " + detail.toString());
                }
                mDetailResult.setText(String.valueOf(details));
            }
        });
    }

    public void resetAllButtonOnClick() {
        mEngine.clearData().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Log.d(TAG, "resetAllButtonOnClick onFailure," + e.getMessage());
            }
        });
    }
}

