package com.futurewei.contact_shield_demo.network;

import android.content.Context;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class ReportOperation extends NetworkTemplate{

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    public ReportOperation(Context context, Handler handler, String operation, boolean is_cskit_running){

//        super("Report Operation", context, handler, 8, "https://us-central1-contact-shield-demo.cloudfunctions.net/reportOperation" );
        super("Report Operation", context, handler, 8, "https://0whq2imdbc.execute-api.us-east-2.amazonaws.com/reportOperations" );
        this.requestBody = makeRequestBody(operation, is_cskit_running);
    }

    RequestBody makeRequestBody(String operation, boolean is_cskit_running){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("operation", operation);
            jsonObject.put("user_id", Settings.Secure.getString(context.getContentResolver(),
                    Settings.Secure.ANDROID_ID));
            jsonObject.put("is_cskit_running", is_cskit_running);
        } catch (JSONException e) {
            Log.e(TAG, e.toString());
        }

        return RequestBody.create(jsonObject.toString(), JSON);
    }
}

