package com.gyreq.ai.example.lab02prompttemplates.service;

import com.gyreq.ai.example.lab02prompttemplates.dto.JokeResponseDTO;

/**
 * 笑话服务接口
 *
 * <p>提供笑话生成的核心业务方法。
 *
 * @author gyreq
 * @since 1.0.0
 */
public interface JokeService {

    /**
     * 生成笑话
     *
     * @param name  笑话主角名字
     * @param topic 笑话主题
     * @return 笑话响应
     */
    JokeResponseDTO generateJoke(String name, String topic);

}
