package com.zhihuianxin.xyaxf.commonsdk.app.utils;

import android.content.Context;

import com.google.gson.Gson;
import com.zhihuianxin.secure.SecureLocal;

import java.util.Map;

/**
 * Created by Vincent on 2016/11/2.
 */

public class NetUtils {
    private static final String TAG = "NetUtils";

    public static String getRequestParams(Context context, Map<String, Object> paramMap) {
        paramMap.put("req", new MessageFactory().createBaseRequest(context));
        return new Gson().toJson(paramMap);
    }

    public static String getSign(String json) {
        String sign = "";
        try {
            sign = byte2HexStr(SecureLocal.signMessage((json+ "dev_029014c7257d49bbb337f42efce2f295").getBytes("UTF-8"))); //测试线上数据用这个
//            sign = HttpUtils.byte2HexStr(Secure.signMessage((json).getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sign;
    }

    public static String byte2HexStr(byte[] data) {
        if (data == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();

        for (byte b : data) {
            String bHexStr = Integer.toHexString(0x00ff & b);
            if (bHexStr.length() == 1) {
                sb.append('0');
            }

            sb.append(bHexStr);

        }

        return sb.toString();
    }
}
