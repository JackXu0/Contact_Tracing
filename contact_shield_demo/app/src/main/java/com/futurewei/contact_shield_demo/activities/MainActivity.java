/**
 * Copyright © 2020  Futurewei Technologies, Inc. All rights reserved.
 *
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *
 *
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *
 *
 * Unless required by applicable law or agreed to in writing, software
 *
 * distributed under the License is distributed on an "AS IS" BASIS,
 *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the License for the specific language governing permissions and
 *
 * limitations under the License.
 */

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

/**
 * This is the Main Activity. It basically contains three fragments: Homepage, FAQ page, and the settings page
 */
public class MainActivity extends AppCompatActivity {

    private Fragment fragmentHome;
    private Fragment fragmentFaq;
    private Fragment fragmentSettings;
    private FragmentManager fm;
    private FragmentTransaction ft;
    private int page;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main_activity);

        initView();
    }

    /**
     * This method is used to init the view. It enables switching among three fragments
     *
     */
    void initView(){
        permissonRequest();
        registerPush();

        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();

        fragmentHome = new FragmentHome();
        fragmentFaq = new FragmentFaq();
        fragmentSettings = new fragmentSetting();
        ft.replace(R.id.frame, fragmentHome);
        ft.commit();

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);

        BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
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

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    /**
     * This method is used for requesting permissions if they have not been granted yet
     * Storage permission: used for getting periodic keys from contact shield SDK and put Periodic Keys to contact shield SDK
     * Bluetooth permission: used for broadcasting and scanning anonymous identifiers
     * Internet permission: used for sending network requests
     */
    void permissonRequest(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
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
    }

    /**
     * This method is used to register push
     */
    void registerPush(){
        MainActivity.MyReceiver receiver = new MainActivity.MyReceiver();
        IntentFilter filter=new IntentFilter();
        filter.addAction("com.huawei.codelabpush.ON_NEW_TOKEN");
        MainActivity.this.registerReceiver(receiver,filter);
    }

    /**
     * This class is used for Push kit
     */
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