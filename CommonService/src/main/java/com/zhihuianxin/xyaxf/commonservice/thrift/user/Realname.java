package com.zhihuianxin.xyaxf.commonservice.thrift.user;

public class Realname {
    private String name;			// 姓名, 隐藏一个字, *x / x*x
    private String id_card_no;		// 身份证号, 只显示后4位, **************xxxx
    private RealNameAuthStatus status = RealNameAuthStatus.NotAuth;
}
