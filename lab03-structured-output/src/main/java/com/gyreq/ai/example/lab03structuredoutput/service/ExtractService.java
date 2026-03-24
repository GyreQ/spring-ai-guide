package com.gyreq.ai.example.lab03structuredoutput.service;

import com.gyreq.ai.example.lab03structuredoutput.dto.ExtractResponseDTO;
import com.gyreq.ai.example.lab03structuredoutput.model.PersonInfo;

/**
 * 信息提取服务接口
 *
 * <p>提供从自然语言文本中提取结构化信息的能力。
 *
 * @author gyreq
 * @since 1.0.0
 */
public interface ExtractService {

    /**
     * 从自然语言文本中提取人员信息
     *
     * @param text 自然语言文本
     * @return 提取的人员信息
     */
    ExtractResponseDTO extractPersonInfo(String text);

}
