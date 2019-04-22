package com.zhihuianxin.xyaxf.commonsdk.converter;

import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.zhihuianxin.xyaxf.commonsdk.BuildConfig;
import com.zhihuianxin.xyaxf.commonsdk.app.utils.AppConstant;
import com.zhihuianxin.xyaxf.commonsdk.app.utils.NetUtils;
import com.zhihuianxin.xyaxf.commonsdk.http.ApiException;
import com.zhihuianxin.xyaxf.commonsdk.mvp.model.api.Api;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
 * create by shimao at 2019/04/02
 * 安心付retrofit2 接口返回数据转换器，
 */
final class AXFResponseBodyConverter<T> implements Converter<ResponseBody, T> {
    private final Type mType;
    private final Gson mGson;

    AXFResponseBodyConverter(Type type, Gson gson) {
        mType = type;
        mGson = gson;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T convert(ResponseBody body) throws IOException {
        Class<T> proxyClass = (Class<T>) mType;
        String responseStr = body.string();
        if (TextUtils.isEmpty(responseStr))
            return null;
        //解密检查
        responseStr = decryptDataAndCheck(responseStr);
        //解析数据
        if (proxyClass.isInterface()) {//动态代理接口模式转换
            try {
                JSONObject jsonObject = new JSONObject(responseStr);
                if (BuildConfig.AnXinDEBUG) {
                    Log.i("HttpLog", "json=" + jsonObject.toString());
                }
                JSONObject resJosn = jsonObject.getJSONObject("resp");
                String resCode = resJosn.getString("resp_code");
                String resDesc = resJosn.getString("resp_desc");
                if (resCode.equals(AppConstant.SESSION_OUT_OF_TIME) ||
                        resCode.equals(AppConstant.LOGIN_IN_OTHER_DEVICE) ||
                        resCode.equals(AppConstant.INVALID_SESSION) ||
                        resCode.equals(AppConstant.SCHOOL_REQUIRED) ||
                        resCode.equals(AppConstant.SESSION_ERROR_ID_NULL) ||
                        resCode.equals(AppConstant.SESSION_ERROR) ||
                        resCode.equals(AppConstant.SESSION_SETTING_ERROR)
                ) {
                    throw new ApiException(AppConstant.RELOGIN, "需要重新登录(" + resDesc + "_" + resCode + ")");
                }
                Method[] methods = proxyClass.getMethods();
                DataField dataField;
                Map<String, Object> objectMap = new HashMap<>();
                String keyName;
                for (int i = 0; i < methods.length; i++) {
                    dataField = methods[i].getAnnotation(DataField.class);
                    if (dataField == null || !checkMethod(methods[i]))
                        continue;
                    keyName = TextUtils.isEmpty(dataField.value()) ? methods[i].getName() :
                            dataField.value();
                    if (jsonObject.has(keyName)) {
                        Object object = parseValueToObject(jsonObject.getString(keyName),
                                methods[i]);
                        if (object != null)
                            objectMap.put(keyName, object);
                    }
                }
                return (T) Proxy.newProxyInstance(proxyClass.getClassLoader(),
                        new Class<?>[]{proxyClass},
                        new ResponseResultInvoker(objectMap));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        } else if (proxyClass != String.class) {//普通JSON实体转换
            TypeAdapter<T> adapter = mGson.getAdapter(TypeToken.get(proxyClass));
            return adapter.fromJson(responseStr);
        } else//返回字符串
            return (T) responseStr;
    }

    /**
     * 校验方法代理模式下。代理接口的方法规范性
     *
     * @param method
     * @return
     */
    private boolean checkMethod(Method method) {
        if (method.getReturnType() != Void.TYPE && method.getReturnType() != Void.class)
            return true;
        else
            return false;
    }

    private Object parseValueToObject(String value, Method method) {
        if (String.class == method.getReturnType()) {
            return value;
        } else {
            try {
                return mGson.fromJson(value, method.getGenericReturnType());
            } catch (Exception e) {
                return null;
            }
        }
    }

    /**
     * 解密並检查，返回json数据字符串
     *
     * @param responseStr
     * @return
     */
    private String decryptDataAndCheck(String responseStr) {
        String[] params = responseStr.split("&");
        if (params.length < 2) {
            throw new RuntimeException("签名或json数据未找到");
        }
        String json = null, sign = null;
        for (String s : params) {
            String[] kv = s.split("=");
            if (kv.length != 2) {
                throw new RuntimeException("返回数据格式不正确");
            }
            String value;
            try {
                value = URLDecoder.decode(kv[1], "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException("Can not decode value with UTF-8");
            }
            if (kv[0].equals("json")) {
                json = value;
            } else if (kv[0].equals("sign")) {
                sign = value;
            }
        }

        boolean signatureIsOK = sign != null && sign.equals(NetUtils.getSign(json));

        if (!signatureIsOK) {
            throw new RuntimeException("签名错误");
        }
        if (BuildConfig.AnXinDEBUG) {
            Log.i("HttpLog", "sign=" + sign);
        }

        return json;
    }

    private void handleCommonResponseException(String resCode, String resDesc) {
         if (resCode.equals(AppConstant.SESSION_OUT_OF_TIME) ||
                resCode.equals(AppConstant.LOGIN_IN_OTHER_DEVICE) ||
                resCode.equals(AppConstant.INVALID_SESSION) ||
                resCode.equals(AppConstant.SCHOOL_REQUIRED) ||
                resCode.equals(AppConstant.SESSION_ERROR_ID_NULL) ||
                resCode.equals(AppConstant.SESSION_ERROR) ||
                resCode.equals(AppConstant.SESSION_SETTING_ERROR)
        ) {
            throw new ApiException(AppConstant.RELOGIN, "需要重新登录(" + resDesc + "_" + resCode + ")");
        }
    }

    /**
     * 动态代理对象类
     */
    private static class ResponseResultInvoker implements InvocationHandler {
        private final Map<String, Object> values;

        public ResponseResultInvoker(Map<String, Object> values) {
            this.values = values;
        }

        @Override
        public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
            if (method.getReturnType() == Void.TYPE || method.getReturnType() == Void.class)
                return null;
            DataField dataField = method.getAnnotation(DataField.class);
            if (dataField == null) {
                return getDefaultValue(null, method.getReturnType());
            }
            String keyName = TextUtils.isEmpty(dataField.value()) ? method.getName() :
                    dataField.value();
            return getDefaultValue(values.get(keyName), method.getReturnType());
        }

        private static Object getDefaultValue(Object value, Class<?> returnType) {
            if (value != null) {
                return value;
            } else if (returnType == Boolean.class || returnType == Boolean.TYPE) {
                return false;
            } else if (returnType == Integer.class || returnType == Integer.TYPE) {
                return 0;
            } else if (returnType == Long.class || returnType == Long.TYPE) {
                return 0L;
            } else if (returnType == Short.class || returnType == Short.TYPE) {
                return (short) 0;
            } else if (returnType == Float.class || returnType == Float.TYPE || returnType == Double.class || returnType == Double.TYPE) {
                return 0f;
            } else if (returnType == Byte.class || returnType == Byte.TYPE) {
                return (byte) 0;
            } else
                return null;
        }
    }
}
