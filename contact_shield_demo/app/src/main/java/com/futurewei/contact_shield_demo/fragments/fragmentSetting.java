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

package com.futurewei.contact_shield_demo.fragments;

import androidx.fragment.app.Fragment;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.futurewei.contact_shield_demo.BackgroundContactCheckingIntentService;
import com.futurewei.contact_shield_demo.R;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.contactshield.ContactShield;
import com.huawei.hms.contactshield.ContactShieldSetting;

import static android.content.Context.MODE_PRIVATE;

/**
 * This is the fragment for the settings page
 */
public class fragmentSetting extends Fragment {

    private View root;
    SharedPreferences sharedPreferences;
    SwitchMaterial disableAppBtn;
    SwitchMaterial disableNotificationBtn;
    SwitchMaterial disablePkBtn;
    Button clearDataBtn;
    String TAG = "fragment settings";

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root= inflater.inflate(R.layout.fragment_settings, container, false);

        initView(root);

        return root;
    }

    void initView(View root){
        sharedPreferences = getContext().getSharedPreferences("settings", MODE_PRIVATE);
        boolean isAppDisabled = sharedPreferences.getBoolean("is_app_disabled", false);
        boolean isNotificationDisabled = sharedPreferences.getBoolean("is_notification_disabled", false);
        boolean isPKUploadDisabled = sharedPreferences.getBoolean("is_PK_upload_disabled", false);


        disableAppBtn = root.findViewById(R.id.disableAppButton);
        disableNotificationBtn = root.findViewById(R.id.disableNotificationsButton);
        disablePkBtn = root.findViewById(R.id.disablePKUploadButton);
        clearDataBtn = root.findViewById(R.id.clearDataButton);

        disableAppBtn.setChecked(isAppDisabled);
        disableNotificationBtn.setChecked(isNotificationDisabled);
        disablePkBtn.setChecked(isPKUploadDisabled);

        disableAppBtn.setOnCheckedChangeListener((CompoundButton buttonView, boolean isChecked) -> {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("is_app_disabled", isChecked);
                editor.commit();
        });

        disableNotificationBtn.setOnCheckedChangeListener((CompoundButton buttonView, boolean isChecked) -> {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("is_notification_disabled", isChecked);
                editor.commit();
        });

        disablePkBtn.setOnCheckedChangeListener((CompoundButton buttonView, boolean isChecked) -> {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("is_PK_upload_disabled", isChecked);
                editor.commit();
        });

        clearDataBtn.setOnClickListener((View v) -> clearData());
    }



    void clearData(){
        Task<Void> task = ContactShield.getContactShieldEngine(getActivity()).clearData();

        restarContactShield();

        sharedPreferences = getActivity().getSharedPreferences("dashboard_info",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("number_of_hits", 0);
        editor.putInt("risk_level", 0);
        editor.commit();

    }

    void restarContactShield(){

        ContactShield.getContactShieldEngine(getActivity()).stopContactShield()
                .addOnSuccessListener(aVoid -> {
                    Log.e(TAG, "stopContactShield >> Success");
                    PendingIntent pendingIntent = PendingIntent.getService(getActivity(), 0, new Intent(getActivity(),
                                    BackgroundContactCheckingIntentService.class),
                            PendingIntent.FLAG_UPDATE_CURRENT);


                    ContactShield.getContactShieldEngine(getActivity()).startContactShield(pendingIntent, ContactShieldSetting.DEFAULT)
                            .addOnSuccessListener(bVoid -> {
                                Toast.makeText(getContext(), "Data Cleared", Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "startContactShield >> Success");
                            })
                            .addOnFailureListener(e -> { Log.e(TAG, "startContactShield >> Failure"); });
                });




    }
}
