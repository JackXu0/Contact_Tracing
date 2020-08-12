package com.futurewei.contact_shield_demo.network;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;

import java.util.*;

import com.futurewei.contact_shield_demo.utils.H2GUtils;
import com.huawei.hms.contactshield.ContactDetail;
import com.huawei.hms.contactshield.PeriodicKey;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class ReportContactDetails extends NetworkTemplate {

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    public ReportContactDetails(Context context, Handler handler, List<ContactDetail> contactDetailList){
        super("report contact detail", context, handler, 7, "https://us-central1-contact-shield-demo.cloudfunctions.net/reportContactDetail");
        this.requestBody = makeRequestBody(contactDetailList);
    }

    RequestBody makeRequestBody(List<ContactDetail> contactDetailList){

        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        try {

            for(ContactDetail contactDetail : contactDetailList){
                JSONObject jo = new JSONObject();
                jo.put("dayNumber", (int) contactDetail.getDayNumber());
                jo.put("durationMinutes", (int) (contactDetail.getDurationMinutes()));
                jo.put("attenuationRiskValue", (int) contactDetail.getAttenuationRiskValue());
                jo.put("initialRiskLevel", (int) contactDetail.getInitialRiskLevel());
                jo.put("totalRiskValue", (int) contactDetail.getTotalRiskValue());
                jsonArray.put(jo);
            }

            jsonObject.put("contact_details", jsonArray);
            jsonObject.put("api_level", Build.VERSION.SDK_INT);
            jsonObject.put("android_version", Build.VERSION.RELEASE);
            jsonObject.put("brand", Build.MANUFACTURER);
            jsonObject.put("model", Build.MODEL);
            jsonObject.put("user_id", Settings.Secure.getString(context.getContentResolver(),
                    Settings.Secure.ANDROID_ID));
        }catch(Exception e){
            Log.e(TAG, e.toString());
        }

        return RequestBody.create(jsonObject.toString(), JSON);
    }
}
