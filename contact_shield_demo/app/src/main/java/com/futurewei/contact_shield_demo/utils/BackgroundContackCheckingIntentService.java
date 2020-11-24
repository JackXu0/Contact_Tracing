/*
 * Copyright 2020. Huawei Technologies Co., Ltd. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 
package com.futurewei.contact_shield_demo.utils;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.huawei.hms.contactshield.ContactShield;
import com.huawei.hms.contactshield.ContactShieldCallback;
import com.huawei.hms.contactshield.ContactShieldEngine;

public class BackgroundContackCheckingIntentService extends IntentService {
    private static final String TAG = "ContactShield_BackgroundContackCheckingIntentService";
    private ContactShieldEngine contactEngine;

    public BackgroundContackCheckingIntentService() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        contactEngine = ContactShield.getContactShieldEngine(BackgroundContackCheckingIntentService.this);
        Log.d(TAG, "BackgroundContackCheckingIntentService onCreate");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "BackgroundContackCheckingIntentService onDestroy");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            contactEngine.handleIntent(intent,
                    new ContactShieldCallback() {
                        @Override
                        public void onHasContact(String s) {
                          Log.d(TAG, "onHasContact");
                        }
                        @Override
                        public void onNoContact(String s) {
                            Log.d(TAG, "onNoContact");
                        }
                    });
        }
    }
}