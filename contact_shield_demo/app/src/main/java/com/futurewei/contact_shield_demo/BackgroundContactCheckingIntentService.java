package com.futurewei.contact_shield_demo;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.FileDescriptor;

public class BackgroundContactCheckingIntentService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e("222", "222");
        return null;
    }
}
