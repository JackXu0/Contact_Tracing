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

package com.futurewei.contact_shield_demo.network;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * This network gets the TAN using the registration key
 */
public class GetTan extends NetworkTemplate {

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    public GetTan(Context context, Handler handler, String registration_key){
        super("Get TAN", context, handler, 3, "https://k14s6lpjj8.execute-api.us-east-2.amazonaws.com/getTAN");
        this.requestBody = makeRequestBody(registration_key);
    }

    RequestBody makeRequestBody(String registration_key){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("registration_key", registration_key);
        } catch (JSONException e) {
            Log.e(TAG, e.toString());
        }

        return RequestBody.create(jsonObject.toString(), JSON);
    }
}
