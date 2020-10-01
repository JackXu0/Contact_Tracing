/**
 * Copyright © 2020  Futurewei Technologies, Inc. All rights reserved.
 *
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *
 *
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *
 *
 * Unless required by applicable law or agreed to in writing, software
 *
 * distributed under the License is distributed on an "AS IS" BASIS,
 *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the License for the specific language governing permissions and
 *
 * limitations under the License.
 */

package com.futurewei.contact_shield_demo.network;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * This class is the template for all network requests.
 */
public class NetworkTemplate extends Thread {

    public String TAG;
    public Context context;
    public Message msg=new Message();
    public Handler handler;
    public int what;
    public String url;
    public RequestBody requestBody;
    private static final String RESPONSE_CODE = "response_code";
    private static final String RESPONSE_BODY = "response_body";

    public NetworkTemplate(String name, Context context, Handler handler, int what, String url){
        this.TAG = name;
        this.context = context;
        this.handler = handler;
        this.what = what;
        this.url = url;
    }

    @Override
    public void run() {
        super.run();

        msg.what=what;

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        Call call=client.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG,"on Failure");
                Bundle b =new Bundle();
                b.putInt(RESPONSE_CODE,0);
                b.putString(RESPONSE_BODY, e.getMessage());
                msg.setData(b);
                handler.sendMessage(msg);
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if(response.isSuccessful()){
                    String response_body = response.body().string();
                    Log.e(TAG,"response and success");
                    Log.e(TAG, "response body: "+response_body);

                    Bundle b =new Bundle();
                    b.putInt(RESPONSE_CODE,1);
                    b.putString(RESPONSE_BODY, response_body);
                    msg.setData(b);
                    handler.sendMessage(msg);
                }else{
                    Log.e(TAG,"response but failed");
                    Bundle b =new Bundle();
                    b.putInt(RESPONSE_CODE,2);
                    b.putString(RESPONSE_BODY, response.body().string());
                    msg.setData(b);
                    handler.sendMessage(msg);
                }

            }
        });
    }


}
