package com.futurewei.contact_shield_demo.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.futurewei.contact_shield_demo.BackgroundContactCheckingIntentService;
import com.futurewei.contact_shield_demo.R;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.contactshield.ContactDetail;
import com.huawei.hms.contactshield.ContactShield;
import com.huawei.hms.contactshield.ContactShieldSetting;
import com.huawei.hms.contactshield.ContactSketch;
import com.huawei.hms.contactshield.PeriodicKey;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import com.futurewei.contact_shield_demo.network.upload_periodic_key;
import com.futurewei.contact_shield_demo.network.download_new;

public class MainActivity extends AppCompatActivity {

    List<PeriodicKey> sharedKeys;

    Button start_engine_btn;
    Button stop_engine_btn;
    Button get_periodical_key_btn;
    Button clear_data_btn;
    Button put_shared_key_btn;
    Button get_contact_sketch_btn;
    Button push_notification_btn;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;

        initView();

        Log.e("info1", Build.VERSION.SDK_INT+"");
        Log.e("info2", Build.DEVICE);
        Log.e("info3", Build.MANUFACTURER);
        Log.e("info4", Build.MODEL);
        Log.e("info5", Build.ID);
        Log.e("info6", Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID));
    }

    void initView(){
        start_engine_btn = findViewById(R.id.start_engine_btn);
        stop_engine_btn = findViewById(R.id.stop_engine_btn);
        get_periodical_key_btn = findViewById(R.id.get_periodical_key_btn);
        clear_data_btn = findViewById(R.id.clear_data_btn);
        put_shared_key_btn = findViewById(R.id.put_shared_key_btn);
        get_contact_sketch_btn = findViewById(R.id.get_contact_sketch_btn);
        push_notification_btn = findViewById(R.id.push_notification_btn);

        start_engine_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                engine_start_pre_check();
            }
        });

        stop_engine_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                engine_stop_pre_check();
            }
        });

        get_periodical_key_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPeriodicalKey();
            }
        });

        clear_data_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clear_data();
            }
        });

        put_shared_key_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new download_new(context, myHandler).start();
            }
        });

        get_contact_sketch_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getContactSketch();
            }
        });

        push_notification_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                make_alert_window();
            }
        });
    }

    void engine_start_pre_check(){
        Task<Boolean> isRunningTask = ContactShield.getContactShieldEngine(this).isContactShieldRunning();
        isRunningTask.addOnSuccessListener(new OnSuccessListener<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean) {
                if(!aBoolean){
                    engine_start();
                    Log.e("Is running", "NO");
                }else{
                    Log.e("Is running", "YES");
                }
            }
        });
    }

    void engine_stop_pre_check(){
        Task<Boolean> isRunningTask = ContactShield.getContactShieldEngine(this).isContactShieldRunning();
        isRunningTask.addOnSuccessListener(new OnSuccessListener<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean) {
                if(!aBoolean){
                    Log.e("Is running", "NO");
                }else{
                    engine_stop();
                    Log.e("Is running", "YES");
                }
            }
        });
    }

    void engine_start(){
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, new Intent(this, BackgroundContactCheckingIntentService.class),
                PendingIntent.FLAG_UPDATE_CURRENT);


        Task<Void> engine_start_task = ContactShield.getContactShieldEngine(this).startContactShield(pendingIntent, ContactShieldSetting.DEFAULT)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Engine start", "Success");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        e.printStackTrace();
                        Log.e("Engine start", "Failure");
                    }
                });


    }

    void engine_stop(){
        Task<Void> engine_start_task = ContactShield.getContactShieldEngine(this).stopContactShield()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Engine stop", "Success");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        e.printStackTrace();
                        Log.e("Engine stop", "Failure");
                    }
                });
    }

    void clear_data(){
        Task<Void> engine_start_task = ContactShield.getContactShieldEngine(this).clearData()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("clear data", "Success");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        e.printStackTrace();
                        Log.e("clear data", "Failure");
                    }
                });
    }



    void getPeriodicalKey(){
        Task<List<PeriodicKey>> task_pk = ContactShield.getContactShieldEngine(this).getPeriodicKey();

        task_pk.addOnSuccessListener(new OnSuccessListener<List<PeriodicKey>>() {
            @Override
            public void onSuccess(List<PeriodicKey> periodicKeys) {
                sharedKeys = periodicKeys;
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

    void getContactSketch(){
        Task<ContactSketch> contactSketchTask = ContactShield.getContactShieldEngine(getApplicationContext()).getContactSketch();
        contactSketchTask.addOnSuccessListener(new OnSuccessListener<ContactSketch>() {
            @Override
            public void onSuccess(ContactSketch contactSketch) {
                Log.e("sketch", contactSketch.toString());
            }
        });
    }

    void getContactDetail(){
        Task<List<ContactDetail>> contactDetailTask = ContactShield.getContactShieldEngine(getApplicationContext()).getContactDetail();
        contactDetailTask.addOnSuccessListener(new OnSuccessListener<List<ContactDetail>>() {
            @Override
            public void onSuccess(List<ContactDetail> contactDetails) {
                for(ContactDetail cd : contactDetails){
                    Log.e("detail", cd.toString());
                }
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


    void putSharedKey(List<PeriodicKey> sharedKeys){
        Task<Void> task = ContactShield.getContactShieldEngine(getApplicationContext()).putSharedKey(sharedKeys);
        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.e("put key", "success");
            }
        });
        Log.e("put shared key", "ok");
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
                case 2:

                    break;

                default:
                    Log.e("default handler", "triggered");
                    break;
            }
        }
    };

    void make_alert_window(){

        NotificationManager notification_manager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);

        Intent notificationIntent = new Intent(context, MainActivity.class);
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
            notification_builder = new NotificationCompat.Builder(this, chanel_id);
        } else {
            notification_builder = new NotificationCompat.Builder(this);
        }
        notification_builder.setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("corona virus alert")
                .setContentText("You have been exposed to a corona virus patient recently. Please practice self quarantine and contact your doctor if you are not feeling fine.")
                .setAutoCancel(true)
                .setContentIntent(intent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

// notificationId is a unique int for each notification that you must define
        notificationManager.notify(1, notification_builder.build());


    }


//    void alarm_test(){
//        AlarmManager alarmManager =
//                (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
//        PendingIntent pendingIntent =
//                PendingIntent.getService(getApplicationContext(), 0, new Intent(getApplicationContext(), AlarmReceiver.class),
//                        PendingIntent.FLAG_NO_CREATE);
//        if (pendingIntent != null && alarmManager != null) {
//            alarmManager.cancel(pendingIntent);
//        }
//
//        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
//                SystemClock.elapsedRealtime() +
//                        60 * 1000, new Intent(getApplicationContext(), AlarmReceiver.class));
//    }
}
