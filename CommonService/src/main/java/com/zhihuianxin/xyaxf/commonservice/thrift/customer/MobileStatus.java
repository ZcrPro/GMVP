package com.zhihuianxin.xyaxf.commonservice.thrift.customer;

public enum MobileStatus {
    NotRegistered(),	// 未注册
    OK(), 			// 已注册
    BeenFrozen(),		// 已锁定
    NoPassword(), 	// 未设置密码(老用户),app端不处理此情况
}
