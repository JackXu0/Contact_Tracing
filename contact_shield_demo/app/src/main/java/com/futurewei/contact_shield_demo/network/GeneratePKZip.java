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

public class GeneratePKZip extends NetworkTemplate {

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    public GeneratePKZip(Context context, Handler handler){
        super("Download New", context, handler, 5, "http://35.222.93.177:5000/zip");
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
