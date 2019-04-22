package com.zhihuianxin.xyaxf.commonservice.thrift.user;

import java.io.Serializable;

public class School implements Serializable {
    public String code;             // 学校编号
    public String name;             // 学校名称
    public String logo;             // logo
    public String landmark_icon;
    public String pinyin_header;
    public String land_mark_image;
    public String quanpin;          // 全拼
    public String city_code;        // 城市编号
    public String city_name;		// 城市名称
    public String province_name;	// 省份名称
}
