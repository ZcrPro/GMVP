package com.zhihuianxin.xyaxf.commonsdk.app.utils;

import android.content.Context;
import android.os.Build;
import android.os.UserManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.jess.arms.base.App;
import com.zhihuianxin.xyaxf.commonsdk.app.AppLifecyclesImpl;
import com.zhihuianxin.xyaxf.commonsdk.http.BaseMessageObject;
import com.zhihuianxin.xyaxf.commonsdk.http.BaseRequest;
import com.zhihuianxin.xyaxf.commonsdk.http.BaseResponse;


public class MessageFactory {
	private static final String TAG = "MessageFactory";

	public String toStringMessage(BaseMessageObject obj){
		Gson gson = new Gson();
		String json = gson.toJson(obj);
		return json;
	}

	public <T extends BaseMessageObject> T create(Context context, Class<T> clazz){
		try {
			BaseMessageObject msg = clazz.newInstance();
			if(msg instanceof BaseRequest){
				initBaseRequest(context, (BaseRequest)msg);
			}
			if(msg instanceof BaseResponse){
				initBaseResponse((BaseResponse)msg);
			}
			return (T)msg;
		} catch (Exception e) {
			Log.e(TAG, String.format("Create msg: %s failed", clazz), e);
			return null;
		}
	}

	protected void initBaseRequest(Context context, BaseRequest baseRequest){
		baseRequest.app_name = context.getPackageName();
		baseRequest.app_version = HttpUtils.getPackageInfo(context).versionName;
		baseRequest.sys_name = AppConstant.SYSTEM_NAME.name();
		baseRequest.sys_version = Build.VERSION.RELEASE;
		baseRequest.session_id = AppLifecyclesImpl.mSession.getSession();
	}

	protected void initBaseResponse(BaseResponse baseResponse){
		baseResponse.resp_code = AppConstant.SUCCESS;
		baseResponse.resp_desc = "成功";
	}

	public BaseRequest createBaseRequest(Context context){
		BaseRequest req = create(context, BaseRequest.class);
		return req;
	}
}
