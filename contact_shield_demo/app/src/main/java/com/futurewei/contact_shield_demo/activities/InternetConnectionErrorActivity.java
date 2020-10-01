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
 * This Activity generally alerts user that the internet connection is off.
 * It is trigger when the onFailure callback of a network request is executed
 *
 */
public class InternetConnectionErrorActivity extends Activity {

    Button retryButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_internet_connection_error);

        initView();
    }

    /**
     * This method initialize the view and set the on click listener for the retry button
     * Once the user has clicked retry button, he will go back to the MainActivity
     *
     */
    void initView(){
        retryButton = (Button) findViewById(R.id.retryButton);
        retryButton.setOnClickListener((View v) -> {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
        });
    }
}