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
 * This Activity appear when user clicks on a Push Notification.
 * Push Notification will be broadcast to everyone when the health authority wants to announce an emergency alert.
 */
public class PushNotificationsActivity extends Activity {

    Button backToHomeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push_notifications);

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