package com.futurewei.contact_shield_demo.activities;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.futurewei.contact_shield_demo.R;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.contactshield.ContactShield;
import com.huawei.hms.contactshield.PeriodicKey;

import com.futurewei.contact_shield_demo.network.upload_periodic_key;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class VerificationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        //EventListener for submit button
        Button submitButton = (Button) findViewById(R.id.submitButton);
        submitButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Log.e("verrification","Submit Button successfully pressed.");
                getPeriodicalKey();
                finish();
            }
        });

        //EventListener for cancel button
        Button cancelButton = (Button) findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                System.out.println("cancel button pressed, go back to home page");
                finish();
            }
        });
    }

    void getPeriodicalKey(){
        Task<List<PeriodicKey>> task_pk = ContactShield.getContactShieldEngine(this).getPeriodicKey();

        task_pk.addOnSuccessListener(new OnSuccessListener<List<PeriodicKey>>() {
            @Override
            public void onSuccess(List<PeriodicKey> periodicKeys) {
                Log.e("get periodical key","success");
                Log.e("length", periodicKeys.size()+"");
                for(PeriodicKey pk : periodicKeys){
                    byte[] bs = pk.getContent();
                    for(byte b : bs){
                        Log.e("bytee", b+"");
                    }
                    Log.e("pk", pk.toString());
                }

                upload_periodic_keys(periodicKeys);

//                putSharedKey(sharedKeys);
            }
        });
    }

    void upload_periodic_keys(List<PeriodicKey> periodic_keys){

        JSONArray jsonArray = new JSONArray();
        try {

            for(PeriodicKey periodicKey : periodic_keys){
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("pk", extract_pk_string(periodicKey.toString()));
                jsonObject.put("valid_time", periodicKey.getPeriodKeyValidTime());
                jsonObject.put("life_time", periodicKey.getPeriodKeyLifeTime());
                jsonObject.put("risk_level", periodicKey.getInitialRiskLevel());
                jsonArray.put(jsonObject);
            }
            JSONObject jo = new JSONObject();
            jo.put("periodic_keys", jsonArray);

            Log.e("json object", jo.toString());

            (new upload_periodic_key(this, myHandler, jo)).start();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    String extract_pk_string(String raw){
        int s = raw.indexOf('[');
        int e = raw.indexOf(']');
        return raw.substring(s+1,e).replace(" ","");
    }


    Handler myHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            Bundle b;
            int code;
            switch (msg.what){
                case 1:
                    Log.e("upload pk message", msg.getData().getInt("response_code")+"");
                    break;
                default:
                    Log.e("default handler", "triggered");
                    break;
            }
        }
    };
}