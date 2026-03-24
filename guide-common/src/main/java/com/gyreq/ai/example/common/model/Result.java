package com.gyreq.ai.example.common.model;

import java.io.Serializable;

/**
 * 统一响应结果封装类
 *
 * <p>所有 API 接口统一使用此格式返回，便于前端统一处理。
 *
 * @param <T> 数据类型
 * @author gyreq
 * @since 1.0.0
 */
public record Result<T>(

        /**
         * 状态码
         */
        int code,

        /**
         * 提示信息
         */
        String message,

        /**
         * 响应数据
         */
        T data

) implements Serializable {

    private static final int SUCCESS_CODE = 200;
    private static final int ERROR_CODE = 500;

    /**
     * 成功响应
     *
     * @param data 响应数据
     * @param <T>  数据类型
     * @return 成功结果
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(SUCCESS_CODE, "success", data);
    }

    /**
     * 成功响应（无数据）
     *
     * @param <T> 数据类型
     * @return 成功结果
     */
    public static <T> Result<T> success() {
        return new Result<>(SUCCESS_CODE, "success", null);
    }

    /**
     * 失败响应
     *
     * @param message 错误信息
     * @param <T>     数据类型
     * @return 失败结果
     */
    public static <T> Result<T> error(String message) {
        return new Result<>(ERROR_CODE, message, null);
    }

    /**
     * 失败响应（自定义状态码）
     *
     * @param code    状态码
     * @param message 错误信息
     * @param <T>     数据类型
     * @return 失败结果
     */
    public static <T> Result<T> error(int code, String message) {
        return new Result<>(code, message, null);
    }

    /**
     * 判断是否成功
     *
     * @return 是否成功
     */
    public boolean isSuccess() {
        return this.code == SUCCESS_CODE;
    }

}
