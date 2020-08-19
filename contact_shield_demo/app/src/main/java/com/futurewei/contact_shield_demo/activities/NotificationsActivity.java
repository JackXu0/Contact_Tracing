package com.futurewei.contact_shield_demo.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


import com.futurewei.contact_shield_demo.R;

/**
 * This Activity appears after use click on the notification, the notification is prompted out when the risk level is greater than medium.
 * It basically tells user that he may be in danger, please practice self quarantine and go to see a doctor if not feeling fine.
 */
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
        backToHomeButton.setOnClickListener((View v) -> {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        });

    }
}