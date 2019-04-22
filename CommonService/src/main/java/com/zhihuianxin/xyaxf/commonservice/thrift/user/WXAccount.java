package com.zhihuianxin.xyaxf.commonservice.thrift.user;

import com.zhihuianxin.xyaxf.commonservice.thrift.common.Gender;

import java.io.Serializable;

public class WXAccount implements Serializable {

    public WXAccount(String open_id, String union_id, String nickname, String avatar, Gender gender, String country, String province, String city) {
        this.open_id = open_id;
        this.union_id = union_id;
        this.nickname = nickname;
        this.avatar = avatar;
        this.gender = gender;
        this.country = country;
        this.province = province;
        this.city = city;
    }

    public String open_id;
    public String union_id;
    public String nickname;
    public String avatar;
    public Gender gender;
    public String country;
    public String province;
    public String city;
}
