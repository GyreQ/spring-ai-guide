package com.gyreq.ai.example.common.exception;

/**
 * 业务异常基类
 *
 * <p>所有业务层抛出的异常应继承此类，统一由 GlobalExceptionHandler 处理。
 *
 * @author gyreq
 * @since 1.0.0
 */
public class BusinessException extends RuntimeException {

    /**
     * 错误码
     */
    private final int code;

    /**
     * 构造业务异常（默认错误码 500）
     *
     * @param message 异常信息
     */
    public BusinessException(String message) {
        super(message);
        this.code = 500;
    }

    /**
     * 构造业务异常
     *
     * @param code    错误码
     * @param message 异常信息
     */
    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * 构造业务异常
     *
     * @param message 异常信息
     * @param cause   原因
     */
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.code = 500;
    }

    /**
     * 构造业务异常
     *
     * @param code    错误码
     * @param message 异常信息
     * @param cause   原因
     */
    public BusinessException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public int getCode() {
        return code;
    }

}
