package com.futurewei.contact_shield_demo.activities;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.futurewei.contact_shield_demo.R;

public class RequestTeletanActivity extends Activity {

    String verificationCenterPhone = "3322078569";
    Activity activity = this;
    Button callButton;
    Button enterTANButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.request_teletan_activity);

        //TODO:: Call number

        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[] {Manifest.permission.CALL_PHONE},
                    940);
        }

        initView();

    }

    void initView(){

        callButton = (Button) findViewById(R.id.call_button);
        enterTANButton = (Button) findViewById(R.id.enter_TAN_button);

        callButton.setOnClickListener((View v) -> {

            if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED){
                String uri = "tel:" + verificationCenterPhone.trim();
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(uri));
                startActivity(intent);
            }else{
                ActivityCompat.requestPermissions(activity, new String[] {Manifest.permission.CALL_PHONE}, 940);
            }
        });


        enterTANButton.setOnClickListener((View v) -> {
                //next page
                Intent intent = new Intent(getApplicationContext(), SubmitViaTeletanActivity.class);
                startActivity(intent);
                finish();
        });
    }
}