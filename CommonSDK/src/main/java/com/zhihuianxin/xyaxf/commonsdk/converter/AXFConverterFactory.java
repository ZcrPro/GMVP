package com.zhihuianxin.xyaxf.commonsdk.converter;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * create by shimao at 2019/04/02
 * 安心付retrofit2 接口数据转工厂
 */
public class AXFConverterFactory extends Converter.Factory {
    private final Gson mGson;
    private final Context mContext;
    public static AXFConverterFactory create(Context context) {
        return create(context,new Gson());
    }

    public static AXFConverterFactory create(Context context,Gson gson) {
        if (gson == null||context==null) throw new NullPointerException("gson == null");

        return new AXFConverterFactory(context,gson);
    }

    public AXFConverterFactory(Context context,Gson gson) {
        mContext=context;
        mGson = gson;
    }

    /**
     * 接口返回数据转换器
     * @param type
     * @param annotations
     * @param retrofit
     * @return
     */
    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations,
                                                            Retrofit retrofit) {
        return new AXFResponseBodyConverter<>(type, mGson);
    }

    /**
     * 数据转换成网络请求类
     * @param type
     * @param parameterAnnotations
     * @param methodAnnotations
     * @param retrofit
     * @return
     */
    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type,
                                                          Annotation[] parameterAnnotations,
                                                          Annotation[] methodAnnotations,
                                                          Retrofit retrofit) {
        TypeAdapter<?> adapter = mGson.getAdapter(TypeToken.get(type));
        return new AXFRequestBodyConverter<>(mGson, adapter,mContext);
    }
}
