package com.futurewei.contact_shield_demo.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import com.futurewei.contact_shield_demo.R;
import com.futurewei.contact_shield_demo.network.ReportOperation;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.contactshield.ContactShield;

public class ReportMethodChooseActivity extends Activity {

    Button qrCodeButton;
    Button teleTANButton;
    Button cancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_method_choose);

        initView();
    }

    void initView(){
        qrCodeButton = (Button) findViewById(R.id.select_qrcode_Button);
        teleTANButton = (Button) findViewById(R.id.select_teleTAN_Button);
        cancelButton = (Button) findViewById(R.id.cancelButton);

        qrCodeButton.setOnClickListener((View v) -> {

            Task<Boolean> isRunningTask = ContactShield.getContactShieldEngine(getApplicationContext()).isContactShieldRunning();
            isRunningTask.addOnSuccessListener(aBoolean -> {
                if(!aBoolean){
                    new ReportOperation(getApplicationContext(), new Handler(), "choose submit via qrcode", false).start();
                }else{
                    new ReportOperation(getApplicationContext(), new Handler(), "choose submit via qrcode", true).start();
                }
            });

            //next page
            Intent intent = new Intent(getApplicationContext(), SubmitViaGuidActivity.class);
            startActivity(intent);
            finish();
        });


        teleTANButton.setOnClickListener((View v) -> {

            Task<Boolean> isRunningTask = ContactShield.getContactShieldEngine(getApplicationContext()).isContactShieldRunning();
            isRunningTask.addOnSuccessListener(aBoolean -> {
                if(!aBoolean){
                    new ReportOperation(getApplicationContext(), new Handler(), "choose submit via teletan", false).start();
                }else{
                    new ReportOperation(getApplicationContext(), new Handler(), "choose submit via teletan", true).start();
                }
            });

            //next page
            Intent intent = new Intent(getApplicationContext(), RequestTeletanActivity.class);
            startActivity(intent);
            finish();
        });

        cancelButton.setOnClickListener((View v) -> finish());
    }
}