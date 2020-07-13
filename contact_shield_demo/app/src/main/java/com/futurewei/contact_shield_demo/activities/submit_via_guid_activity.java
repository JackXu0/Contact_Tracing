package com.futurewei.contact_shield_demo.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import com.futurewei.contact_shield_demo.R;
import com.futurewei.contact_shield_demo.network.get_registration_key_guid;
import com.futurewei.contact_shield_demo.network.get_tan;
import com.futurewei.contact_shield_demo.network.upload_periodic_key;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.contactshield.ContactShield;
import com.huawei.hms.contactshield.PeriodicKey;
import com.huawei.hms.hmsscankit.ScanUtil;
import com.huawei.hms.ml.scan.HmsScan;
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import static com.futurewei.contact_shield_demo.activities.ReportTempActivity.DEFAULT_VIEW;

public class submit_via_guid_activity extends Activity {

    public static final int DEFAULT_VIEW = 0x22;
    private static final int REQUEST_CODE_SCAN = 0X01;

    TextView guid_tv;
    Button submit_button;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.submit_via_guid_activity);

        initView();

        begin_scanning();
    }

    void initView(){
        guid_tv = findViewById(R.id.guid_tv);
        submit_button = findViewById(R.id.submit_button);

        submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String guid = guid_tv.getText().toString();
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("guid", guid);
                    new get_registration_key_guid(getApplicationContext(), myHandler, jsonObject).start();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                finish();
                Toast.makeText(getApplicationContext(),"Thanks for reporting", Toast.LENGTH_LONG).show();
            }
        });
    }

    void begin_scanning(){
        ActivityCompat.requestPermissions(
                submit_via_guid_activity.this,
                new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE},
                DEFAULT_VIEW);

        // After the permissions are applied for, call the barcode scanning view in Default View mode.
        ScanUtil.startScan(submit_via_guid_activity.this, REQUEST_CODE_SCAN, new HmsScanAnalyzerOptions.Creator().setHmsScanTypes(HmsScan.ALL_SCAN_TYPE).create());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (permissions == null || grantResults == null || grantResults.length < 2 || grantResults[0] != PackageManager.PERMISSION_GRANTED || grantResults[1] != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        if (requestCode == DEFAULT_VIEW) {
            // Call the barcode scanning view in Default View mode.
            ScanUtil.startScan(submit_via_guid_activity.this, REQUEST_CODE_SCAN, new HmsScanAnalyzerOptions.Creator().setHmsScanTypes(HmsScan.ALL_SCAN_TYPE).create());

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //receive result after your activity finished scanning
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null) {
            return;
        }
        // Obtain the return value of HmsScan from the value returned by the onActivityResult method by using ScanUtil.RESULT as the key value.
        if (requestCode == REQUEST_CODE_SCAN) {
            Object obj = data.getParcelableExtra(ScanUtil.RESULT);
            if (obj instanceof HmsScan) {
                if (!TextUtils.isEmpty(((HmsScan) obj).getOriginalValue())) {
                    guid_tv.setText(((HmsScan) obj).getOriginalValue());
                }
                return;
            }
        }

    }

    Handler myHandler = new Handler(){
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void handleMessage(@NonNull Message msg) {
            Bundle b = msg.getData();

            int response_code;
            String registration_key;
            String tan;
            JSONObject jsonObject;

            switch (msg.what){

                // Step 1 : handler for get registration key via GUID
                case 3:
                    Log.e("handler info", "get registraion key handler activated");
                    response_code = b.getInt("response_code");


                    if(response_code == 1){
                        registration_key = b.getString("registration_key", "");
                        Log.e("registrationkey handler", registration_key);

                        //store the registration key locally
                        sharedPreferences = getSharedPreferences("upload_pk_history", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("registration_key", registration_key);
                        editor.commit();

                        //use the registration key to fet ch the TAN
                        jsonObject = new JSONObject();
                        try {
                            jsonObject.put("registration_key", registration_key);
                            new get_tan(getApplicationContext(), myHandler, jsonObject).start();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }


                    break;

                // Step 2 : handler for get tan
                case 5:
                    Log.e("handler info", "get registraion key handler activated");
                    response_code = b.getInt("response_code");

                    //If Tan is obtained successfully, use the TAN to upload Periodic keys
                    if(response_code == 1){
                        tan = b.getString("tan");
                        Log.e("tan handler", tan);
                        jsonObject = new JSONObject();
                        try {
                            jsonObject.put("tan", tan);
                            getPeriodicalKey(tan);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }


                    break;

                // Step 3 : handler for upload periodic key
                case 1:
                    response_code = b.getInt("response_code");
                    Log.e("upload pk message", response_code+"");

                    //If the periodic Keys are uploaded successfully, update the latest upload timestamp on local storage
                    if(response_code == 1){
                        //store the latest upload timestamp locally
                        sharedPreferences = getSharedPreferences("upload_pk_history", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt("timestamp", (int) (System.currentTimeMillis()/1000/600));
                        editor.commit();
                    }

                    break;

                default:
                    Log.e("default handler", "triggered");
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
                Log.e("get periodical key","success");
                Log.e("length", periodicKeys.size()+"");
                for(PeriodicKey pk : periodicKeys){
                    byte[] bs = pk.getContent();
                    for(byte b : bs){
                        Log.e("bytee", b+"");
                    }
                    Log.e("pk", pk.toString());
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
                jsonObject.put("valid_time", periodicKey.getPeriodKeyValidTime());
                jsonObject.put("life_time", periodicKey.getPeriodKeyLifeTime());
                jsonObject.put("risk_level", periodicKey.getInitialRiskLevel());
                jsonArray.put(jsonObject);
            }
            JSONObject jo = new JSONObject();
            jo.put("periodic_keys", jsonArray);
            jo.put("tan", tan);
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


}
