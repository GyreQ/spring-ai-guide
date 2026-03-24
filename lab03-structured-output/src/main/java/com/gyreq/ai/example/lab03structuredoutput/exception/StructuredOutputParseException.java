package com.gyreq.ai.example.lab03structuredoutput.exception;

import com.gyreq.ai.example.common.exception.BusinessException;

/**
 * 结构化输出解析异常
 *
 * <p>当大模型返回的内容无法解析为目标 POJO 时抛出此异常。
 *
 * @author gyreq
 * @since 1.0.0
 */
public class StructuredOutputParseException extends BusinessException {

    /**
     * 构造异常
     *
     * @param message 异常信息
     */
    public StructuredOutputParseException(String message) {
        super(message);
    }

    /**
     * 构造异常
     *
     * @param message 异常信息
     * @param cause   原因
     */
    public StructuredOutputParseException(String message, Throwable cause) {
        super(message, cause);
    }

}
