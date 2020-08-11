package com.futurewei.contact_shield_demo.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.futurewei.contact_shield_demo.R;

public class InternetConnectionErrorActivity extends Activity {

    Button retryButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_internet_connection_error);

        initView();
    }

    void initView(){
        retryButton = (Button) findViewById(R.id.retryButton);
        retryButton.setOnClickListener((View v) -> {
                Intent intent = new Intent(getApplicationContext(), NewMainActivity.class);
                startActivity(intent);
                finish();
        });
    }
}