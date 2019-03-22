package com.huang.mmall.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.util.Objects;

/**
 * 封装高可用响应对象，Jackson的注解，需要用com.fasterxml.jackson这个包下的
 * 用自己导入的包注解不生效，因为jackson没有注入到ioc中，需要自己配置。
 *
 * @author hxy
 * @date 2019/01/14
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ServerResponse<T> implements Serializable {
    /**
     * 请求状态
     */
    private int status;
    /**
     * 提示信息
     */
    private String msg;
    /**
     * 返回的数据
     */
    private T data;

    private ServerResponse(int status) {
        this.status = status;
    }

    private ServerResponse(int status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    private ServerResponse(int status, T data) {
        this.status = status;
        this.data = data;
    }

    private ServerResponse(int status, String msg, T data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    public int getStatus() {
        return status;
    }

    public String getMsg() {
        return msg;
    }

    public T getData() {
        return data;
    }

    /**
     * getXXX()、setXXX()、isXXX()开头的都会被序列化成字段
     *
     * @return
     * @ JsonIgnore是忽略该方法，使该方法不序列化为字段
     * 详情可看jackson源码
     */
    @JsonIgnore
    public boolean isSuccess() {
        return this.status == ResponseCode.SUCCESS.getCode();
    }

    /**
     * 请求成功
     */
    public static <T> ServerResponse<T> createBySuccess() {
        return new ServerResponse<>(ResponseCode.SUCCESS.getCode());
    }

    /**
     * 请求成功，带提示信息
     *
     * @param msg
     * @param <T>
     * @return
     */
    public static <T> ServerResponse<T> createBySuccessMessage(String msg) {
        return new ServerResponse<>(ResponseCode.SUCCESS.getCode(), msg);
    }

    /**
     * 请求成功，带数据
     *
     * @param data
     * @param <T>
     * @return
     */
    public static <T> ServerResponse<T> createBySuccess(T data) {
        return new ServerResponse<>(ResponseCode.SUCCESS.getCode(), data);
    }

    /**
     * 请求成功，带提示信息、数据
     *
     * @param msg
     * @param data
     * @param <T>
     * @return
     */
    public static <T> ServerResponse<T> createBySuccess(String msg, T data) {
        return new ServerResponse<>(ResponseCode.SUCCESS.getCode(), msg, data);
    }

    /**
     * 请求失败
     *
     * @param <T>
     * @return
     */
    public static <T> ServerResponse<T> createByError() {
        return new ServerResponse<>(ResponseCode.ERROR.getCode(), ResponseCode.ERROR.getDesc());
    }

    /**
     * 请求失败，带提示信息
     *
     * @param errorMsg
     * @param <T>
     * @return
     */
    public static <T> ServerResponse<T> createByErrorMessage(String errorMsg) {
        return new ServerResponse<>(ResponseCode.ERROR.getCode(), errorMsg);
    }

    /**
     * 请求失败，带失败状态码跟描述
     *
     * @param errorCode
     * @param errorMsg
     * @param <T>
     * @return
     */
    public static <T> ServerResponse<T> createByErrorCodeMessage(int errorCode, String errorMsg) {
        return new ServerResponse<>(errorCode, errorMsg);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ServerResponse<?> response = (ServerResponse<?>) o;
        return status == response.status &&
                Objects.equals(msg, response.msg) &&
                Objects.equals(data, response.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, msg, data);
    }
}
