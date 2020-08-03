package com.futurewei.contact_shield_demo.activities;

import android.Manifest;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;
import androidx.core.content.ContextCompat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentTransaction;

import com.futurewei.contact_shield_demo.R;
import com.futurewei.contact_shield_demo.fragments.*;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class NewMainActivity extends AppCompatActivity {

    private Fragment fragmentHome;
    private Fragment fragmentFaq;
    private Fragment fragmentSettings;
    private FragmentManager fm;
    private FragmentTransaction ft;
    private int page;

    private static final String TAG = "NewMainActivity";

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = (@NonNull MenuItem item) -> {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    ft = fm.beginTransaction();
                    ft.replace(R.id.frame, fragmentHome);
                    ft.commit();
                    page = 0;
                    return true;
                case R.id.navigation_faq:
                    ft = fm.beginTransaction();
                    ft.replace(R.id.frame, fragmentFaq);
                    ft.commit();
                    page = 1;
                    return true;
                case R.id.navigation_settings:
                    ft = fm.beginTransaction();
                    ft.replace(R.id.frame, fragmentSettings);
                    ft.commit();
                    page = 2;
                    return true;
                default:
                    return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.new_main_activity);

        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();

        fragmentHome = new FragmentHome();
        fragmentFaq = new FragmentFaq();
        fragmentSettings = new fragmentSetting();
        ft.replace(R.id.frame, fragmentHome);
        ft.commit();

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        permissonRequest();
        registerPush();

    }

    void permissonRequest(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    940);
        }

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    940);
        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    940);
        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH},
                    940);
        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_ADMIN},
                    940);
        }
        if (ContextCompat.checkSelfPermission(this,
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