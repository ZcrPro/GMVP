package com.zhihuianxin.xyaxf.commonsdk.converter;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonWriter;
import com.zhihuianxin.xyaxf.commonsdk.app.utils.MessageFactory;
import com.zhihuianxin.xyaxf.commonsdk.app.utils.NetUtils;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import retrofit2.Converter;
/**
 * create by shimao at 2019/04/02
 * 安心付retrofit2 接口请求数据转换器，
 */
final class AXFRequestBodyConverter<T> implements Converter<T, RequestBody> {
    private static final MediaType MEDIA_TYPE_FORM_URLENCODED = MediaType.get("application/x-www-form-urlencoded; charset=UTF-8");
    private static final MediaType MEDIA_TYPE_JSON = MediaType.get("application/json; charset=UTF-8");
    private static final Charset UTF_8 = Charset.forName("UTF-8");

    private final Gson gson;
    private final TypeAdapter<T> adapter;
    private final Context mContext;

    public AXFRequestBodyConverter(Gson gson, TypeAdapter<T> adapter, Context context) {
        this.gson = gson;
        this.adapter = adapter;
        mContext = context;
    }


    @Override
    public RequestBody convert(T value) throws IOException {
        if(value instanceof Map){
            StringBuilder stringBuffer=new StringBuilder();
            Map<String,Object> map= (Map<String, Object>) value;
            map.put("req", new MessageFactory().createBaseRequest(mContext));
            String json=gson.toJson(map);
            stringBuffer.append("json=")
                    .append(json)
                    .append("&sign=")
                    .append(NetUtils.getSign(json));
            return FormBody.create(MEDIA_TYPE_FORM_URLENCODED,stringBuffer.toString());

        }else{
            Buffer buffer = new Buffer();
            Writer writer = new OutputStreamWriter(buffer.outputStream(), UTF_8);
            JsonWriter jsonWriter = gson.newJsonWriter(writer);
            adapter.write(jsonWriter, value);
            jsonWriter.close();
            return RequestBody.create(MEDIA_TYPE_JSON, buffer.readByteString());
        }
    }
}