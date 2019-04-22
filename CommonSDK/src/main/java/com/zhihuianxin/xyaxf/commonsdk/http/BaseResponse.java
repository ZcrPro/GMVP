package com.zhihuianxin.xyaxf.commonsdk.http;

import java.io.Serializable;

public class BaseResponse extends BaseMessageObject implements Serializable, Cloneable{
    public String resp_code;  // required
    public String resp_desc;  // required
}
