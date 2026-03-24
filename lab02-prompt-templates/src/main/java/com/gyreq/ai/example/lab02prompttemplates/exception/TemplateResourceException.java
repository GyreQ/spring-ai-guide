package com.gyreq.ai.example.lab02prompttemplates.exception;

import com.gyreq.ai.example.common.exception.BusinessException;

/**
 * 模板资源异常
 *
 * <p>当模板文件缺失或加载失败时抛出此异常。
 * 继承 BusinessException，由 GlobalExceptionHandler 统一处理。
 *
 * @author gyreq
 * @since 1.0.0
 */
public class TemplateResourceException extends BusinessException {

    /**
     * 构造异常
     *
     * @param message 异常信息
     */
    public TemplateResourceException(String message) {
        super("服务配置错误：" + message);
    }

    /**
     * 构造异常
     *
     * @param message 异常信息
     * @param cause   原因
     */
    public TemplateResourceException(String message, Throwable cause) {
        super("服务配置错误：" + message, cause);
    }

}
