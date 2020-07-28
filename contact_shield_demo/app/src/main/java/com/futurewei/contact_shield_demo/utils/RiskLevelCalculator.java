package com.futurewei.contact_shield_demo.utils;

import android.util.Log;

import com.huawei.hms.contactshield.ContactDetail;

import java.util.List;

public class RiskLevelCalculator {

    static String TAG = "RiskLevelCalculator";

    public static int getRiskLevel(List<ContactDetail> contactDetails){
        // This calculator will return the maximum total risk value
        int maxTotalRiskValue = 0;
        for(ContactDetail cd : contactDetails){
            int totalRiskValue = getAttenuationRiskLevel(cd.getAttenuationRiskValue()) * getDaysAfterContactedRisklevel(cd.getDayNumber()) * getDurationRiskLevel(cd.getDurationMinutes()) * cd.getInitialRiskLevel();
            Log.e(TAG, ""+totalRiskValue);
            Log.e(TAG, ""+((int) Math.pow(totalRiskValue, 1.0/4.0)+1));
            maxTotalRiskValue = Math.max(totalRiskValue, maxTotalRiskValue);
        }

        return (int) Math.pow(maxTotalRiskValue, 1.0/4.0)+1;
    }

    static int getAttenuationRiskLevel(int attenuation){
        if(attenuation > 73)
            return 1;
        else if(attenuation > 63)
            return 2;
        else if(attenuation > 51)
            return 3;
        else if(attenuation > 33)
            return 4;
        else if(attenuation > 27)
            return 5;
        else if(attenuation > 15)
            return 6;
        else if(attenuation > 10)
            return 7;
        else
            return 8;

    }

    static int getDaysAfterContactedRisklevel(long dayNum){
        int interval = (int) (System.currentTimeMillis()/(24*60*60*1000)) - (int) dayNum;

        if(interval >= 14)
            return 1;
        else if(interval >= 12)
            return 2;
        else if(interval >= 10)
            return 3;
        else if(interval >= 8)
            return 4;
        else if(interval >= 6)
            return 5;
        else if(interval >= 4)
            return 6;
        else if(interval >= 2)
            return 7;
        else
            return 8;
    }

    static int getDurationRiskLevel(int duration){
        if(duration <= 5)
            return 2;
        else if(duration <= 10)
            return 3;
        else if(duration <= 15)
            return 4;
        else if(duration <= 20)
            return 5;
        else if(duration <= 25)
            return 6;
        else if(duration <= 30)
            return 7;
        else
            return 8;
    }


}
