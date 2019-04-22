package com.zhihuianxin.xyaxf.commonsdk.app.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public class HttpUtils {
    public static final String[] HTTP_URL = {"http://"};
    public static final String HTTPS_PREFIX = "https://";

    public static boolean isEmpty(String text) {
        return text == null || text.trim().length() == 0;
    }

    public static PackageInfo getPackageInfo(Context context) {
        return getPackageInfo(context, context.getPackageName());
    }

    public static PackageInfo getPackageInfo(Context context, String packageName) {
        PackageManager manager = context.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(packageName, 0);
            return info;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String byte2HexStr(byte[] data){
        if(data == null){
            return "";
        }

        StringBuilder sb = new StringBuilder();

        for(byte b: data){
            String bHexStr = Integer.toHexString(0x00ff & b);
            if(bHexStr.length() == 1){
                sb.append('0');
            }

            sb.append(bHexStr);

        }

        return sb.toString();
    }
}
