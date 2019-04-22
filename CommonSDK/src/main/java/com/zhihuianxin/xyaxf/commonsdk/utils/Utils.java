/*
 * Copyright 2018 JessYan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zhihuianxin.xyaxf.commonsdk.utils;

import android.app.Activity;
import android.content.Context;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.util.Base64;

import com.alibaba.android.arouter.launcher.ARouter;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * ================================================
 * Created by JessYan on 30/03/2018 17:16
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * ================================================
 *
 * // 构建标准的路由请求
 * ARouter.getInstance().build("/home/main").navigation();
 *
 * // 构建标准的路由请求，并指定分组
 * ARouter.getInstance().build("/home/main", "ap").navigation();
 *
 * // 构建标准的路由请求，通过Uri直接解析
 * Uri uri;
 * ARouter.getInstance().build(uri).navigation();
 *
 * // 构建标准的路由请求，startActivityForResult
 * // navigation的第一个参数必须是Activity，第二个参数则是RequestCode
 * ARouter.getInstance().build("/home/main", "ap").navigation(this, 5);
 *
 * // 直接传递Bundle
 * Bundle params = new Bundle();
 * ARouter.getInstance()
 *     .build("/home/main")
 *     .with(params)
 *     .navigation();
 *
 * // 指定Flag
 * ARouter.getInstance()
 *     .build("/home/main")
 *     .withFlags();
 *     .navigation();
 *
 * // 获取Fragment
 * Fragment fragment = (Fragment) ARouter.getInstance().build("/test/fragment").navigation();
 *
 * // 对象传递
 * ARouter.getInstance()
 *     .withObject("key", new TestObj("Jack", "Rose"))
 *     .navigation();
 *
 * // 觉得接口不够多，可以直接拿出Bundle赋值
 * ARouter.getInstance()
 *         .build("/home/main")
 *         .getExtra();
 *
 * // 转场动画(常规方式)
 * ARouter.getInstance()
 *     .build("/test/activity2")
 *     .withTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom)
 *     .navigation(this);
 *
 * // 转场动画(API16+)
 * ActivityOptionsCompat compat = ActivityOptionsCompat.
 *     makeScaleUpAnimation(v, v.getWidth() / 2, v.getHeight() / 2, 0, 0);
 *
 * // ps. makeSceneTransitionAnimation 使用共享元素的时候，需要在navigation方法中传入当前Activity
 *
 * ARouter.getInstance()
 *     .build("/test/activity2")
 *     .withOptionsCompat(compat)
 *     .navigation();
 *
 * // 使用绿色通道(跳过所有的拦截器)
 * ARouter.getInstance().build("/home/main").greenChannel().navigation();
 *
 * // 使用自己的日志工具打印日志
 * ARouter.setLogger();
 *
 * // 使用自己提供的线程池
 * ARouter.setExecutor();
 */
public class Utils {
    private Utils() {
        throw new IllegalStateException("you can't instantiate me!");
    }

    /**
     * 使用 {@link ARouter} 根据 {@code path} 跳转到对应的页面, 这个方法因为没有使用 {@link Activity}跳转
     * 所以 {@link ARouter} 会自动给 {@link android.content.Intent} 加上 Intent.FLAG_ACTIVITY_NEW_TASK
     * 如果不想自动加上这个 Flag 请使用 {@link ARouter#getInstance()#navigation(Context, String)} 并传入 {@link Activity}
     *
     * @param path {@code path}
     */
    public static void navigation(String path) {
        ARouter.getInstance().build(path).navigation();
    }

    /**
     * 使用 {@link ARouter} 根据 {@code path} 跳转到对应的页面, 如果参数 {@code context} 传入的不是 {@link Activity}
     * {@link ARouter} 就会自动给 {@link android.content.Intent} 加上 Intent.FLAG_ACTIVITY_NEW_TASK
     * 如果不想自动加上这个 Flag 请使用 {@link Activity} 作为 {@code context} 传入
     *
     * @param context
     * @param path
     */
    public static void navigation(Context context, String path) {
        ARouter.getInstance().build(path).navigation(context);
    }

    public static void navigation(Context context, String path,String key,String data) {
        ARouter.getInstance().build(path).withString(key,data).navigation(context);
    }

    public static void navigation(Context context, String path,String key,Object data) {
        ARouter.getInstance().build(path).withObject(key,data).navigation(context);
    }

    public static void navigation(Context context, String path, String key, ArrayList<String> data) {
        ARouter.getInstance().build(path).withStringArrayList(key,data).navigation(context);
    }

    public static String arrrylistStringSerialization(ArrayList<String> arrayList){
        StringBuilder builder=new StringBuilder();
        for(int i=0;i<arrayList.size();++i){
            builder.append(arrayList.get(i)+"&&&");
        }
        return builder.toString();
    }

    public static ArrayList<String> arraylistStringUnserialization(String s){
        ArrayList<String> arrayList=new ArrayList<>();
        for(String str:s.split("&&&")){
            arrayList.add(str);
        }
        return arrayList;
    }

    /**
     * 判断手机号是否符合规范
     * @param phoneNo 输入的手机号
     * @return
     */
    public static boolean isPhoneNumber(String phoneNo) {
        if (TextUtils.isEmpty(phoneNo)) {
            return false;
        }
        if (phoneNo.length() == 11) {
            for (int i = 0; i < 11; i++) {
                if (!PhoneNumberUtils.isISODigit(phoneNo.charAt(i))) {
                    return false;
                }
            }
            Pattern p = Pattern.compile("^((13[^4,\\D])" + "|(134[^9,\\D])" +
                    "|(14[5,7])" +
                    "|(15[^4,\\D])" +
                    "|(17[3,6-8])" +
                    "|(18[0-9]))\\d{8}$");
            Matcher m = p.matcher(phoneNo);
            return m.matches();
        }
        return false;
    }

    public static String readFile(String path, String encoding) throws IOException {
        InputStream is = getFileStream(path);
        String result = readStreamString(is, encoding);
        is.close();

        return result;
    }

    public static InputStream getFileStream(String path) throws IOException {
        FileInputStream fis = new FileInputStream(path);
        return fis;
    }

    public static String readStreamString(InputStream is, String encoding) throws IOException {
        return new String(readStream(is), encoding);
    }

    public static byte[] readStream(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buf = new byte[1024 * 10];
        int readlen;
        while ((readlen = is.read(buf)) >= 0) {
            baos.write(buf, 0, readlen);
        }

        baos.close();

        return baos.toByteArray();
    }

    public static boolean isEnabled(String value) {
        return !isEmpty(value);
    }

    public static boolean isEmpty(String text) {
        return text == null || text.trim().length() == 0;
    }

    public static byte[] getUtf8Bytes(String str) {
        try {
            return str.getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            return new byte[0];
        }
    }

    public static String Decrypt(String src, String key) throws Exception {
        try {
            // 判断Key是否正确
            if (key == null) {
                System.out.print("Key为空null");
                return null;
            }

            // 密钥补位
            int plus = 16 - key.length();
            byte[] data = key.getBytes("utf-8");
            byte[] raw = new byte[16];
            byte[] plusbyte = {0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f};
            for (int i = 0; i < 16; i++) {
                if (data.length > i)
                    raw[i] = data[i];
                else
                    raw[i] = plusbyte[plus];
            }

            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);

            byte[] encrypted1 = Base64.decode(src.getBytes("UTF-8"),Base64.NO_WRAP);//base64
//            byte[] encrypted1 =src.getBytes("UTF-8");//十六进制
            try {
                byte[] original = cipher.doFinal(encrypted1);
                String originalString = new String(original, "utf-8");
                return originalString;
            } catch (Exception e) {
                System.out.println(e.toString());
                return null;
            }
        } catch (Exception ex) {
            System.out.println(ex.toString());
            return null;
        }
    }

}
