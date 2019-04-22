package com.zhihuianxin.xyaxf.commonservice.thrift.customer;

import com.zhihuianxin.xyaxf.commonservice.thrift.user.Realname;
import com.zhihuianxin.xyaxf.commonservice.thrift.user.School;
import com.zhihuianxin.xyaxf.commonservice.thrift.user.StudentInfo;
import com.zhihuianxin.xyaxf.commonservice.thrift.user.WXAccount;

public class Customer {
    public CustomerBaseInfo base_info;    // 基本用户信息
    public School school;                // 学校
    public boolean is_could_cancel;        // 是否可以注销
    public boolean is_show_bind_card_guide;// 是否显示绑卡引导
    public String bind_card_short_hint;    // 绑卡引导短提示，先注释掉，不全删，后面可能会有，待定
    public String bind_card_long_hint;        // 绑卡引导长提示，先注释掉，不全删，后面可能会有，待定
    // 身份中心相关
    public WXAccount wx_account;            // 微信账号信息
    public StudentInfo student;            // 学生信息
    public Realname realname;                // 实名信息
}
