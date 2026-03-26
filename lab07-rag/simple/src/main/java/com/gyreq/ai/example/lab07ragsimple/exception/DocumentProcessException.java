package com.gyreq.ai.example.lab07ragsimple.exception;

import com.gyreq.ai.example.common.exception.BusinessException;

/**
 * 文档处理异常
 *
 * <p>当文档解析或处理过程中发生错误时抛出。
 *
 * @author gyreq
 * @since 1.0.0
 */
public class DocumentProcessException extends BusinessException {

    /**
     * 构造函数
     *
     * @param message 错误信息
     */
    public DocumentProcessException(String message) {
        super(message);
    }

    /**
     * 构造函数
     *
     * @param message 错误信息
     * @param cause   原因
     */
    public DocumentProcessException(String message, Throwable cause) {
        super(message, cause);
    }

}
