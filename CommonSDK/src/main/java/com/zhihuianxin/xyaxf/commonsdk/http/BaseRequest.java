package com.zhihuianxin.xyaxf.commonsdk.http;

import java.io.Serializable;

public class BaseRequest extends BaseMessageObject implements Serializable, Cloneable{
	public String sys_name ;  // required
	public String sys_version ;  // required
	public String app_name ;  // required
	public String app_version ;  // required
	public String session_id ;  // optional
	public String channel ;  // required
	public String timestamp;
}