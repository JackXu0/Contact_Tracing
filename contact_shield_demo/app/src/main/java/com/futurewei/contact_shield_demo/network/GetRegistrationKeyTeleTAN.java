package com.futurewei.contact_shield_demo.network;

import android.content.Context;
import android.os.Handler;

import org.json.JSONObject;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class GetRegistrationKeyTeleTAN extends NetworkTemplate {

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    public GetRegistrationKeyTeleTAN(Context context, Handler handler, JSONObject jsonObject){
//        super("Get Registration Key TELETAN", context, handler, 2, "https://us-central1-contact-tracing-demo-281120.cloudfunctions.net/getRegistrationKeyTELETAN");
        super("Get Registration Key TELETAN", context, handler, 2, "https://us-central1-contact-shield-demo.cloudfunctions.net/getRegistrationKeyTELETAN");

        this.requestBody = makeRequestBody(jsonObject);
    }

    RequestBody makeRequestBody(JSONObject jsonObject){
        return RequestBody.create(jsonObject.toString(), JSON);
    }

}
