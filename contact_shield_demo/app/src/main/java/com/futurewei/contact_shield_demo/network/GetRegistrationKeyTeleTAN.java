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
