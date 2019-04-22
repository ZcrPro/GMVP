package com.zhihuianxin.xyaxf.commonsdk.converter;


import com.zhihuianxin.xyaxf.commonsdk.http.BaseResponse;

public interface ResponseResult {
    @DataField("resp")
    BaseResponse getCode();
}
