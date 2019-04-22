package com.zhihuianxin.xyaxf.commonservice.thrift.customer;

import com.zhihuianxin.xyaxf.commonservice.thrift.common.Gender;

public class CustomerBaseInfo {
    public String regist_serial;        // 注册序列号
    public String mobile;                // 手机号
    public String nickname;                // 昵称
    public Gender gender = Gender.Male;        // 性别
    public String avatar;                // 头像
    public String name;                    // 用户姓名
}
