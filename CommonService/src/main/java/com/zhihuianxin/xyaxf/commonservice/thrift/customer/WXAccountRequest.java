package com.zhihuianxin.xyaxf.commonservice.thrift.customer;

import com.zhihuianxin.xyaxf.commonservice.thrift.user.WXAccount;

import java.io.Serializable;

public class WXAccountRequest extends AccountRequest implements Serializable {

    public WXAccountRequest(WXAccount wx_account) {
        this.wx_account = wx_account;
    }

    public WXAccount wx_account;
}
