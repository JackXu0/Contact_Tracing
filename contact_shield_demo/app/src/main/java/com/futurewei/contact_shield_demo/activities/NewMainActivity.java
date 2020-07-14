package com.futurewei.contact_shield_demo.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
    protected void onResume() {
        super.onResume();
        sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);
        boolean is_app_disabled = sharedPreferences.getBoolean("is_app_disabled", false);

        is_app_disabled = false;

        if(!is_app_disabled)
            engine_start_pre_check();

        getPeriodicalKey("asdad");
    }

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

        sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);
        boolean is_app_disabled = sharedPreferences.getBoolean("is_app_disabled", false);

        //If not disabled, start the engine
//        if(!is_app_disabled){
//            Log.d(TAG, "onCreate >> !is_app_disabled");
//            engine_start_pre_check();
//        }
//        engine_start();



    }

    void engine_start_pre_check(){
        Log.d(TAG, "engine_start_pre_check");
        Task<Boolean> isRunningTask = ContactShield.getContactShieldEngine(this).isContactShieldRunning();
        isRunningTask.addOnSuccessListener(aBoolean -> {
            if(!aBoolean){
                engine_start();
                Log.e(TAG, "isContactShieldRunning >> NO");
            }else{
                Log.e(TAG, "isContactShieldRunning >> YES");
            }
        });
    }

    void engine_start(){
        Log.d(TAG, "engine_start");
        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 0, new Intent(getApplicationContext(), BackgroundContactCheckingIntentService.class),
                PendingIntent.FLAG_UPDATE_CURRENT);


        ContactShield.getContactShieldEngine(this).startContactShield(pendingIntent, ContactShieldSetting.DEFAULT)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "startContactShield >> Success"))
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    Log.e(TAG, "startContactShield >> Failure");
                });


    }

    void getPeriodicalKey(String tan){
        Task<List<PeriodicKey>> task_pk = ContactShield.getContactShieldEngine(this).getPeriodicKey();

        task_pk.addOnSuccessListener(new OnSuccessListener<List<PeriodicKey>>() {
            @Override
            public void onSuccess(List<PeriodicKey> periodicKeys) {
                Log.e("get periodical key","success");
                Log.e("length", periodicKeys.size()+"");
                for(PeriodicKey pk : periodicKeys){
                    byte[] bs = pk.getContent();
                    for(byte b : bs){
                        Log.e("bytee", b+"");
                    }
                    Log.e("pk", pk.toString());
                }

            }
        });
    }


}