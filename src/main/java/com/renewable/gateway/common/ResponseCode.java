package com.renewable.gateway.common;

/**
 * @Author: jarry
 */
public enum ResponseCode {

    //控制层响应
    SUCCESS(0, "SUCCESS"),
    ERROR(1, "ERROR"),
    NEED_LOGIN(10, "NEED_LOGIN"),
    ILLEAGAL_ARGUMENT(2, "ILLEAGAL ARGUMENT");

    private final int code;
    private final String desc;

    ResponseCode(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }


}
