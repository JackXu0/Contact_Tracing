package com.futurewei.contact_shield_demo.fragments;

import androidx.fragment.app.Fragment;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.futurewei.contact_shield_demo.BackgroundContactCheckingIntentService;
import com.futurewei.contact_shield_demo.R;
import com.futurewei.contact_shield_demo.network.ReportOperation;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.contactshield.ContactShield;
import com.huawei.hms.contactshield.ContactShieldSetting;

import static android.content.Context.MODE_PRIVATE;

public class fragmentSetting extends Fragment {

    private View root;
    SharedPreferences sharedPreferences;
    SwitchMaterial disableAppBtn;
    SwitchMaterial disableNotificationBtn;
    SwitchMaterial disablePkBtn;
    TextView android_id_tv;
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
        android_id_tv = root.findViewById(R.id.android_id);

        disableAppBtn.setChecked(isAppDisabled);
        disableNotificationBtn.setChecked(isNotificationDisabled);
        disablePkBtn.setChecked(isPKUploadDisabled);
        android_id_tv.setText(Settings.Secure.getString(getActivity().getContentResolver(),
                Settings.Secure.ANDROID_ID));

        disableAppBtn.setOnCheckedChangeListener((CompoundButton buttonView, boolean isChecked) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("is_app_disabled", isChecked);
            editor.commit();

            Task<Boolean> isRunningTask = ContactShield.getContactShieldEngine(getContext()).isContactShieldRunning();
            isRunningTask.addOnSuccessListener(aBoolean -> {
                if(!aBoolean){
                    new ReportOperation(getContext(), new Handler(), "set disable app to "+isChecked, false).start();
                }else{
                    new ReportOperation(getContext(), new Handler(), "set disable app to "+isChecked, true).start();
                }
            });
        });

        disableNotificationBtn.setOnCheckedChangeListener((CompoundButton buttonView, boolean isChecked) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("is_notification_disabled", isChecked);
            editor.commit();

            Task<Boolean> isRunningTask = ContactShield.getContactShieldEngine(getContext()).isContactShieldRunning();
            isRunningTask.addOnSuccessListener(aBoolean -> {
                if(!aBoolean){
                    new ReportOperation(getContext(), new Handler(), "set disable notification to "+isChecked, false).start();
                }else{
                    new ReportOperation(getContext(), new Handler(), "set disable notification to "+isChecked, true).start();
                }
            });
        });

        disablePkBtn.setOnCheckedChangeListener((CompoundButton buttonView, boolean isChecked) -> {

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("is_PK_upload_disabled", isChecked);
            editor.commit();

            Task<Boolean> isRunningTask = ContactShield.getContactShieldEngine(getContext()).isContactShieldRunning();
            isRunningTask.addOnSuccessListener(aBoolean -> {
                if(!aBoolean){
                    new ReportOperation(getContext(), new Handler(), "set disable upload pk to "+isChecked, false).start();
                }else{
                    new ReportOperation(getContext(), new Handler(), "set disable upload pk to "+isChecked, true).start();
                }
            });
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

//        task.addOnSuccessListener((Void aVoid) -> Toast.makeText(getContext(), "Data Cleared", Toast.LENGTH_SHORT).show());
    }

    void restarContactShield(){

        ContactShield.getContactShieldEngine(getActivity()).stopContactShield()
                .addOnSuccessListener(aVoid -> {
                    Log.e(TAG, "stopContactShield >> Success");
                    PendingIntent pendingIntent = PendingIntent.getService(getActivity(), 0, new Intent(getActivity(),
                                    BackgroundContactCheckingIntentService.class),
                            PendingIntent.FLAG_UPDATE_CURRENT);


                    ContactShield.getContactShieldEngine(getActivity()).startContactShield(ContactShieldSetting.DEFAULT)
                            .addOnSuccessListener(bVoid -> {
                                Toast.makeText(getContext(), "Data Cleared", Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "startContactShield >> Success");
                                Task<Boolean> isRunningTask = ContactShield.getContactShieldEngine(getContext()).isContactShieldRunning();
                                isRunningTask.addOnSuccessListener(aBoolean -> {
                                    if(!aBoolean){
                                        new ReportOperation(getContext(), new Handler(), "data cleared", false).start();
                                    }else{
                                        new ReportOperation(getContext(), new Handler(), "data cleared", true).start();
                                    }
                                });
                            })
                            .addOnFailureListener(e -> { Log.e(TAG, "startContactShield >> Failure"); });


                });




    }
}
