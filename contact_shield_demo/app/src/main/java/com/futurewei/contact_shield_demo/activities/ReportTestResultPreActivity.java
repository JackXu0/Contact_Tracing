package com.futurewei.contact_shield_demo.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.futurewei.contact_shield_demo.R;

public class ReportTestResultPreActivity extends Activity {

    Button continueButton;
    Button cancelButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_test_result);

        initView();
    }

    void initView(){

        continueButton = (Button) findViewById(R.id.continueButton);
        cancelButton = (Button) findViewById(R.id.cancelButton);

        //EventListener for continue button
        continueButton.setOnClickListener((View v) -> {
                //next page
                Intent intent = new Intent(getApplicationContext(), ReportMethodChooseActivity.class);
                startActivity(intent);
                finish();
        });

        //EventListener for cancel button
        cancelButton.setOnClickListener((View v) -> finish());
    }
}