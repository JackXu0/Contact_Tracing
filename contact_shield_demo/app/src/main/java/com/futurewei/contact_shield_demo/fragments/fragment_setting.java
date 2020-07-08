package com.futurewei.contact_shield_demo.fragments;

import android.app.Fragment;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.futurewei.contact_shield_demo.BackgroundContactCheckingIntentService;
import com.futurewei.contact_shield_demo.R;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.contactshield.ContactShield;
import com.huawei.hms.contactshield.ContactShieldSetting;
import com.huawei.hms.contactshield.PeriodicKey;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class fragment_setting extends Fragment {

    private View root;
    SharedPreferences sharedPreferences;
    SwitchMaterial disable_app_btn;
    SwitchMaterial disable_notification_btn;
    SwitchMaterial disable_pk_btn;
    Button clear_data_btn;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root= inflater.inflate(R.layout.settings_fragment, container, false);

        initView(root);

        return root;
    }

    void initView(View root){
        sharedPreferences = getContext().getSharedPreferences("settings", MODE_PRIVATE);
        boolean is_app_disabled = sharedPreferences.getBoolean("is_app_disabled", false);
        boolean is_notification_disabled = sharedPreferences.getBoolean("is_notification_disabled", false);
        boolean is_PK_upload_disabled = sharedPreferences.getBoolean("is_PK_upload_disabled", false);


        disable_app_btn = root.findViewById(R.id.disableAppButton);
        disable_notification_btn = root.findViewById(R.id.disableNotificationsButton);
        disable_pk_btn = root.findViewById(R.id.disablePKUploadButton);
        clear_data_btn = root.findViewById(R.id.clearDataButton);

        disable_app_btn.setChecked(is_app_disabled);
        disable_notification_btn.setChecked(is_notification_disabled);
        disable_pk_btn.setChecked(is_PK_upload_disabled);

        disable_app_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if (!disable_app_btn.isSelected()) {
                    disable_app_btn.setChecked(true);
                    disable_app_btn.setSelected(true);
                    editor.putBoolean("is_app_disabled", true);
                    engine_stop_pre_check();
                } else {
                    disable_app_btn.setChecked(false);
                    disable_app_btn.setSelected(false);
                    editor.putBoolean("is_app_disabled", false);
                    engine_start_pre_check();
                }
                editor.commit();
            }
        });

        disable_notification_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if (!disable_notification_btn.isSelected()) {
                    disable_notification_btn.setChecked(true);
                    disable_notification_btn.setSelected(true);
                    editor.putBoolean("is_notification_disabled", true);
                } else {
                    disable_notification_btn.setChecked(false);
                    disable_notification_btn.setSelected(false);
                    editor.putBoolean("is_notification_disabled", false);
                }
                editor.commit();
            }
        });

        clear_data_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clear_data();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    void engine_start_pre_check(){
        Task<Boolean> isRunningTask = ContactShield.getContactShieldEngine(getContext()).isContactShieldRunning();
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

    @RequiresApi(api = Build.VERSION_CODES.M)
    void engine_stop_pre_check(){
        Task<Boolean> isRunningTask = ContactShield.getContactShieldEngine(getContext()).isContactShieldRunning();
        isRunningTask.addOnSuccessListener(new OnSuccessListener<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean) {
                if(!aBoolean){
                    Log.e("Is running", "NO");
                }else{
                    engine_stop();
                    Log.e("Is running", "YES");
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    void engine_start(){
        PendingIntent pendingIntent = PendingIntent.getService(getContext(), 0, new Intent(getContext(), BackgroundContactCheckingIntentService.class),
                PendingIntent.FLAG_UPDATE_CURRENT);


        Task<Void> engine_start_task = ContactShield.getContactShieldEngine(getContext()).startContactShield(pendingIntent, ContactShieldSetting.DEFAULT)
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

    @RequiresApi(api = Build.VERSION_CODES.M)
    void engine_stop(){
        Task<Void> engine_start_task = ContactShield.getContactShieldEngine(getContext()).stopContactShield()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Engine stop", "Success");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        e.printStackTrace();
                        Log.e("Engine stop", "Failure");
                    }
                });
    }

    void clear_data(){
        Task<Void> task = ContactShield.getContactShieldEngine(getContext()).clearData();

        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.e("clear data","success");
                Toast.makeText(getContext(), "Data Cleared", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
