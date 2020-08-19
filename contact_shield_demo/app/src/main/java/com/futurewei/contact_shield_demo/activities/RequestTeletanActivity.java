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

/**
 * This Activity is used for user to fetch a TeleTAN
 */
public class RequestTeletanActivity extends Activity {

    String verificationCenterPhone = "2134258730";
    Activity activity = this;
    Button callButton;
    Button enterTANButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_teletan);

        requestPermission();
        initView();

    }

    /**
     * This method request the permission for phone call. This permission will be used to call the health authority so that users can get a TeleTAN
     */
    void requestPermission(){
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[] {Manifest.permission.CALL_PHONE},
                    940);
        }
    }

    /**
     * This method initializes the views and sets on click listener for all buttons
     */
    void initView(){

        callButton = (Button) findViewById(R.id.call_button);
        enterTANButton = (Button) findViewById(R.id.enter_TAN_button);

        callButton.setOnClickListener((View v) -> {

                if (ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED){
                    String uri = "tel:" + verificationCenterPhone.trim();
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(uri));
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