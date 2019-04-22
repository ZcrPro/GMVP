package com.zhihuianxin.xyaxf.commonsdk.app.data;


import com.cocosw.favor.AllFavor;
import com.cocosw.favor.Default;

@AllFavor
public interface IAXLogin {
    // UUID
    @Default("")
    String getUUID();
    void setUUID(String uuid);
}
