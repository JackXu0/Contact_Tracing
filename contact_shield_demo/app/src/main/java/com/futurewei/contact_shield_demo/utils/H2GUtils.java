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

import java.util.Base64;

/**
 * This class converts the periodic key in contact shield SDK (HMS key) to a key in exposure notification SDK (GMS key)
 */
public class H2GUtils {
    private static final int HEX_CHAR_BYTES = 2;
    private static final int RADIX_16 = 16;
    private static final int SHIFT_4BITS = 4;
    public static String getGmsKey(byte[] data){
        return Base64.getEncoder().encodeToString(hexStringToBytes(bytesToHexString(data)));
    }
    private static String bytesToHexString(byte[] bArr) {
        StringBuffer sb = new StringBuffer(bArr.length);
        String sTmp;
        for (int i = 0; i < bArr.length; i++) {
            sTmp = Integer.toHexString(0xFF & bArr[i]);
            if (sTmp.length() < 2)
                sb.append(0);
            sb.append(sTmp.toUpperCase());
        }
        return sb.toString();
    }
    private static byte[] hexStringToBytes(String hexString) {
        int hex2ByteLen = hexString.length() / HEX_CHAR_BYTES;
        byte[] bytes = new byte[hex2ByteLen];
        for (int index = 0; index < hex2ByteLen; index++) {
            bytes[index] = (byte) ((Character.digit(hexString.charAt(index * HEX_CHAR_BYTES), RADIX_16) << SHIFT_4BITS)
                    + Character.digit(hexString.charAt(index * HEX_CHAR_BYTES + 1), RADIX_16));
        }
        return bytes;
    }

}
