/**
 * Copyright © 2020  Futurewei Technologies, Inc. All rights reserved.
 *
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *
 *
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *
 *
 * Unless required by applicable law or agreed to in writing, software
 *
 * distributed under the License is distributed on an "AS IS" BASIS,
 *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the License for the specific language governing permissions and
 *
 * limitations under the License.
 */

package com.futurewei.contact_shield_demo.activities;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.futurewei.contact_shield_demo.R;

/**
 * This Activity is used for user to fetch a TeleTAN
 */
public class RequestTeletanActivity extends Activity {

    String verificationCenterPhone = "3322078569";
    Button callButton;
    Button enterTANButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_teletan);

        initView();

    }

    /**
     * This method initializes the views and sets on click listener for all buttons
     */
    void initView(){

        callButton = (Button) findViewById(R.id.call_button);
        enterTANButton = (Button) findViewById(R.id.enter_TAN_button);

        callButton.setOnClickListener((View v) -> {
                String uri = "tel:" + verificationCenterPhone.trim();
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(uri));
                startActivity(intent);
        });


        enterTANButton.setOnClickListener((View v) -> {
                //next page
                Intent intent = new Intent(getApplicationContext(), SubmitViaTeletanActivity.class);
                startActivity(intent);
                finish();
        });
    }
}