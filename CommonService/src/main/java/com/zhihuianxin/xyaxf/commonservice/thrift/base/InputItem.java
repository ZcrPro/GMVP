package com.zhihuianxin.xyaxf.commonservice.thrift.base;

public class InputItem {

    public String name;           // 字段名，key
    public boolean trim = false;   // 是否忽略首尾不可见字符(空格等)
    public String title;       // 信息标题
    public String hint;           // 信息输入提示
    public String type;       // 信息输入类型
    public String reg_exp;       // 检测内容的正则表达式
    public int max_length;       // 最大内容长度
}
