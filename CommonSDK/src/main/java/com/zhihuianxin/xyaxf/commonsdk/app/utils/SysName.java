package com.zhihuianxin.xyaxf.commonsdk.app.utils;

public enum SysName {
	Android(),
	iOS(),

	;

	static{
		for(SysName e: values()){
			if(!e.manual && e.ordinal() > 0){
				e.code = values()[e.ordinal() - 1].code + 1;
			}
		}
	}

	private int code = 0;
	private boolean manual = false;
	private SysName(int code){
		this.code = code;
		this.manual = true;
	}

	private SysName(){
	}

	public int getCode(){
		return code;
	}
}
