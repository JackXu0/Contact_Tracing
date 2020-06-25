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

import java.io.File;

public class NewMainActivity extends AppCompatActivity {

    private Fragment fragment_home;
    private Fragment fragment_faq;
    private Fragment fragment_settings;
    private FragmentManager fm;
    private FragmentTransaction ft;
    private int page = 0;

    SharedPreferences sharedPreferences;

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

        if(!is_app_disabled)
            engine_start_pre_check();
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
        if(!is_app_disabled){
            Log.e("eee", "eeeeeee");
            engine_start_pre_check();
        }



    }

    void engine_start_pre_check(){
        Task<Boolean> isRunningTask = ContactShield.getContactShieldEngine(this).isContactShieldRunning();
        isRunningTask.addOnSuccessListener(new OnSuccessListener<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean) {
                if(!aBoolean){
                    engine_start();
                    Log.e("Is running", "NO");
                }else{
                    Log.e("Is running", "YES");
                }
            }
        });
    }

    void engine_start(){
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, new Intent(this, BackgroundContactCheckingIntentService.class),
                PendingIntent.FLAG_UPDATE_CURRENT);


        Task<Void> engine_start_task = ContactShield.getContactShieldEngine(this).startContactShield(pendingIntent, ContactShieldSetting.DEFAULT)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Engine start", "Success");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        e.printStackTrace();
                        Log.e("Engine start", "Failure");
                    }
                });


    }

}