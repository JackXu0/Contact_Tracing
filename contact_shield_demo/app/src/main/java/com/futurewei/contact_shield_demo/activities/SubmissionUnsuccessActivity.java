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

public class SubmissionUnsuccessActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submission_unsuccess);

        Button retryButton = (Button) findViewById(R.id.retryButton);
        retryButton.setOnClickListener((View v) -> {
            Task<Boolean> isRunningTask = ContactShield.getContactShieldEngine(getApplicationContext()).isContactShieldRunning();
            isRunningTask.addOnSuccessListener(aBoolean -> {
                if(!aBoolean){
                    new ReportOperation(getApplicationContext(), new Handler(), "submission unsuccess prompted", false).start();
                }else{
                    new ReportOperation(getApplicationContext(), new Handler(), "submission unsuccess prompted", true).start();
                }
            });

            Intent intent = new Intent(getApplicationContext(), ReportMethodChooseActivity.class);
            startActivity(intent);
            finish();
        });
    }
}