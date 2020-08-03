package com.futurewei.contact_shield_demo.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.futurewei.contact_shield_demo.R;

public class ReportMethodChooseActivity extends Activity {

    Button qrCodeButton;
    Button teleTANButton;
    Button cancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification_methods);

        initView();
    }

    void initView(){
        qrCodeButton = (Button) findViewById(R.id.select_qrcode_Button);
        teleTANButton = (Button) findViewById(R.id.select_teleTAN_Button);
        cancelButton = (Button) findViewById(R.id.cancelButton);

        qrCodeButton.setOnClickListener((View v) -> {
                //next page
                Intent intent = new Intent(getApplicationContext(), SubmitViaGuidActivity.class);
                startActivity(intent);
                finish();
        });


        teleTANButton.setOnClickListener((View v) -> {
                //next page
                Intent intent = new Intent(getApplicationContext(), RequestTeletanActivity.class);
                startActivity(intent);
                finish();
        });

        cancelButton.setOnClickListener((View v) -> finish());
    }
}