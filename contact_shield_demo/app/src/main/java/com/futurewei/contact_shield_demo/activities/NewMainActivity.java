package com.futurewei.contact_shield_demo.activities;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.futurewei.contact_shield_demo.BackgroundContactCheckingIntentService;
import com.futurewei.contact_shield_demo.R;
import com.futurewei.contact_shield_demo.fragments.*;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.contactshield.ContactShield;
import com.huawei.hms.contactshield.ContactShieldSetting;
import com.huawei.hms.contactshield.PeriodicKey;

import java.io.File;
import java.util.List;

public class NewMainActivity extends AppCompatActivity {

    private Fragment fragment_home;
    private Fragment fragment_faq;
    private Fragment fragment_settings;
    private FragmentManager fm;
    private FragmentTransaction ft;
    private int page = 0;

    SharedPreferences sharedPreferences;
    private static final String TAG = "NewMainActivity";

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    ft = fm.beginTransaction();
                    ft.replace(R.id.frame, fragment_home);
                    ft.commit();
                    page = 0;
                    return true;
                case R.id.navigation_faq:
                    ft = fm.beginTransaction();
                    ft.replace(R.id.frame, fragment_faq);
                    ft.commit();
                    page = 1;
                    return true;
                case R.id.navigation_settings:
                    ft = fm.beginTransaction();
                    ft.replace(R.id.frame, fragment_settings);
                    ft.commit();
                    page = 2;
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.new_main_activity);

        fm = getFragmentManager();
        ft = fm.beginTransaction();

        fragment_home = new fragment_home();
        fragment_faq = new fragment_faq();
        fragment_settings = new fragment_setting();
        ft.replace(R.id.frame, fragment_home);
        ft.commit();

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        permissonRequest();
        registerPush();

    }

    void permissonRequest(){
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    940);
        }

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

    }

    void registerPush(){
        NewMainActivity.MyReceiver receiver = new NewMainActivity.MyReceiver();
        IntentFilter filter=new IntentFilter();
        filter.addAction("com.huawei.codelabpush.ON_NEW_TOKEN");
        NewMainActivity.this.registerReceiver(receiver,filter);
    }

    //push kit
    public class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("com.huawei.codelabpush.ON_NEW_TOKEN".equals(intent.getAction())) {
                String token = intent.getStringExtra("token");
//                tvToken.setText(token);
            }
        }
    }
}