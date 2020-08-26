/**
 * Copyright © 2020  Futurewei Technologies, Inc. All rights reserved.
 *
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *
 *
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *
 *
 * Unless required by applicable law or agreed to in writing, software
 *
 * distributed under the License is distributed on an "AS IS" BASIS,
 *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the License for the specific language governing permissions and
 *
 * limitations under the License.
 */

package com.futurewei.contact_shield_demo.utils;

import android.util.Log;

import com.huawei.hms.contactshield.ContactDetail;

import java.util.List;

/**
 * This class first converts all risk values to risk level and then picks the maximum risk level
 * It follows formula: risk level = Math.pow(risk value - 1) + 1.
 * Risk value has value range [1,4096], thus risk level will have value range [1,8]
 */
public class RiskLevelCalculator {

    static String TAG = "RiskLevelCalculator";

    public static int getRiskLevel(List<ContactDetail> contactDetails){
        int maxTotalRiskValue = 0;
        for(ContactDetail cd : contactDetails){
            int totalRiskValue = cd.getTotalRiskValue()-1;
            Log.e(TAG, ""+totalRiskValue);
            Log.e(TAG, ""+((int) Math.pow(totalRiskValue, 1.0/4.0)+1));
            maxTotalRiskValue = Math.max(totalRiskValue, maxTotalRiskValue);
        }

        return (int) Math.pow(maxTotalRiskValue, 1.0/4.0)+1;
    }
}
