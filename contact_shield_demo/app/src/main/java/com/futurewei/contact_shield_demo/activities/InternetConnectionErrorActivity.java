package com.futurewei.contact_shield_demo.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.futurewei.contact_shield_demo.R;

/**
 * This Activity generally alerts user that the internet connection is off.
 * It is trigger when the onFailure callback of a network request is executed
 *
 */
public class InternetConnectionErrorActivity extends Activity {

    Button retryButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_internet_connection_error);

        initView();
    }

    /**
     * This method initialize the view and set the on click listener for the retry button
     * Once the user has clicked retry button, he will go back to the MainActivity
     *
     */
    void initView(){
        retryButton = (Button) findViewById(R.id.retryButton);
        retryButton.setOnClickListener((View v) -> {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
        });
    }
}