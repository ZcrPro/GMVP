package com.zhihuianxin.xyaxf.commonservice.thrift.customer;

public class MobileAccountRequest extends AccountRequest {

    public MobileAccountRequest(String mobile, String security_code, String password) {
        this.mobile = mobile;
        this.security_code = security_code;
        this.password = password;
    }

    public String mobile;
    public String security_code;
    public String password;
}
