package com.zhihuianxin.xyaxf.commonservice.thrift.customer;

import java.io.Serializable;

public class StudentAccountRequest extends AccountRequest implements Serializable {

    public StudentAccountRequest(String school_code, String account_no) {
        this.school_code = school_code;
        this.account_no = account_no;
    }

    public String school_code;
    public String account_no;
}