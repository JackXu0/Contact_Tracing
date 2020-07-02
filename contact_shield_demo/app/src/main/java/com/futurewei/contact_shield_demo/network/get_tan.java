package com.futurewei.contact_shield_demo.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.json.JSONException;
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

public class get_tan extends Thread{
    public Context context;
    public Handler handler;
    public JSONObject jsonObject;
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    SharedPreferences sharedPreferences;

    public get_tan(Context context, Handler handler, JSONObject jsonObject){
        this.context = context;
        this.handler = handler;
        this.jsonObject = jsonObject;
    }

    @Override
    public void run() {
        super.run();

        final Message msg=new Message();
        msg.what=5;

        RequestBody body = RequestBody.create(jsonObject.toString(), JSON);

        try {
            Log.e("rkey thread", jsonObject.getString("registration_key"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://us-central1-contact-tracing-demo-281120.cloudfunctions.net/getTAN")
                .post(body)
                .build();

        Call call=client.newCall(request);

        //execute contact_list_child
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("Get TAN","on Failure");
                Bundle b =new Bundle();
                b.putInt("response_code",0);
                msg.setData(b);
                handler.sendMessage(msg);
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if(response.isSuccessful()){
                    String tan = response.body().string();
                    Log.e("Get TAN","response and success");
                    Log.e("Get TAN", tan);

                    sharedPreferences = context.getSharedPreferences("tan", MODE_PRIVATE);
                    final SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("value", tan);
                    editor.commit();

                    Bundle b =new Bundle();
                    b.putInt("response_code",1);
                    b.putString("tan", tan);
                    msg.setData(b);
                    handler.sendMessage(msg);
                }else{
                    Log.e("Get TAN","response but failed");
                    Log.e("Get TAN", response.body().string());
                    Bundle b =new Bundle();
                    b.putInt("response_code",2);
                    msg.setData(b);
                    handler.sendMessage(msg);
                }

            }
        });
    }
}
