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
import android.content.SharedPreferences;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import okhttp3.MediaType;
import okhttp3.RequestBody;

import static android.content.Context.MODE_PRIVATE;

/**
 * This class requests the server to generate the ZIP file and upload this ZIP file to Google Storage
 *
 */
public class GeneratePKZip extends NetworkTemplate {

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    public GeneratePKZip(Context context, Handler handler){
        super("Download New", context, handler, 5, "http://3.16.177.15:5000/zip");
        this.requestBody = makeRequestBody();

    }

    RequestBody makeRequestBody(){
        final SharedPreferences sharedPreferences = context.getSharedPreferences("last_download_timeStamp",MODE_PRIVATE);
        long lastDownloadTimeStamp=sharedPreferences.getLong("last_download_timeStamp",0);
        String user_id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.e(TAG, "user_id:"+user_id);
        Log.e(TAG, "timestamp:"+lastDownloadTimeStamp);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("timestamp", lastDownloadTimeStamp);
            jsonObject.put("user_id", user_id);
        } catch (JSONException e) {
            Log.e(TAG, e.toString());
        }

        Log.e(TAG, "last download timestamp: "+lastDownloadTimeStamp+"");
        RequestBody formBody = RequestBody.create(jsonObject.toString(), JSON);

        return formBody;
    }
}
