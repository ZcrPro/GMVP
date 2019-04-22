package com.zhihuianxin.xyaxf.commonservice.thrift.secure_code;


public enum VerifyFor {
	ResetPassword(),
	ChangeMobile(),
	Register(),
	ClearPayPassword()
	;

	static{
		for(VerifyFor e: values()){
			if(!e.manual && e.ordinal() > 0){
				e.code = values()[e.ordinal() - 1].code + 1;
			}
		}
	}

	private int code = 0;
	private boolean manual = false;
	private VerifyFor(int code){
		this.code = code;
		this.manual = true;
	}

	private VerifyFor(){
	}

	public int getCode(){
		return code;
	}
}
