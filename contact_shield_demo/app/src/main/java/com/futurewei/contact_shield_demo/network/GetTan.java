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
        super("Get TAN", context, handler, 3, "https://us-central1-contact-shield-demo.cloudfunctions.net/getTAN");
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
