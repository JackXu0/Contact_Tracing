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
 * This network gets the registration key using GUID. GUID is the result after scanning a valid QR code
 */
public class GetRegistrationKeyQRCode extends NetworkTemplate {


    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");


    public GetRegistrationKeyQRCode(Context context, Handler handler, String guid){
        super("Get Registration Key QR Code", context, handler, 1, "https://8md2fn42xg.execute-api.us-east-2.amazonaws.com/getRegistrationKeyGUID");
        this.requestBody = makeRequestBody(guid);

    }

    RequestBody makeRequestBody(String guid){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("guid", guid);
        } catch (JSONException e) {
            Log.e(TAG, e.toString());
        }

        return RequestBody.create(jsonObject.toString(), JSON);
    }

}
