package com.gyreq.ai.example.lab07ragmilvus.exception;

import com.gyreq.ai.example.common.exception.BusinessException;

/**
 * 文档处理异常
 *
 * @author gyreq
 * @since 1.0.0
 */
public class DocumentProcessException extends BusinessException {

    public DocumentProcessException(String message) {
        super(message);
    }

    public DocumentProcessException(String message, Throwable cause) {
        super(message, cause);
    }

}
