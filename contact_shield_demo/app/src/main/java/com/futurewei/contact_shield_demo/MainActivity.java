package com.futurewei.contact_shield_demo;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.huawei.hmf.tasks.OnCompleteListener;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.contactshield.ContactDetail;
import com.huawei.hms.contactshield.ContactShield;
import com.huawei.hms.contactshield.ContactShieldCallback;
import com.huawei.hms.contactshield.ContactShieldEngine;
import com.huawei.hms.contactshield.ContactShieldSetting;
import com.huawei.hms.contactshield.ContactSketch;
import com.huawei.hms.contactshield.PeriodicKey;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    List<PeriodicKey> sharedKeys;
    byte[] bytes_honor = new byte[]{-89, -52, 90, -14, 19, 11, -105, -61, 33, 96, -23, 22, 96, 72, 94, -124};
    byte[] bytes_mate = new byte[]{50, 125, -44, 23, -109, 102, -124, -71, 11, -76, 118, 34, -101, -75, 79, -7};

    Button start_engine_btn;
    Button stop_engine_btn;
    Button get_periodical_key_btn;
    Button clear_data_btn;
    EditText periodical_key_et;
    EditText valid_time_et;
    Button put_shared_key_btn;
    Button get_contact_sketch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();



    }

    void initView(){
        start_engine_btn = findViewById(R.id.start_engine_btn);
        stop_engine_btn = findViewById(R.id.stop_engine_btn);
        get_periodical_key_btn = findViewById(R.id.get_periodical_key_btn);
        clear_data_btn = findViewById(R.id.clear_data_btn);
        periodical_key_et = findViewById(R.id.periodical_key_et);
        valid_time_et = findViewById(R.id.valid_time_et);
        put_shared_key_btn = findViewById(R.id.put_shared_key_btn);
        get_contact_sketch = findViewById(R.id.get_contact_sketch_btn);

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
                String periodical_key = periodical_key_et.getText().toString().replace("[","").replace("]","").replace(" ","");
                byte[] bytes = new byte[16];
                String[] byte_strings = periodical_key.split(";");
                for(int i = 0; i<16; i++){
                    bytes[i] = (byte) Integer.parseInt(byte_strings[i]);
                }


                int valid_time = Integer.parseInt(valid_time_et.getText().toString());

                List<PeriodicKey> sks = new ArrayList<>();
                PeriodicKey.Builder builder = new PeriodicKey.Builder();
                builder.setContent(bytes);
                builder.setInitialRiskLevel(1);
                builder.setPeriodKeyLifeTime(4);
                builder.setPeriodKeyValidTime(valid_time);
                PeriodicKey pk = builder.build();
                Log.e("input shared key", pk.toString());
                sks.add(pk);

                putSharedKey(sks);
            }
        });

        get_contact_sketch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getContactSketch();
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

//                putSharedKey(sharedKeys);
            }
        });
    }

//    [76, 125, -110, -74, -36, 99, 90, -5, -97, 10, -53, -31, 21, -63, -100, 86]
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

    void putSharedKey(List<PeriodicKey> sharedKeys){
//        List<PeriodicKey> sks = new ArrayList<>();
//        PeriodicKey.Builder builder = new PeriodicKey.Builder();
//        builder.setContent(bytes_honor);
//        builder.setInitialRiskLevel(1);
//        builder.setPeriodKeyLifeTime(10);
//        builder.setPeriodKeyValidTime(2654344);
//        PeriodicKey pk = builder.build();
//        sks.add(pk);
        Task<Void> task = ContactShield.getContactShieldEngine(getApplicationContext()).putSharedKey(sharedKeys);
        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.e("put key", "success");
//                getContactSketch();
            }
        });
        Log.e("put shared key", "ok");
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
