package com.futurewei.contact_shield_demo.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;

public class upload_periodic_key extends  Thread{

    private static final String TAG = "upload periodic keys";
    public Context context;
    public Handler handler;
    public JSONObject jsonObject;
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    public upload_periodic_key(Context context, Handler handler, JSONObject jsonObject){
        this.context = context;
        this.handler = handler;
        this.jsonObject = jsonObject;
    }

    @Override
    public void run() {
        super.run();

        final Message msg=new Message();
        msg.what=1;

        try{
            jsonObject.put("api_level", Build.VERSION.SDK_INT);
            jsonObject.put("android_version", Build.VERSION.RELEASE);
            jsonObject.put("brand", Build.MANUFACTURER);
            jsonObject.put("model", Build.MODEL);
            jsonObject.put("user_id", Settings.Secure.getString(context.getContentResolver(),
                    Settings.Secure.ANDROID_ID));
        }catch(Exception e){
            e.printStackTrace();
        }


        RequestBody body = RequestBody.create(jsonObject.toString(), JSON);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder()
                .url("https://us-central1-contact-tracing-demo-281120.cloudfunctions.net/uploadPeriodicKeys")
                .post(body)
                .build();

        Call call=client.newCall(request);

        //execute contact_list_child
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG,"on Failure");
                Bundle b =new Bundle();
                b.putInt("response_code",0);
                msg.setData(b);
                handler.sendMessage(msg);
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if(response.isSuccessful()){
                    Log.e(TAG,"response and success");
                    Log.e(TAG, response.body().string());
                    Bundle b =new Bundle();
                    b.putInt("response_code",1);
                    msg.setData(b);
                    handler.sendMessage(msg);
                }else{
                    Log.e(TAG,"response but failed");
                    Bundle b =new Bundle();
                    b.putInt("response_code",2);
                    b.putString("message", response.body().string());
                    msg.setData(b);
                    handler.sendMessage(msg);
                }

            }
        });
    }
}
