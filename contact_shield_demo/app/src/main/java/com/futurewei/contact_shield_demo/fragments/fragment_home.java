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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.futurewei.contact_shield_demo.BackgroundContactCheckingIntentService;
import com.futurewei.contact_shield_demo.R;

import com.futurewei.contact_shield_demo.activities.NotificationsActivity;
import com.futurewei.contact_shield_demo.activities.report_test_result_pre_activity;
import com.futurewei.contact_shield_demo.network.download_new;
import com.futurewei.contact_shield_demo.network.get_tan;
import com.google.android.material.card.MaterialCardView;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.contactshield.ContactShield;
import com.huawei.hms.contactshield.ContactShieldSetting;
import com.huawei.hms.contactshield.ContactSketch;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;

public class fragment_home extends Fragment {

    Context context;
    String token = "3bdd528fd98947bcaffa0d8fda68ca54";
    private View root;
    MaterialCardView my_status_card;
    ConstraintLayout heading;
    TextView scanning_tv;
    TextView headling_tv;
    RadioGroup radioGroup;
    RadioButton radio_positive;
    RadioButton radio_negtive;
    Button reportButton;
    Button refresh_btn;
    public static TextView number_of_hits_tv;
    public static TextView risk_level_tv;

    HashMap<Integer, String> risk_level_map;
    SharedPreferences sharedPreferences;
    private static final String TAG = "fragment home";


    public static final int UPLOAD_INTERVAL_IN_DAYS = 7;
    public static final boolean FLEXIBLE_MY_STATUS_ENABLED = false;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        this.context = getActivity();

        root= inflater.inflate(R.layout.home_fragment, container, false);

        init_risk_level_map();

        initView(root);

        return root;

    }

    @Override
    public void onResume() {
        super.onResume();
        refresh_UI();
    }

    void initView(View root){
        my_status_card = root.findViewById(R.id.card1);
        heading = root.findViewById(R.id.Heading);
        headling_tv = root.findViewById(R.id.heading_tv);
        refresh_btn = (Button) root.findViewById(R.id.refreshButton);
        radioGroup = (RadioGroup) root.findViewById(R.id.radioGroup);
        radio_positive = (RadioButton) root.findViewById(R.id.radioPositive);
        radio_negtive = root.findViewById(R.id.radioNegative);
        reportButton = (Button) root.findViewById(R.id.reportResults);
        number_of_hits_tv = root.findViewById(R.id.number_of_hits_tv);
        risk_level_tv = root.findViewById(R.id.risk_level_tv);
        scanning_tv = root.findViewById(R.id.scanning_tv);




        //EventListener for radio button
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                sharedPreferences = context.getSharedPreferences("my staus choice", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if(checkedId == radio_positive.getId()){
                    reportButton.setVisibility(View.VISIBLE);
                    editor.putBoolean("choice", true);
                } else{
                    reportButton.setVisibility(View.GONE);
                    editor.putBoolean("choice", false);
                }
                editor.commit();
            }
        });

        //EventListener for report your results button
        reportButton.setOnClickListener(new View.OnClickListener(){
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                sharedPreferences = getContext().getSharedPreferences("settings", MODE_PRIVATE);
                boolean is_app_disabled = sharedPreferences.getBoolean("is_app_disabled", false);
                // report is only supported when contact shield API is running
                if(!is_app_disabled){
                    Intent intent = new Intent(context, report_test_result_pre_activity.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(context, "Please enable the app before reporting", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //EventListener for refresh button
        refresh_btn.setOnClickListener(new View.OnClickListener(){
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v){
                new download_new(context, myHandler).start();
            }

        });
    }

    void refresh_UI(){

        //refreshing scanning button
        check_is_scanning();

        //refresh my status choices
        sharedPreferences = context.getSharedPreferences("my staus choice", MODE_PRIVATE);
        boolean choice = sharedPreferences.getBoolean("choice", false);
        radio_negtive.setChecked(!choice);
        radio_positive.setChecked(choice);

        //refresh dashboard
        sharedPreferences = context.getSharedPreferences("dashboard_info",MODE_PRIVATE);
        number_of_hits_tv.setText(""+sharedPreferences.getInt("number_of_hits",0));
        risk_level_tv.setText(""+sharedPreferences.getInt("risk_level", 0));



        // Check is manually upload is needed
        if(FLEXIBLE_MY_STATUS_ENABLED && !check_if_allows_manual_upload()){
            // disable two radio buttons
            radio_negtive.setEnabled(false);
            radio_positive.setEnabled(false);

            //set positive button checked
            radio_positive.setChecked(true);
            radio_negtive.setChecked(false);

            // gray background for my status panel and change heading text
            my_status_card.setBackgroundColor(getResources().getColor(R.color.disable_gray));
            headling_tv.setText("Result Reported");
//            headling_tv.setTextColor(getResources().getColor(R.color.disable_gray));
            reportButton.setVisibility(View.GONE);

            // alert user when click my status panel
            my_status_card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "Your result has been reported", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    boolean check_if_allows_manual_upload(){
        sharedPreferences = context.getSharedPreferences("upload_pk_history", MODE_PRIVATE);
        String registration_key = sharedPreferences.getString("registration_key","");
        int latest_uploading_time = sharedPreferences.getInt("timestamp", 0);
        sharedPreferences = getContext().getSharedPreferences("settings", MODE_PRIVATE);
        boolean is_PK_upload_disabled = sharedPreferences.getBoolean("is_PK_upload_disabled", false);

        // if registration_key is missing or corrupted, or it has been more than one interval (7 days) since last manual upload, needs upload manually again.
        if(registration_key.length() != 32 || latest_uploading_time < ((int) System.currentTimeMillis()/600000 - UPLOAD_INTERVAL_IN_DAYS*24*6)){
            return true;
        }
        //If registration_key exists, but has not uploaded in 24 hours, needs one auto upload
        else if(registration_key.length() == 32 || latest_uploading_time < ((int) System.currentTimeMillis()/600000 - 24 * 6)){
            //check if uploading PK has been disabled by the user
            if(!is_PK_upload_disabled)
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
        Task<ContactSketch> contactSketchTask = ContactShield.getContactShieldEngine(context).getContactSketch(token);
        contactSketchTask.addOnSuccessListener(new OnSuccessListener<ContactSketch>() {
            @Override
            public void onSuccess(ContactSketch contactSketch) {
                int number_of_hits = contactSketch.getNumberOfHits();
                int risk_level = contactSketch.getMaxRiskValue();

                sharedPreferences = getContext().getSharedPreferences("dashboard_info",MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("number_of_hits", number_of_hits);
                editor.putInt("risk_level", risk_level);
                editor.commit();

                number_of_hits_tv.setText(""+number_of_hits);
                risk_level_tv.setText(risk_level);

                sharedPreferences = getContext().getSharedPreferences("settings",MODE_PRIVATE);
                boolean is_notification_disabled = sharedPreferences.getBoolean("is_notification_disabled", false);
                if(!is_notification_disabled && contactSketch.getMaxRiskValue() >= 2){
                    make_alert_window();
                }
                Log.e(TAG, "sketch"+contactSketch.toString());
            }
        });
    }

    void check_is_scanning(){
        sharedPreferences = getContext().getSharedPreferences("settings",MODE_PRIVATE);
        boolean is_app_disabled = sharedPreferences.getBoolean("is_app_disabled", false);
        if(!is_app_disabled){
            scanning_tv.setVisibility(View.VISIBLE);
            engine_start_pre_check();
            Log.e(TAG, "contact shielding is running");
        }else{
            scanning_tv.setVisibility(View.INVISIBLE);
            Log.e(TAG, "contact shielding is not running");
        }

    }

    void make_alert_window(){

        NotificationManager notification_manager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        Intent notificationIntent = new Intent(context, NotificationsActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent = PendingIntent.getActivity(context, 0,
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
            notification_builder = new NotificationCompat.Builder(context, chanel_id);
        } else {
            notification_builder = new NotificationCompat.Builder(context);
        }
        notification_builder.setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("corona virus alert")
                .setContentText("You have been exposed to a corona virus patient recently. Please practice self quarantine and contact your doctor if you are not feeling fine.")
                .setAutoCancel(true)
                .setContentIntent(intent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

// notificationId is a unique int for each notification that you must define
        notificationManager.notify(1, notification_builder.build());


    }

    void upload_PK_automatically(String registration_key){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("registration_key", registration_key);
            new get_tan(context, myHandler, jsonObject).start();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void engine_start_pre_check(){
        Log.d(TAG, "engine_start_pre_check");
        Task<Boolean> isRunningTask = ContactShield.getContactShieldEngine(context).isContactShieldRunning();
        isRunningTask.addOnSuccessListener(aBoolean -> {
            if(!aBoolean){
                engine_start();
                Log.e(TAG, "isContactShieldRunning >> NO");
            }else{
                Log.e(TAG, "isContactShieldRunning >> YES");
            }
        });
    }

    void engine_start(){
        Log.d(TAG, "engine_start");
        PendingIntent pendingIntent = PendingIntent.getService(getActivity(), 0, new Intent(getActivity(), BackgroundContactCheckingIntentService.class),
                PendingIntent.FLAG_UPDATE_CURRENT);


        ContactShield.getContactShieldEngine(context).startContactShield(pendingIntent, ContactShieldSetting.DEFAULT)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "startContactShield >> Success"))
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    Log.e(TAG, "startContactShield >> Failure");
                });


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


                default:
                    Log.e(TAG, "default handler triggered");
                    break;
            }
        }
    };

    
}
