package com.futurewei.contact_shield_demo.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.futurewei.contact_shield_demo.R;

public class request_teletan_activity extends Activity {

    String verification_center_phone = "3322078569";
    Activity activity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.request_teletan_activity);
        //TODO:: Call number
        Button callButton = (Button) findViewById(R.id.call_button);


        if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[] {Manifest.permission.CALL_PHONE},
                    940);
        }


        callButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED){
                    String uri = "tel:" + verification_center_phone.trim();
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(uri));
                    startActivity(intent);
                }else{
                    ActivityCompat.requestPermissions(activity, new String[] {Manifest.permission.CALL_PHONE}, 940);
                }

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