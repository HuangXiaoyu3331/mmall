package com.huang.mmall.common;

/**
 * 响应状态枚举类
 *
 * @author hxy
 * @date 2019/01/14
 */
public enum ResponseCode {
    //请求成功状态码
    SUCCESS(1, "SUCCESS"),
    //请求失败状态码
    ERROR(0, "ERROR"),
    //需要登录状态码
    NEED_LOGIN(10, "NEED_LOGIN"),
    //参数异常状态码
    ILLEGAL_ARGUMENT(2, "ILLEGAL_ARGUMENT");

    /**
     * 状态码
     */
    private final int code;
    /**
     * 描述
     */
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
