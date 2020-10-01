package com.example.ctdemoapp.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.ctdemoapp.R;
import com.example.ctdemoapp.ReportTestResultActivity;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    View root = null;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        root = inflater.inflate(R.layout.fragment_home, container, false);

        //EventListener for Notification button
        Button notifications = (Button) root.findViewById(R.id.refreshButton);
        notifications.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                System.out.println("refresh button pressed");
            }
        });

        //EventListener for radio button
        RadioGroup radioGroup = (RadioGroup) root.findViewById(R.id.radioGroup);
        final RadioButton positiveButton = (RadioButton) root.findViewById(R.id.radioPositive);
        final Button reportButton = (Button) root.findViewById(R.id.reportResults);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                System.out.println("checkedID is " + checkedId);
                if(checkedId == positiveButton.getId()){
                    reportButton.setTextColor(0xff000000);
                    reportButton.setEnabled(true);
                } else{
                    reportButton.setTextColor(0x00000000);
                    reportButton.setEnabled(false);
                }
            }
        });

        //EventListener for report your results button
        reportButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                System.out.println("reportButton pressed");
                Intent intent = new Intent(getContext(), ReportTestResultActivity.class);
                startActivity(intent);
            }
        });
        return root;
    }
}