package com.futurewei.contact_shield_demo.activities;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import com.futurewei.contact_shield_demo.R;
import com.futurewei.contact_shield_demo.network.ReportOperation;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.contactshield.ContactShield;

public class SubmissionSuccessActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submission_success);

        Button backToHomeButton = (Button) findViewById(R.id.homeButton);
        backToHomeButton.setOnClickListener((View v) -> {
            Task<Boolean> isRunningTask = ContactShield.getContactShieldEngine(getApplicationContext()).isContactShieldRunning();
            isRunningTask.addOnSuccessListener(aBoolean -> {
                if(!aBoolean){
                    new ReportOperation(getApplicationContext(), new Handler(), "submission success prompted", false).start();
                }else{
                    new ReportOperation(getApplicationContext(), new Handler(), "submission success prompted", true).start();
                }
            });
            finish();
        });
    }
}