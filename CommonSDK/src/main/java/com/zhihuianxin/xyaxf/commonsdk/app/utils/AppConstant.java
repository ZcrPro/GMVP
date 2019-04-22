package com.zhihuianxin.xyaxf.commonsdk.app.utils;


import com.zhihuianxin.xyaxf.commonsdk.mvp.model.api.Api;

public class AppConstant {
    // axinfu-v2-devel.zhihuianxin.com
    // https://appserver-v2-test.zhihuianxin.com 银联二维码测试环境
    // https://axinfu-v2-online.zhihuianxin.com/app-service/2.0.3/
    // https://appserver-preview.axinfu.com/app-service/2.0.4/
    public static final String URL = Api.APP_DOMAIN;

    public static final int DEFAULT_COUNT = 120000;

    public static final SysName SYSTEM_NAME = SysName.Android;
    public static final int PAGE_SIZE = 20;

    public static final int SIGNATURE_OR_JSON_NOT_FOUND = 9001;
    public static final int ILLEGAL_FORMAT_RESPONSE = 9002;
    public static final int CAN_DECODE_VALUE_UTF8 = 9003;
    public static final int SIGNATURE_ERROR = 9004;
    public static final int INIT_JSON_ERROR = 9005;
    public static final int RELOGIN = 9006;
    public static final int OTHER_SERVER_ERROR = 9007;

    public static final String SUCCESS = "AS0000";
    public static final String SESSION_OUT_OF_TIME = "AS0001";
    public static final String LOGIN_IN_OTHER_DEVICE = "AS0002";
    public static final String INVALID_SESSION = "AS0003";
    public static final String SESSION_ERROR_ID_NULL = "AS0011";
    public static final String SESSION_SETTING_ERROR = "AS0106";
    public static final String INVALID_TIMESTAMP = "AS0004";
    public static final String LOGIN_REQUIRED = "AS0004";
    public static final String SCHOOL_REQUIRED = "AS0005";
    public static final String STUDENT_REQUIRED = "AS0006";
    public static final String ECARD_REQUIRED = "AS0007";
    public static final String FEE_REQUIRED = "AS0008";
    public static final String INVALID_REQUEST_VALUE = "AS0009";
    public static final String UNKNOWN_ERROR = "AS0098";
    public static final String SYSTEM_ERROR = "AS0099";

    // customer
    public static final String GENERAL_CUSTOMER_ERROR = "AS0100";
    public static final String SESSION_ERROR = "AS0101";
    public static final String PASSWORD_NOT_MATCH = "AS0102";
    public static final String ACCOUNT_ALREADY_EXIST = "AS0103";
    public static final String ACCOUNT_NOT_EXIST = "AS0104";
    public static final String SWITCH_SCHOOL_NOT_ALLOWED = "AS0105";


}
