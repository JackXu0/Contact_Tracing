package com.futurewei.contact_shield_demo.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

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

    public Context context;
    public Handler handler;
    public JsonArray jsonArray;
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    public upload_periodic_key(Context context, Handler handler, JsonArray jsonArray){
        this.context = context;
        this.handler = handler;
        this.jsonArray = jsonArray;
    }

    @Override
    public void run() {
        super.run();

        final Message msg=new Message();
        msg.what=1;

        RequestBody body = RequestBody.create(jsonArray.toString(), JSON);

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://10.0.2.2:8000/downloadNew")
                .post(body)
                .build();

        Call call=client.newCall(request);

        //execute contact_list_child
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("upload periodic key","on Failure");
                Bundle b =new Bundle();
                b.putInt("response_code",0);
                msg.setData(b);
                handler.sendMessage(msg);
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if(response.isSuccessful()){
                    Log.e("upload periodic key","response and success");
                    Bundle b =new Bundle();
                    b.putInt("response_code",1);
                    msg.setData(b);
                    handler.sendMessage(msg);
                }else{
                    Log.e("upload periodic key","response but failed");
                    Bundle b =new Bundle();
                    b.putInt("response_code",2);
                    msg.setData(b);
                    handler.sendMessage(msg);
                }

            }
        });
    }
}
