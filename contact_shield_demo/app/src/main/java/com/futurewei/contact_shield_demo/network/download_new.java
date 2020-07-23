package com.futurewei.contact_shield_demo.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;

import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.contactshield.ContactShield;
import com.huawei.hms.contactshield.ContactSketch;
import com.huawei.hms.contactshield.PeriodicKey;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
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

public class download_new extends Thread {

    private static final String TAG = "download new";
    public Context context;
    public Handler handler;
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    public download_new(Context context, Handler handler){
        this.context = context;
        this.handler = handler;
    }

    @Override
    public void run() {
        super.run();

        final Message msg=new Message();
        msg.what=2;

        final SharedPreferences sharedPreferences = context.getSharedPreferences("last_download_timeStamp",MODE_PRIVATE);
        int last_download_timeStamp=sharedPreferences.getInt("last_download_timeStamp",0);
        String user_id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.e(TAG, "user_id:"+user_id);
        last_download_timeStamp = 2658900;

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("timestamp", last_download_timeStamp);
            jsonObject.put("user_id", user_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e(TAG, "last download timestamp: "+last_download_timeStamp+"");
        RequestBody formBody = RequestBody.create(jsonObject.toString(), JSON);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder()
                .url("http://35.239.227.130:5000/zip")
                .post(formBody)
                .build();

        Call call=client.newCall(request);

        //execute contact_list_child
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG,"on failure");
                Log.e(TAG, e.toString());
                Bundle b =new Bundle();
                b.putInt("response_code",0);
                msg.setData(b);
                handler.sendMessage(msg);
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if(response.isSuccessful()){
                    List<PeriodicKey> pks = new ArrayList();
                    String url = response.body().string();
                    Log.e(TAG,"responded and success");
                    Log.e(TAG, "url: "+ url);

                    new download_ZIP(context, user_id).start();

                    Bundle b =new Bundle();
                    b.putInt("response_code",1);
                    msg.setData(b);
                    handler.sendMessage(msg);
                }else{
                    Log.e(TAG,"responded but failed");
                    Log.e(TAG, response.message());
                    Bundle b =new Bundle();
                    b.putInt("response_code",2);
                    b.putString("message", response.body().string());
                    msg.setData(b);
                    handler.sendMessage(msg);
                }

            }
        });
    }

//    void putSharedKey(List<PeriodicKey> sharedKeys){
//        Task<Void> task = ContactShield.getContactShieldEngine(context).putSharedKey(sharedKeys);
//        task.addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void aVoid) {
//                Log.e(TAG, "put key success");
//                getContactSketch();
//            }
//        });
//    }
//
//    void getContactSketch(){
//        Task<ContactSketch> contactSketchTask = ContactShield.getContactShieldEngine(context).getContactSketch();
//        contactSketchTask.addOnSuccessListener(new OnSuccessListener<ContactSketch>() {
//            @Override
//            public void onSuccess(ContactSketch contactSketch) {
//                Log.e(TAG, "sketch"+contactSketch.toString());
//            }
//        });
//    }
}
