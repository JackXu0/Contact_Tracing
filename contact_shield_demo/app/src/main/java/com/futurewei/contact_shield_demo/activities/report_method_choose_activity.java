package com.futurewei.contact_shield_demo.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.futurewei.contact_shield_demo.R;

public class report_method_choose_activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification_methods);

        //TODO::Select QR code
        Button qrCodeButton = (Button) findViewById(R.id.select_qrcode_Button);
        qrCodeButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //next page
                Intent intent = new Intent(getApplicationContext(), submit_via_guid_activity.class);
                startActivity(intent);
                finish();
            }
        });

        Button teleTANButton = (Button) findViewById(R.id.select_teleTAN_Button);
        teleTANButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //next page
                Intent intent = new Intent(getApplicationContext(), request_teletan_activity.class);
                startActivity(intent);
                finish();
            }
        });

        //EventListener for cancel button
        Button cancelButton = (Button) findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                System.out.println("cancel button pressed, go back to home page");
                finish();
            }
        });
    }
}