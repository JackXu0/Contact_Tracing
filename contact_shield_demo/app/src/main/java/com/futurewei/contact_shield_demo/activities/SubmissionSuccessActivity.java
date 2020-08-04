package com.futurewei.contact_shield_demo.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.futurewei.contact_shield_demo.R;

public class SubmissionSuccessActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submission_success);

        Button backToHomeButton = (Button) findViewById(R.id.homeButton);
        backToHomeButton.setOnClickListener((View v) -> finish());
    }
}