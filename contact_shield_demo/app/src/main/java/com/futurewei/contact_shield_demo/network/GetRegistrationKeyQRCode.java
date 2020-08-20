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
        super("Get Registration Key QR Code", context, handler, 1, "https://us-central1-contact-shield-demo.cloudfunctions.net/getRegistrationKeyGUID");
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
