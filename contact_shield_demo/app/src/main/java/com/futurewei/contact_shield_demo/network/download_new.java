package com.futurewei.contact_shield_demo.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.contactshield.ContactShield;
import com.huawei.hms.contactshield.PeriodicKey;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
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

public class download_new extends Thread {

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

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("timestamp", last_download_timeStamp);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e("download new", last_download_timeStamp+"");
        RequestBody formBody = RequestBody.create(jsonObject.toString(), JSON);

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://us-central1-contact-tracing-demo-281120.cloudfunctions.net/downloadNew")
                .post(formBody)
                .build();

        Call call=client.newCall(request);

        //execute contact_list_child
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("download new ","on failure");
                Bundle b =new Bundle();
                b.putInt("response_code",0);
                msg.setData(b);
                handler.sendMessage(msg);
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if(response.isSuccessful()){
                    List<PeriodicKey> pks = new ArrayList();
                    Log.e("download new ","responded and success");
                    try{
                        String responseBody = response.body().string();
                        responseBody = "{ \"list\": " + responseBody + "}";
                        Log.e("eee", responseBody);
                        JSONObject object = new JSONObject(responseBody);
                        JSONArray ja = (JSONArray) object.get("list");



                        for(int i=0; i<ja.length(); i++){
                            JSONObject ob = ja.getJSONObject(i);
                            PeriodicKey.Builder builder = new PeriodicKey.Builder();
                            builder.setContent(string_to_byte(ob.getString("pk")));
                            builder.setInitialRiskLevel(ob.getInt("risk_level"));
                            builder.setPeriodKeyLifeTime(ob.getInt("life_time"));
                            builder.setPeriodKeyValidTime(ob.getInt("valid_time"));
                            PeriodicKey pk = builder.build();
                            pks.add(pk);
                        }

                        // 27 = 24 + 3
                        // minus 24 here because there are 4 hours (4*6 epochs) margin with the time used by contact shield API
                        // minus 3 here for a half hour buffer
                        int current_time = (int) (System.currentTimeMillis()/1000/600) - 27;
                        final SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt("last_download_timeStamp",current_time);
                        editor.commit();
                    }catch(Exception e){
                        e.printStackTrace();
                    }

                    putSharedKey(pks);


                    Bundle b =new Bundle();
                    b.putInt("response_code",1);
                    msg.setData(b);
                    handler.sendMessage(msg);
                }else{
                    Log.e("download new ","responded but failed");
                    Bundle b =new Bundle();
                    b.putInt("response_code",2);
                    msg.setData(b);
                    handler.sendMessage(msg);
                }

            }
        });
    }

    byte[] string_to_byte(String raw){
        byte[] bytes = new byte[16];
        String[] byte_strings = raw.split(",");
        for(int i = 0; i<16; i++){
            bytes[i] = (byte) Integer.parseInt(byte_strings[i]);
        }

        return bytes;
    }

    void putSharedKey(List<PeriodicKey> sharedKeys){
        Task<Void> task = ContactShield.getContactShieldEngine(context).putSharedKey(sharedKeys);
        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.e("put key", "success");
//                getContactSketch();
            }
        });
        Log.e("put shared key", "ok");
    }
}
