package com.futurewei.contact_shield_demo.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


import com.futurewei.contact_shield_demo.R;

public class NotificationsActivity extends Activity {

    Button backToHomeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        initView();
    }

    void initView(){
        backToHomeButton = (Button) findViewById(R.id.backToHomeButton);
        backToHomeButton.setOnClickListener((View v) -> finish());

    }
}