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
import com.futurewei.contact_shield_demo.activities.MainActivity;
import com.futurewei.contact_shield_demo.activities.NotificationsActivity;
import com.futurewei.contact_shield_demo.activities.ReportTestResultActivity;
import com.futurewei.contact_shield_demo.network.download_new;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.contactshield.ContactShield;
import com.huawei.hms.contactshield.ContactSketch;

import java.util.HashMap;

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

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        root= inflater.inflate(R.layout.fragment_home, container, false);

        init_risk_level_map();



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


        check_is_scanning();

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
                Intent intent = new Intent(getContext(), ReportTestResultActivity.class);
                startActivity(intent);
            }
        });


        return root;

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

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("number_of_hits", number_of_hits);
                editor.putString("risk_level", risk_level);
                editor.commit();

                number_of_hits_tv.setText(""+number_of_hits);
                risk_level_tv.setText(risk_level);

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
        Task<Boolean> isRunningTask = ContactShield.getContactShieldEngine(getContext()).isContactShieldRunning();
        isRunningTask.addOnSuccessListener(new OnSuccessListener<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean) {
                if(!aBoolean){
                    scanning_tv.setVisibility(View.INVISIBLE);
                }else{
                    scanning_tv.setVisibility(View.VISIBLE);
                }
            }
        });
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

    Handler myHandler = new Handler(){
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what){

                case 2:
                    getContactSketch();
                    break;

                default:
                    Log.e("default handler", "triggered");
                    break;
            }
        }
    };

}
