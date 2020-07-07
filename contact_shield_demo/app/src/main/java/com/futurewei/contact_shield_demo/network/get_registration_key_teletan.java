package com.futurewei.contact_shield_demo.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;

public class get_registration_key_teletan extends Thread {

    public Context context;
    public Handler handler;
    public JSONObject jsonObject;
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    SharedPreferences sharedPreferences;

    public get_registration_key_teletan(Context context, Handler handler, JSONObject jsonObject){
        this.context = context;
        this.handler = handler;
        this.jsonObject = jsonObject;
    }

    @Override
    public void run() {
        super.run();

        final Message msg=new Message();
        msg.what=4;

        RequestBody body = RequestBody.create(jsonObject.toString(), JSON);

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://us-central1-contact-tracing-demo-281120.cloudfunctions.net/getRegistrationKeyTELETAN")
                .post(body)
                .build();

        Call call=client.newCall(request);

        //execute contact_list_child
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("Get registration key","on Failure");
                Bundle b =new Bundle();
                b.putInt("response_code",0);
                msg.setData(b);
                handler.sendMessage(msg);
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if(response.isSuccessful()){
                    String registration_key = response.body().string();
                    Log.e("Get registration key","response and success");
                    Log.e("Get registration key", registration_key);

                    sharedPreferences = context.getSharedPreferences("registration_key", MODE_PRIVATE);
                    final SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("value", registration_key);
                    editor.commit();

                    Bundle b =new Bundle();
                    b.putInt("response_code",1);
                    b.putString("registration_key", registration_key);
                    msg.setData(b);
                    handler.sendMessage(msg);
                }else{
                    Log.e("Get registration key","response but failed");
                    Bundle b =new Bundle();
                    b.putInt("response_code",2);
                    msg.setData(b);
                    handler.sendMessage(msg);
                }

            }
        });
    }

}
