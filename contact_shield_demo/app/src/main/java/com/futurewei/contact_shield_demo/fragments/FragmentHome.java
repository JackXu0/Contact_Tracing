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
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.futurewei.contact_shield_demo.BackgroundContactCheckingIntentService;
import com.futurewei.contact_shield_demo.R;
import com.futurewei.contact_shield_demo.activities.ReportTestResultPreActivity;
import com.futurewei.contact_shield_demo.handlers.DownloadHandler;
import com.futurewei.contact_shield_demo.network.GeneratePKZip;
import com.futurewei.contact_shield_demo.network.GetTan;
import com.google.android.material.card.MaterialCardView;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.contactshield.ContactShield;
import com.huawei.hms.contactshield.ContactShieldSetting;

import java.util.*;


import static android.content.Context.MODE_PRIVATE;

/**
 * This is the fragment for the Home page
 */
public class FragmentHome extends Fragment {

    Context context;
    private View root;
    MaterialCardView myStatusCard;
    ConstraintLayout heading;
    TextView scanningTv;
    TextView headlingTv;
    RadioGroup radioGroup;
    RadioButton radioPositiveBtn;
    RadioButton radioNegtiveBtn;
    Button reportButton;
    Button refresh_btn;
    public static TextView numberOfHitsTv;
    public static TextView riskLevelTv;
    Handler handler;

    Map<Integer, String> riskLevelMap;
    SharedPreferences sharedPreferences;

    private static final String TAG = "fragment home";
    private static final String SETTINGS = "settings";
    private static final String IS_APP_DISABLED = "is_app_disabled";


    public static final int UPLOAD_INTERVAL_IN_DAYS = 7;
    public static final boolean FLEXIBLE_MY_STATUS_ENABLED = false;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        this.handler = new DownloadHandler(context, TAG);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        this.context = getActivity();

        root= inflater.inflate(R.layout.fragment_home, container, false);

        initRiskLevelMap();

        initView(root);

        return root;

    }

    @Override
    public void onResume() {
        super.onResume();
        refreshDashboard();
        refreshIsScanning();
    }

    void initView(View root){
        myStatusCard = root.findViewById(R.id.card1);
        heading = root.findViewById(R.id.Heading);
        headlingTv = root.findViewById(R.id.heading_tv);
        refresh_btn = (Button) root.findViewById(R.id.refreshButton);
        radioGroup = (RadioGroup) root.findViewById(R.id.radioGroup);
        radioPositiveBtn = (RadioButton) root.findViewById(R.id.radioPositive);
        radioNegtiveBtn = root.findViewById(R.id.radioNegative);
        reportButton = (Button) root.findViewById(R.id.reportResults);
        numberOfHitsTv = root.findViewById(R.id.number_of_hits_tv);
        riskLevelTv = root.findViewById(R.id.risk_level_tv);
        scanningTv = root.findViewById(R.id.scanning_tv);

        //init dashboard
        sharedPreferences = context.getSharedPreferences("my staus choice", MODE_PRIVATE);
        boolean choice = sharedPreferences.getBoolean("choice", false);
        radioNegtiveBtn.setChecked(!choice);
        radioPositiveBtn.setChecked(choice);

        if(choice)
            reportButton.setVisibility(View.VISIBLE);
        else
            reportButton.setVisibility(View.INVISIBLE);

        // Check is manually upload is needed
        if(FLEXIBLE_MY_STATUS_ENABLED && !checkIfAllowsManualUpload()){

            // disable two radio buttons
            radioNegtiveBtn.setEnabled(false);
            radioPositiveBtn.setEnabled(false);

            //set positive button checked
            radioPositiveBtn.setChecked(true);
            radioNegtiveBtn.setChecked(false);

            // gray background for my status panel and change heading text
            myStatusCard.setBackgroundColor(getResources().getColor(R.color.disable_gray));
            headlingTv.setText("Result Reported");
            reportButton.setVisibility(View.GONE);

            // alert user when click my status panel
            myStatusCard.setOnClickListener((View v) -> Toast.makeText(context, "Your result has been reported", Toast.LENGTH_SHORT).show());
        }

        //EventListener for radio button
        radioGroup.setOnCheckedChangeListener((RadioGroup group, int checkedId) -> {
                sharedPreferences = context.getSharedPreferences("my staus choice", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if(checkedId == radioPositiveBtn.getId()){
                    reportButton.setVisibility(View.VISIBLE);
                    editor.putBoolean("choice", true);
                } else{
                    reportButton.setVisibility(View.GONE);
                    editor.putBoolean("choice", false);
                }
                editor.commit();
        });

        //EventListener for report your results button
        reportButton.setOnClickListener((View v) -> {
            Task<Boolean> isRunningTask = ContactShield.getContactShieldEngine(context).isContactShieldRunning();
            isRunningTask.addOnSuccessListener(aBoolean -> {
                if(aBoolean){
                    Intent intent = new Intent(context, ReportTestResultPreActivity.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(context, "Please enable the app before reporting", Toast.LENGTH_SHORT).show();
                }
            });
            isRunningTask.addOnFailureListener(e -> {
                Toast.makeText(context, "Please enable the app before reporting", Toast.LENGTH_SHORT).show();
            });
        });

        //EventListener for refresh button
        refresh_btn.setOnClickListener((View v) -> new GeneratePKZip(context, handler).start());


    }


    void initRiskLevelMap(){
        riskLevelMap = new HashMap<>();
        riskLevelMap.put(0, "NO RISK");
        riskLevelMap.put(1, "LOWEST");
        riskLevelMap.put(2, "LOW");
        riskLevelMap.put(3, "MEDIUM LOW");
        riskLevelMap.put(4, "MEDIUM");
        riskLevelMap.put(5, "MEDIUM_HIGH");
        riskLevelMap.put(6, "HIGH");
        riskLevelMap.put(7, "EXTRA HIGH");
        riskLevelMap.put(8, "HIGHEST");
    }

    boolean checkIfAllowsManualUpload(){
        sharedPreferences = context.getSharedPreferences("upload_pk_history", MODE_PRIVATE);
        String registrationKey = sharedPreferences.getString("registration_key","");
        long latest_uploading_time = sharedPreferences.getLong("timestamp", 0);
        sharedPreferences = getContext().getSharedPreferences(SETTINGS, MODE_PRIVATE);
        boolean isPKUploadDisabled = sharedPreferences.getBoolean("is_PK_upload_disabled", false);

        // if registration_key is missing or corrupted, or it has been more than one interval (7 days) since last manual upload, needs upload manually again.
        if(registrationKey.length() != 32 || latest_uploading_time < ( System.currentTimeMillis() - UPLOAD_INTERVAL_IN_DAYS*24*6*600000)){
            return true;
        }
        //If registration_key exists, but has not uploaded in 24 hours, needs one auto upload
        else if(registrationKey.length() == 32 || latest_uploading_time < ( System.currentTimeMillis() - 24 * 6 * 600000)){
            //check if uploading PK has been disabled by the user
            if(!isPKUploadDisabled)
                uploadPKAutomatically(registrationKey);
            return false;
        }else{
            //If registration_key exists, and has auto uploaded within 24 hours, no need for further operations
            return false;
        }
    }

    void uploadPKAutomatically(String registrationKey){
        new GetTan(context, handler, registrationKey).start();
    }

    void refreshDashboard(){
        //refresh my status choices
        sharedPreferences = context.getSharedPreferences("dashboard_info",MODE_PRIVATE);
        numberOfHitsTv.setText(""+sharedPreferences.getInt("number_of_hits",0));
        riskLevelTv.setText(riskLevelMap.get(sharedPreferences.getInt("risk_level", 0)));

    }

    void refreshIsScanning(){
        sharedPreferences = getContext().getSharedPreferences(SETTINGS,MODE_PRIVATE);
        boolean isAppDisabled = sharedPreferences.getBoolean(IS_APP_DISABLED, false);

        if(!isAppDisabled){
            engineStartPreCheck();
            Log.e(TAG, "contact shielding should be running");
        }else{
            Task<Void> stopContactShield = ContactShield.getContactShieldEngine(context).stopContactShield();
            stopContactShield.addOnSuccessListener( (Void v) -> Log.e(TAG, "stop contact shield >> Succeeded"));
            stopContactShield.addOnFailureListener( (Exception e) -> Log.e(TAG, "stop contact shield "+ e.getMessage()));
            Log.e(TAG, "contact shielding should not be running");
        }
    }

    void engineStartPreCheck(){
        Log.d(TAG, "engine_start_pre_check");
        Task<Boolean> isRunningTask = ContactShield.getContactShieldEngine(context).isContactShieldRunning();
        isRunningTask.addOnSuccessListener(aBoolean -> {
            if(!aBoolean){
                engineStart();
                Log.e(TAG, "isContactShieldRunning >> NO");
            }else{
                scanningTv.setVisibility(View.VISIBLE);
                Log.e(TAG, "isContactShieldRunning >> YES");
            }
        });
    }

    void engineStart(){
        Log.d(TAG, "engine_start");
        PendingIntent pendingIntent = PendingIntent.getService(getActivity(), 0, new Intent(getActivity(),
                        BackgroundContactCheckingIntentService.class),
                PendingIntent.FLAG_UPDATE_CURRENT);


        ContactShield.getContactShieldEngine(context).startContactShield(pendingIntent, ContactShieldSetting.DEFAULT)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "startContactShield >> Success");
                    scanningTv.setVisibility(View.VISIBLE);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "startContactShield >> Failure");
                    scanningTv.setVisibility(View.INVISIBLE);
                });
    }

}
