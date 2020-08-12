package com.futurewei.contact_shield_demo.activities;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import com.chaos.view.PinView;
import com.futurewei.contact_shield_demo.R;
import com.futurewei.contact_shield_demo.network.GetRegistrationKeyTeleTAN;
import com.futurewei.contact_shield_demo.handlers.UploadHandler;
import com.futurewei.contact_shield_demo.network.ReportOperation;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.contactshield.ContactShield;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;

public class SubmitViaTeletanActivity extends Activity {

    private static final String TAG = "submit_via_teletan_activity";
    TextView errorMessage;
    Context context;
    Handler handler;
    ProgressBar progressBar;
    com.google.android.material.card.MaterialCardView cardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_via_teletan);

        context = this;
        handler = new UploadHandler(context, TAG, null);

        initView();
    }

    void initView(){
        PinView pinView = findViewById(R.id.firstPinView);
        errorMessage= findViewById(R.id.errormessage);
        progressBar = findViewById(R.id.progress_bar);
        cardView = findViewById(R.id.card1);

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
                errorMessage.setVisibility(View.GONE);
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if(!Pattern.matches("[0-9]{6}", s.toString())){
//                    errorMessage.setVisibility(View.VISIBLE);
//                }else{
//                    errorMessage.setVisibility(View.GONE);
//                }
            }
            @Override
            public void afterTextChanged(Editable s) {
                if(!Pattern.matches("[0-9]{6}", s.toString())){
                    errorMessage.setVisibility(View.VISIBLE);
                }else{
                    errorMessage.setVisibility(View.GONE);
                }
            }
        });

        //EventListener for submit button
        submitButton.setOnClickListener((View v) -> {
            Log.e("verrification","Submit Button successfully pressed.");
            String teletan = pinView.getText().toString();
            //Check if teletan is 6 digit number
            if(!Pattern.matches("[0-9]{6}", teletan)){
                errorMessage.setVisibility(View.VISIBLE);
                return;
            }
            Log.e(TAG, "teletan pinview: "+teletan+";;");
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("teletan", teletan);
                progressBar.setVisibility(View.VISIBLE);
                cardView.setVisibility(View.GONE);
                new GetRegistrationKeyTeleTAN(getApplicationContext(), handler, jsonObject).start();
            } catch (JSONException e) {
                Log.e(TAG, e.toString());
            }

            Task<Boolean> isRunningTask = ContactShield.getContactShieldEngine(getApplicationContext()).isContactShieldRunning();
            isRunningTask.addOnSuccessListener(aBoolean -> {
                if(!aBoolean){
                    new ReportOperation(getApplicationContext(), new Handler(), "submitted via teletan", false).start();
                }else{
                    new ReportOperation(getApplicationContext(), new Handler(), "submitted via teletan", true).start();
                }
            });
                //finish();
        });

        //EventListener for cancel button
        cancelButton.setOnClickListener((View v) -> finish());
    }

}