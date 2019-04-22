package com.zhihuianxin.xyaxf.commonsdk.app.data;

import com.cocosw.favor.AllFavor;
import com.cocosw.favor.Default;

@AllFavor
public interface ISession {
    @Default("")
    String getSession();
    void setSession(String session);
}
