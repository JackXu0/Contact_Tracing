package com.futurewei.contact_shield_demo.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.futurewei.contact_shield_demo.R;

public class request_teletan_activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.request_teletan_activity);
        //TODO:: Call number
        Button callButton = (Button) findViewById(R.id.call_button);
        callButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //next page
//                Intent intent = new Intent(getApplicationContext(), VerificationActivity.class);
//                startActivity(intent);
//                finish();
            }
        });

        Button enterTANButton = (Button) findViewById(R.id.enter_TAN_button);
        enterTANButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //next page
                Intent intent = new Intent(getApplicationContext(), submit_via_teletan_activity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}