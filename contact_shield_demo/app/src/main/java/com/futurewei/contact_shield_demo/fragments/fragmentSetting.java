package com.futurewei.contact_shield_demo.fragments;

import androidx.fragment.app.Fragment;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.futurewei.contact_shield_demo.R;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.contactshield.ContactShield;

import static android.content.Context.MODE_PRIVATE;

public class fragmentSetting extends Fragment {

    private View root;
    SharedPreferences sharedPreferences;
    SwitchMaterial disableAppBtn;
    SwitchMaterial disableNotificationBtn;
    SwitchMaterial disablePkBtn;
    Button clearDataBtn;

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
        Task<Void> task = ContactShield.getContactShieldEngine(getContext()).clearData();

        task.addOnSuccessListener((Void aVoid) -> Toast.makeText(getContext(), "Data Cleared", Toast.LENGTH_SHORT).show());
    }
}
