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

import org.json.JSONObject;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * This network request gets the registration key using TeleTAN
 */
public class GetRegistrationKeyTeleTAN extends NetworkTemplate {

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    public GetRegistrationKeyTeleTAN(Context context, Handler handler, JSONObject jsonObject){
        super("Get Registration Key TELETAN", context, handler, 2, "https://vc7nfpujef.execute-api.us-east-2.amazonaws.com/getRegistrationKeyTELETAN");
        this.requestBody = makeRequestBody(jsonObject);
    }

    RequestBody makeRequestBody(JSONObject jsonObject){
        return RequestBody.create(jsonObject.toString(), JSON);
    }

}
