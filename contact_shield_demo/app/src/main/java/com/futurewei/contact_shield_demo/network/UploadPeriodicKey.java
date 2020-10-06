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
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;

import com.futurewei.contact_shield_demo.utils.H2GUtils;
import com.huawei.hms.contactshield.PeriodicKey;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.List;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * This class is used to upload periodic keys
 * This class takes two parameters: a list of periodic keys and a valid TAN
 */
public class UploadPeriodicKey extends NetworkTemplate {

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    public UploadPeriodicKey(Context context, Handler handler, List<PeriodicKey> periodicKeyList, String tan){
        super("Upload Periodic Keys", context, handler, 4, "https://c4erz3jwr3.execute-api.us-east-2.amazonaws.com/uploadPeriodicKeys");
        this.requestBody = makeRequestBody(periodicKeyList, tan);
    }

    RequestBody makeRequestBody(List<PeriodicKey> periodicKeyList, String tan){

        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        try {

            for(PeriodicKey periodicKey : periodicKeyList){
                JSONObject jo = new JSONObject();
                jo.put("pk", extractPkString(periodicKey.toString()));
                jo.put("valid_time", (int) (periodicKey.getPeriodicKeyValidTime()));
                jo.put("life_time", (int) periodicKey.getPeriodicKeyLifeTime());
                jo.put("risk_level", 2);
                jo.put("gms_key", H2GUtils.getGmsKey(periodicKey.getContent()));
                jsonArray.put(jo);
            }

            jsonObject.put("periodic_keys", jsonArray);
            jsonObject.put("tan", tan);
            jsonObject.put("api_level", Build.VERSION.SDK_INT);
            jsonObject.put("android_version", Build.VERSION.RELEASE);
            jsonObject.put("brand", Build.MANUFACTURER);
            jsonObject.put("model", Build.MODEL);
            jsonObject.put("user_id", Settings.Secure.getString(context.getContentResolver(),
                    Settings.Secure.ANDROID_ID));
        }catch(Exception e){
            Log.e(TAG, e.toString());
        }

        return RequestBody.create(jsonObject.toString(), JSON);
    }

    String extractPkString(String raw){
        int s = raw.indexOf('[');
        int e = raw.indexOf(']');
        return raw.substring(s+1,e).replace(" ","");
    }
}
