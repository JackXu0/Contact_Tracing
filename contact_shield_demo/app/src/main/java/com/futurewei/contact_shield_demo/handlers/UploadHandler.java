package com.futurewei.contact_shield_demo.handlers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.futurewei.contact_shield_demo.activities.InternetConnectionErrorActivity;
import com.futurewei.contact_shield_demo.activities.SubmissionSuccessActivity;
import com.futurewei.contact_shield_demo.network.GetTan;
import com.futurewei.contact_shield_demo.network.UploadPeriodicKey;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.contactshield.ContactShield;
import com.huawei.hms.contactshield.PeriodicKey;


import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class UploadHandler extends Handler {

    Context context;
    String TAG;
    SharedPreferences sharedPreferences;
    Handler handler;
    ProgressBar progressBar;
    private static final String UPLOAD_PK_HISTORY = "upload_pk_history";
    private static final String TIMESTAMP = "timestamp";
    private static final String REGISTRATION_KEY = "registration_key";


    public UploadHandler(Context context, String tag, ProgressBar progressBar){
        this.context = context;
        this.TAG = tag;
        this.handler = this;
        this.progressBar = progressBar;
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        Bundle b = msg.getData();

        int responseCode= b.getInt("response_code");;
        String responseBody = b.getString("response_body");
        SharedPreferences.Editor editor;
        String registration_key;

        if(responseCode == 0){
            context.startActivity(new Intent(context, InternetConnectionErrorActivity.class));
            ((Activity)context).finish();
            return;
        }
        //TODO: whether to put this here
        //TODO: whether to display detail errors
        else if (responseCode == 2){
            String error_msg = responseBody;
            Log.e(TAG, error_msg);
            Toast.makeText(context, error_msg, Toast.LENGTH_SHORT).show();
//            context.startActivity(new Intent(context, SubmissionUnsuccessActivity.class));
            ((Activity)context).finish();
            return;
        }

        switch (msg.what){
            // Step 1 : handler for get registration key via QR Code
            case 1:
                Log.e(TAG, "Get Registration Key via QR Code handler activated");

                registration_key = responseBody;
                Log.e(TAG, "registration key: "+registration_key);

                //store the registration key locally
                sharedPreferences = context.getSharedPreferences(UPLOAD_PK_HISTORY, MODE_PRIVATE);
                editor = sharedPreferences.edit();
                editor.putString(REGISTRATION_KEY, registration_key);
                editor.commit();

                //use the registration key to fet ch the TAN
                new GetTan(context, handler, registration_key).start();

                break;


            // Step 1 : handler for get registration key via teletan
            case 2:
                Log.e(TAG, "Get Registration Key via TELETAN handler activated");

                registration_key = responseBody;
                Log.e(TAG, "registration key: "+registration_key);

                //store the registration key locally
                sharedPreferences = context.getSharedPreferences(UPLOAD_PK_HISTORY, MODE_PRIVATE);
                editor = sharedPreferences.edit();
                editor.putString(REGISTRATION_KEY, registration_key);
                editor.commit();

                //use the registration key to fet ch the TAN
                new GetTan(context, handler, registration_key).start();

                break;

            // Step 2 : handler for get tan
            case 3:
                Log.e(TAG, "Get TAN handler activated");

                //If Tan is obtained successfully, use the TAN to upload Periodic keys

                String tan = responseBody;
                Log.e(TAG, "TAN: "+tan);
                getPeriodicalKey(tan);

                break;

            // Step 3 : handler for upload periodic key
            case 4:
                Log.e(TAG, "Upload PK handler activated");

                //If the periodic Keys are uploaded successfully, update the latest upload timestamp on local storage

                //store the latest upload timestamp locally
                sharedPreferences = context.getSharedPreferences(UPLOAD_PK_HISTORY, MODE_PRIVATE);
                editor = sharedPreferences.edit();
                editor.putInt(TIMESTAMP, (int) (System.currentTimeMillis()/1000/600));
                editor.commit();

                if(progressBar != null)
                    progressBar.setVisibility(View.INVISIBLE);

                //jump to submit success activity
                Toast.makeText(context,"Thanks for reporting", Toast.LENGTH_LONG).show();
                context.startActivity(new Intent(context, SubmissionSuccessActivity.class));

                ((Activity) context).finish();

                break;

            default:
                Log.e(TAG, "default handler triggered");
                break;
        }
    }

    // This methods get PKs from Contact Shield API and then call upload_periodic_keys to upload
    void getPeriodicalKey(String tan){
        Task<List<PeriodicKey>> task_pk = ContactShield.getContactShieldEngine(context).getPeriodicKey();

        task_pk.addOnSuccessListener((List<PeriodicKey> periodicKeys) -> {
                Log.e(TAG,"get periodical key success");
                Log.e(TAG, "periodic key list length: "+periodicKeys.size()+"");

                (new UploadPeriodicKey(context, handler, periodicKeys, tan)).start();
        });

        task_pk.addOnFailureListener((Exception e) -> Log.e(TAG, e.getMessage()));
    }
}
