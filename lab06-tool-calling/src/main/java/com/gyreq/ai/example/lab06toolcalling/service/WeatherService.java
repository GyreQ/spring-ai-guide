package com.gyreq.ai.example.lab06toolcalling.service;

import com.gyreq.ai.example.lab06toolcalling.dto.WeatherResponseDTO;

/**
 * 天气服务接口
 *
 * <p>演示 Tool Calling 的核心服务，用户无需关心工具调用细节，
 * 框架会自动处理工具选择、调用和结果整合。
 *
 * @author gyreq
 * @since 1.0.0
 */
public interface WeatherService {

    /**
     * 与 AI 对话，自动触发工具调用
     *
     * <p>用户只需发送自然语言问题，如"北京今天天气怎么样？"，
     * 模型会自动判断是否需要调用天气查询工具，并在回复中整合工具结果。
     *
     * @param question 用户问题
     * @return AI 回复
     */
    WeatherResponseDTO chat(String question);

}
