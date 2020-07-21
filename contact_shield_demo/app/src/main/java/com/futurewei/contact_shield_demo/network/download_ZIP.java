package com.futurewei.contact_shield_demo.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.contactshield.ContactShield;
import com.huawei.hms.contactshield.ContactSketch;
import com.huawei.hms.contactshield.DiagnosisConfiguration;
import com.huawei.hms.contactshield.PeriodicKey;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;
import okio.Okio;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import java.nio.file.Path;

import static android.content.Context.MODE_PRIVATE;

public class download_ZIP extends Thread {
    private static final String TAG = "download ZIP";

    String token = "3bdd528fd98947bcaffa0d8fda68ca54";
    Context context;
    String projectId = "contact-tracing-demo-281120";
    String bucketName = "zip001_futurewei";
    String objectName = "551411c533835562.zip";

    // The path to which the file should be downloaded
    Path destFilePath = Paths.get(Environment.getExternalStorageDirectory()+"/Downloads/periodic_key.zip");

    public download_ZIP(Context context, String user_id){
        this.context = context;
        objectName = user_id+".zip";
    }

    @Override
    public void run() {

        try{
            Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();

            Blob blob = storage.get(BlobId.of(bucketName, objectName));
            blob.downloadTo(destFilePath);
        }finally {

        }


    }

    void putSharedKey(List<PeriodicKey> sharedKeys){
        File file = new File(destFilePath.toString());
        List<File> file_list = new ArrayList<>();
        file_list.add(file);
        DiagnosisConfiguration config = new DiagnosisConfiguration.Builder()
                .build();

        Task<Void> task = ContactShield.getContactShieldEngine(context).putSharedKeyFiles(file_list, config, token);
        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.e(TAG, "put key success");
                getContactSketch();
            }
        });


    }

    void getContactSketch(){
        Task<ContactSketch> contactSketchTask = ContactShield.getContactShieldEngine(context).getContactSketch(token);
        contactSketchTask.addOnSuccessListener(new OnSuccessListener<ContactSketch>() {
            @Override
            public void onSuccess(ContactSketch contactSketch) {
                Log.e(TAG, "sketch"+contactSketch.toString());
            }
        });
    }
}
