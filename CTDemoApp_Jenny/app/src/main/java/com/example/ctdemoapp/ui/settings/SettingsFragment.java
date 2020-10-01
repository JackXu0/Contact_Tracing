package com.example.ctdemoapp.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.ctdemoapp.R;

public class SettingsFragment extends Fragment {

    private SettingsViewModel settingsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        settingsViewModel =
                ViewModelProviders.of(this).get(SettingsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_settings, container, false);

        final RadioButton disableApp = (RadioButton) root.findViewById(R.id.disableAppButton);
        disableApp.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                boolean checked = ((RadioButton)v).isChecked();
                System.out.println("check for disable app is " + checked);
                //TODO: MAKE RADIO BUTTON TOGGLE
//                if(!checked){
//                    disableApp.setChecked(true);
//                }
//                else{
//                    disableApp.setChecked(false);
//                }
            }
        });
        RadioButton disableNotification = (RadioButton) root.findViewById(R.id.disableNotificationsButton);
        disableNotification.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                boolean checked = ((RadioButton)v).isChecked();
                System.out.println("check for notifications is " + checked);
                //TODO: MAKE RADIO BUTTON TOGGLE
            }
        });

        return root;
    }
}