package com.futurewei.contact_shield_demo.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.futurewei.contact_shield_demo.R;
import com.futurewei.contact_shield_demo.network.GetRegistrationKeyQRCode;
import com.futurewei.contact_shield_demo.handlers.UploadHandler;
import com.huawei.hms.hmsscankit.ScanUtil;
import com.huawei.hms.ml.scan.HmsScan;
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions;
import com.huawei.hms.ml.scan.HmsScanBase;

import java.util.regex.Pattern;

public class SubmitViaGuidActivity extends Activity {

    private static final String TAG = "submit_via_guid_activity";
    public static final int DEFAULT_VIEW = 0x22;
    private static final int REQUEST_CODE_SCAN = 0X01;
    Context context;
    boolean scanned = false;

    ProgressBar progressBar;
    Handler handler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.submit_via_guid_activity);

        context = this;

        initView();

        handler = new UploadHandler(context, TAG, progressBar);

        beginScanning();
    }

    void initView(){
        progressBar = findViewById(R.id.progress_bar);
    }

    void beginScanning(){
        ActivityCompat.requestPermissions(
                SubmitViaGuidActivity.this,
                new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE},
                DEFAULT_VIEW);

        // After the permissions are applied for, call the barcode scanning view in Default View mode.
        ScanUtil.startScan(SubmitViaGuidActivity.this, REQUEST_CODE_SCAN, new HmsScanAnalyzerOptions.Creator().setHmsScanTypes(HmsScanBase.ALL_SCAN_TYPE).create());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (permissions == null || grantResults == null || grantResults.length < 2 || grantResults[0] != PackageManager.PERMISSION_GRANTED || grantResults[1] != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        if (requestCode == DEFAULT_VIEW) {
            // Call the barcode scanning view in Default View mode.
            ScanUtil.startScan(SubmitViaGuidActivity.this, REQUEST_CODE_SCAN, new HmsScanAnalyzerOptions.Creator().setHmsScanTypes(HmsScanBase.ALL_SCAN_TYPE).create());

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //receive result after your activity finished scanning
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null || scanned) {
            return;
        }
        // Obtain the return value of HmsScan from the value returned by the onActivityResult method by using ScanUtil.RESULT as the key value.
        if (requestCode == REQUEST_CODE_SCAN) {
            Object obj = data.getParcelableExtra(ScanUtil.RESULT);
            if (obj instanceof HmsScan) {
                if (!TextUtils.isEmpty(((HmsScan) obj).getOriginalValue())) {
                    String guid = ((HmsScan) obj).getOriginalValue();
                    if(Pattern.matches("[a-zA-Z0-9]{32}", guid)){
                        progressBar.setVisibility(View.VISIBLE);
                        scanned = true;
                        new GetRegistrationKeyQRCode(getApplicationContext(), handler, guid).start();

                    }
                }
            }
        }

    }



}
