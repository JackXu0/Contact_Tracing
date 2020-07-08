package com.futurewei.contact_shield_demo.fragments;

import android.app.Fragment;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.futurewei.contact_shield_demo.R;
import com.futurewei.contact_shield_demo.activities.NotificationsActivity;
import com.futurewei.contact_shield_demo.activities.report_test_result_pre_activity;
import com.futurewei.contact_shield_demo.network.download_new;
import com.futurewei.contact_shield_demo.network.get_tan;
import com.futurewei.contact_shield_demo.network.upload_periodic_key;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.contactshield.ContactShield;
import com.huawei.hms.contactshield.ContactSketch;
import com.huawei.hms.contactshield.PeriodicKey;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class fragment_home extends Fragment {

    private View root;
    Button refresh_btn;
    RadioGroup radioGroup;
    RadioButton positiveButton;
    Button reportButton;
    TextView number_of_hits_tv;
    TextView risk_level_tv;
    TextView scanning_tv;
    HashMap<Integer, String> risk_level_map;
    SharedPreferences sharedPreferences;

    public static final int UPLOAD_INTERVAL_IN_DAYS = 7;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        root= inflater.inflate(R.layout.home_fragment, container, false);

        init_risk_level_map();

        initView(root);

        check_is_scanning();

        return root;

    }

    void initView(View root){
        refresh_btn = (Button) root.findViewById(R.id.refreshButton);
        radioGroup = (RadioGroup) root.findViewById(R.id.radioGroup);
        positiveButton = (RadioButton) root.findViewById(R.id.radioPositive);
        reportButton = (Button) root.findViewById(R.id.reportResults);
        number_of_hits_tv = root.findViewById(R.id.number_of_hits_tv);
        risk_level_tv = root.findViewById(R.id.risk_level_tv);
        scanning_tv = root.findViewById(R.id.scanning_tv);

        sharedPreferences = getContext().getSharedPreferences("dashboard_info",MODE_PRIVATE);
        number_of_hits_tv.setText(""+sharedPreferences.getInt("number_of_hits",0));
        risk_level_tv.setText(sharedPreferences.getString("risk_level", "NO RISK"));




        //EventListener for refresh button
        refresh_btn.setOnClickListener(new View.OnClickListener(){
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v){
                new download_new(getContext(), myHandler).start();
            }
        });

        //EventListener for radio button
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                System.out.println("checkedID is " + checkedId);
                if(checkedId == positiveButton.getId()){
                    reportButton.setVisibility(View.VISIBLE);
                } else{
                    reportButton.setVisibility(View.GONE);
                }
            }
        });

        //EventListener for report your results button
        reportButton.setOnClickListener(new View.OnClickListener(){
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                System.out.println("reportButton pressed");
                Intent intent = new Intent(getContext(), report_test_result_pre_activity.class);
                startActivity(intent);
            }
        });
    }

    boolean check_if_allows_manual_upload(){
        sharedPreferences = getContext().getSharedPreferences("upload_pk_history", MODE_PRIVATE);
        String registration_key = sharedPreferences.getString("registration_key","");
        int latest_uploading_time = sharedPreferences.getInt("timestamp", 0);

        // if registration_key is missing or corrupted, or it has been more than one interval (7 days) since last manual upload, needs upload manually again.
        if(registration_key.length() != 32 || latest_uploading_time > ((int) System.currentTimeMillis()/600000 - UPLOAD_INTERVAL_IN_DAYS*24*6)){
            return true;
        }else if(registration_key.length() == 32 || latest_uploading_time < ((int) System.currentTimeMillis()/600000 - 24 * 6)){
            //If registration_key exists, but has not uploaded in 24 hours, needs one auto upload
            upload_PK_automatically(registration_key);
            return false;
        }else{
            //If registration_key exists, and has auto uploaded within 24 hours, no need for further operations
            return false;
        }
    }

    void init_risk_level_map(){
        risk_level_map = new HashMap<>();
        risk_level_map.put(0, "NO RISK");
        risk_level_map.put(1, "LOWEST");
        risk_level_map.put(2, "LOW");
        risk_level_map.put(3, "MEDIUM LOW");
        risk_level_map.put(4, "MEDIUM");
        risk_level_map.put(5, "MEDIUM_HIGH");
        risk_level_map.put(6, "HIGH");
        risk_level_map.put(7, "EXTRA HIGH");
        risk_level_map.put(8, "HIGHEST");
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    void getContactSketch(){
        Task<ContactSketch> contactSketchTask = ContactShield.getContactShieldEngine(getContext()).getContactSketch();
        contactSketchTask.addOnSuccessListener(new OnSuccessListener<ContactSketch>() {
            @Override
            public void onSuccess(ContactSketch contactSketch) {
                int number_of_hits = contactSketch.getNumberOfHits();
                String risk_level = risk_level_map.get(contactSketch.getMaxRiskLevel());

                sharedPreferences = getContext().getSharedPreferences("dashboard_info",MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("number_of_hits", number_of_hits);
                editor.putString("risk_level", risk_level);
                editor.commit();

                number_of_hits_tv.setText(""+number_of_hits);
                risk_level_tv.setText(risk_level);

                sharedPreferences = getContext().getSharedPreferences("settings",MODE_PRIVATE);
                boolean is_notification_disabled = sharedPreferences.getBoolean("is_notification_disabled", false);
                if(!is_notification_disabled && contactSketch.getMaxRiskLevel() >= 2){
                    make_alert_window();
                }
                Log.e("sketch", contactSketch.toString());
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    void check_is_scanning(){
        sharedPreferences = getContext().getSharedPreferences("settings",MODE_PRIVATE);
        boolean is_app_disabled = sharedPreferences.getBoolean("is_app_disabled", false);
        if(!is_app_disabled){
            scanning_tv.setVisibility(View.VISIBLE);
        }else{
            scanning_tv.setVisibility(View.INVISIBLE);
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    void make_alert_window(){

        NotificationManager notification_manager = (NotificationManager) getContext()
                .getSystemService(Context.NOTIFICATION_SERVICE);

        Intent notificationIntent = new Intent(getContext(), NotificationsActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent = PendingIntent.getActivity(getContext(), 0,
                notificationIntent, 0);
        NotificationCompat.Builder notification_builder;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String chanel_id = "3000";
            CharSequence name = "Channel Name";
            String description = "Chanel Description";
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel mChannel = new NotificationChannel(chanel_id, name, importance);
            mChannel.setDescription(description);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.BLUE);
            notification_manager.createNotificationChannel(mChannel);
            notification_builder = new NotificationCompat.Builder(getContext(), chanel_id);
        } else {
            notification_builder = new NotificationCompat.Builder(getContext());
        }
        notification_builder.setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("corona virus alert")
                .setContentText("You have been exposed to a corona virus patient recently. Please practice self quarantine and contact your doctor if you are not feeling fine.")
                .setAutoCancel(true)
                .setContentIntent(intent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getContext());

// notificationId is a unique int for each notification that you must define
        notificationManager.notify(1, notification_builder.build());


    }

    void upload_PK_automatically(String registration_key){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("registration_key", registration_key);
            new get_tan(getContext(), myHandler, jsonObject).start();
        } catch (JSONException e) {
            e.printStackTrace();
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

                // Step 1 : handler for get tan
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

                // Step 2 : handler for upload periodic key
                case 1:
                    response_code = b.getInt("response_code");
                    Log.e("upload pk message", response_code+"");

                    //If the periodic Keys are uploaded successfully, update the latest upload timestamp on local storage
                    if(response_code == 1){
                        //store the latest upload timestamp locally
                        sharedPreferences = getContext().getSharedPreferences("upload_pk_history", MODE_PRIVATE);
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
        Task<List<PeriodicKey>> task_pk = ContactShield.getContactShieldEngine(getContext()).getPeriodicKey();

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

            (new upload_periodic_key(getContext(), myHandler, jo)).start();
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
