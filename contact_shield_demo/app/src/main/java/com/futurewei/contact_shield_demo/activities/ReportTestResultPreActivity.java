package com.futurewei.contact_shield_demo.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.futurewei.contact_shield_demo.R;

/**
 * This Activity appears when a user chooses to report his status
 * It tells user what will happen next and allows the user to choose whether he want to continue.
 */
public class ReportTestResultPreActivity extends Activity {

    Button continueButton;
    Button cancelButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_test_result);

        initView();
    }

    /**
     * This method initializes the views and sets on click listener for all buttons
     */
    void initView(){

        continueButton = (Button) findViewById(R.id.continueButton);
        cancelButton = (Button) findViewById(R.id.cancelButton);

        continueButton.setOnClickListener((View v) -> {
                Intent intent = new Intent(getApplicationContext(), ReportMethodChooseActivity.class);
                startActivity(intent);
                finish();
        });

        cancelButton.setOnClickListener((View v) -> finish());
    }
}