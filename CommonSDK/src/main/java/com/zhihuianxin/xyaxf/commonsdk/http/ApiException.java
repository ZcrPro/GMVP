package com.zhihuianxin.xyaxf.commonsdk.http;

/**
 * Created by Vincent on 2016/12/9.
 */

public class ApiException extends RuntimeException {
    public int mErrorCode;

    public ApiException(int errorCode, String errorMessage) {
        super(errorMessage);
        mErrorCode = errorCode;
    }
}

