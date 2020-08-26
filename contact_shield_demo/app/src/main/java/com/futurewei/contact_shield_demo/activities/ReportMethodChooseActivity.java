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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.futurewei.contact_shield_demo.R;

/**
 * This Activity is used for user to choose in which way will he upload his periodic keys.
 * If the user is a patient, he can upload periodic keys by either scanning a QR Code or entering the TeleTAN
 */
public class ReportMethodChooseActivity extends Activity {

    Button qrCodeButton;
    Button teleTANButton;
    Button cancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_method_choose);

        initView();
    }

    /**
     * This method initializes the views and sets on click listener for all buttons
     */
    void initView(){
        qrCodeButton = (Button) findViewById(R.id.select_qrcode_Button);
        teleTANButton = (Button) findViewById(R.id.select_teleTAN_Button);
        cancelButton = (Button) findViewById(R.id.cancelButton);

        qrCodeButton.setOnClickListener((View v) -> {
                //next page
                Intent intent = new Intent(getApplicationContext(), SubmitViaGuidActivity.class);
                startActivity(intent);
                finish();
        });


        teleTANButton.setOnClickListener((View v) -> {
                //next page
                Intent intent = new Intent(getApplicationContext(), RequestTeletanActivity.class);
                startActivity(intent);
                finish();
        });

        cancelButton.setOnClickListener((View v) -> finish());
    }
}