package com.gyreq.ai.example.lab06toolcalling.controller;

import com.gyreq.ai.example.common.model.Result;
import com.gyreq.ai.example.lab06toolcalling.dto.WeatherResponseDTO;
import com.gyreq.ai.example.lab06toolcalling.service.WeatherService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 天气查询控制器
 *
 * <p>演示 Tool Calling 的 REST API 入口。
 *
 * @author gyreq
 * @since 1.0.0
 */
@Validated
@RestController
@RequestMapping("/api/v1/weather")
@RequiredArgsConstructor
public class WeatherController {

    private final WeatherService weatherService;

    /**
     * 与 AI 对话，自动触发工具调用
     *
     * <p>示例请求：
     * <ul>
     *   <li>GET /api/v1/weather/chat?question=北京今天天气怎么样？</li>
     *   <li>GET /api/v1/weather/chat?question=上海未来3天天气预报</li>
     *   <li>GET /api/v1/weather/chat?question=火星的天气如何？（异常场景）</li>
     * </ul>
     *
     * @param question 用户问题
     * @return AI 回复
     */
    @GetMapping("/chat")
    public Result<WeatherResponseDTO> chat(
            @RequestParam @NotBlank(message = "问题不能为空") String question) {

        WeatherResponseDTO response = weatherService.chat(question);
        return Result.success(response);
    }

}
