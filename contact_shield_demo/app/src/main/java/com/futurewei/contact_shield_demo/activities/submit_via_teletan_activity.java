package com.futurewei.contact_shield_demo.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.chaos.view.PinView;
import com.futurewei.contact_shield_demo.R;
import com.futurewei.contact_shield_demo.network.get_registration_key_teletan;
import com.futurewei.contact_shield_demo.network.get_tan;
import com.futurewei.contact_shield_demo.utils.H2GUtils;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.contactshield.ContactShield;
import com.huawei.hms.contactshield.PeriodicKey;

import com.futurewei.contact_shield_demo.network.upload_periodic_key;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.regex.Pattern;

public class submit_via_teletan_activity extends Activity {

    private static final String TAG = "submit_via_teletan_activity";
    SharedPreferences sharedPreferences;
    TextView errorMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        initView();
    }

    void initView(){
        PinView pinView = findViewById(R.id.firstPinView);
        errorMessage= findViewById(R.id.errormessage);

        Button submitButton = (Button) findViewById(R.id.submitButton);
        Button cancelButton = (Button) findViewById(R.id.cancelButton);

        //Setting for pinview
        pinView.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorAccent, getTheme()));
        pinView.setTextColor(ResourcesCompat.getColorStateList(getResources(), R.color.colorPrimary, getTheme()));
        pinView.setLineColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimary, getTheme()));
        pinView.setLineColor(ResourcesCompat.getColorStateList(getResources(), R.color.colorPrimary, getTheme()));
        pinView.setItemHeight(getResources().getDimensionPixelSize(R.dimen.pv_pin_view_item_size));
        pinView.setItemWidth(getResources().getDimensionPixelSize(R.dimen.pv_pin_view_item_size));
        pinView.setItemRadius(getResources().getDimensionPixelSize(R.dimen.pv_pin_view_item_radius));
        pinView.setItemSpacing(getResources().getDimensionPixelSize(R.dimen.pv_pin_view_item_spacing));
        pinView.setLineWidth(getResources().getDimensionPixelSize(R.dimen.pv_pin_view_item_line_width));
        pinView.setAnimationEnable(true);// start animation when adding text
        pinView.setCursorVisible(true);
        pinView.setCursorColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimary, getTheme()));
        pinView.setCursorWidth(getResources().getDimensionPixelSize(R.dimen.pv_pin_view_cursor_width));
        pinView.setItemBackgroundColor(Color.WHITE);
        pinView.setHideLineWhenFilled(false);

        pinView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                errorMessage.setVisibility(View.GONE);
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        //EventListener for submit button
        submitButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Log.e("verrification","Submit Button successfully pressed.");
                String teletan = pinView.getText().toString();
                //Check if teletan is 6 digit number
                if(!Pattern.matches("[0-9]{6}", teletan)){
                    errorMessage.setText("Please enter a valid teleTAN.");
                    errorMessage.setVisibility(View.VISIBLE);
                    return;
                }
                Log.e(TAG, "teletan pinview: "+teletan+";;");
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("teletan", teletan);
                    new get_registration_key_teletan(getApplicationContext(), myHandler, jsonObject).start();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //finish();
            }
        });

        //EventListener for cancel button
        cancelButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Log.e(TAG, "cancel button pressed, go back to home page");
                finish();
            }
        });
    }

    Handler myHandler = new Handler(){
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void handleMessage(@NonNull Message msg) {
            Bundle b = msg.getData();
            String registration_key;
            String tan;
            int response_code = b.getInt("response_code");
            String err_msg;
            JSONObject jsonObject;

            if(response_code == 0){
                Intent intent = new Intent(getApplicationContext(), internet_connection_error_Activity.class);
                startActivity(intent);
                finish();
//                Toast.makeText(getApplicationContext(), "No Internet Connection!", Toast.LENGTH_SHORT).show();
            }

            switch (msg.what){
                // Step 1 : handler for get registration key via teletan
                case 4:
                    Log.e(TAG, "get registraion key handler activated");
                    // If registration key obtained successfully, use the registration key to fetch the TAN and store the registration key locally
                    if(response_code == 1){
                        registration_key = b.getString("registration_key");
                        Log.e(TAG, "registration key: "+registration_key);

                        //store the registration key locally
                        sharedPreferences = getSharedPreferences("upload_pk_history", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("registration_key", registration_key);
                        editor.commit();

                        //use the registration key to fetch the TAN
                        jsonObject = new JSONObject();
                        try {
                            jsonObject.put("registration_key", registration_key);
                            new get_tan(getApplicationContext(), myHandler, jsonObject).start();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Intent intent = new Intent(getApplicationContext(), submission_success_Activity.class);
                        startActivity(intent);
                        finish();

                    }else if(response_code == 2){
                        err_msg = b.getString("message");
                        Log.e(TAG, err_msg);
//                        Toast.makeText(getApplicationContext(), err_msg, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), submission_unsuccess_Activity.class);
                        startActivity(intent);
                        finish();
                    }

                    break;

                // Step 2 : handler for get tan
                case 5:
                    Log.e(TAG, "get registraion key handler activated");
                    //If Tan is obtained successfully, use the TAN to upload Periodic keys
                    if(response_code == 1){
                        tan = b.getString("tan");
                        Log.e(TAG, "TAN: "+tan);
                        jsonObject = new JSONObject();
                        try {
                            jsonObject.put("tan", tan);
                            getPeriodicalKey(tan);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else if(response_code == 2){
                        err_msg = b.getString("message");
                        Log.e(TAG, err_msg);
                        Toast.makeText(getApplicationContext(), err_msg, Toast.LENGTH_SHORT).show();
                    }


                    break;

                // Step 3 : handler for upload periodic key
                case 1:
                    Log.e(TAG, "handler for upload periodic key activated");
                    //If the periodic Keys are uploaded successfully, update the latest upload timestamp on local storage
                    if(response_code == 1){
                        //store the latest upload timestamp locally
                        sharedPreferences = getSharedPreferences("upload_pk_history", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt("timestamp", (int) (System.currentTimeMillis()/1000/600));
                        editor.commit();
                        Toast.makeText(getApplicationContext(),"Thanks for reporting", Toast.LENGTH_LONG).show();
                    }else if(response_code == 2){
                        err_msg = b.getString("message");
                        Log.e(TAG, err_msg);
                        Toast.makeText(getApplicationContext(), err_msg, Toast.LENGTH_SHORT).show();
                    }


                    break;

                default:
                    Log.e(TAG, "default handler triggered");
                    break;
            }
        }
    };

    // This methods get PKs from Contact Shield API and then call upload_periodic_keys
    void getPeriodicalKey(String tan){
        Task<List<PeriodicKey>> task_pk = ContactShield.getContactShieldEngine(this).getPeriodicKey();

        task_pk.addOnSuccessListener(new OnSuccessListener<List<PeriodicKey>>() {
            @Override
            public void onSuccess(List<PeriodicKey> periodicKeys) {
                Log.e(TAG,"get periodical key success");
                Log.e(TAG, "Peroidic key list length: "+periodicKeys.size()+"");
                for(PeriodicKey pk : periodicKeys){
                    byte[] bs = pk.getContent();
                }

                upload_periodic_keys(periodicKeys, tan);
            }
        });
    }

    // This method prepares data for making the internet request
    void upload_periodic_keys(List<PeriodicKey> periodic_keys, String tan){

        JSONArray jsonArray = new JSONArray();
        try {

            for(PeriodicKey periodicKey : periodic_keys){
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("pk", extract_pk_string(periodicKey.toString()));
                jsonObject.put("valid_time", (int) (periodicKey.getPeriodicKeyValidTime()));
                jsonObject.put("life_time", (int) periodicKey.getPeriodicKeyLifeTime());

//                jsonObject.put("valid_time", ((int) (System.currentTimeMillis()/600000)) -1 );
//                jsonObject.put("life_time", 1);
                jsonObject.put("risk_level", 2);
                jsonObject.put("gms_key", H2GUtils.getGmsKey(periodicKey.getContent()));
                jsonArray.put(jsonObject);
            }
            JSONObject jo = new JSONObject();
            jo.put("periodic_keys", jsonArray);
            jo.put("tan", tan);
            Log.e(TAG, "JSON object: "+jo.toString());

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
}