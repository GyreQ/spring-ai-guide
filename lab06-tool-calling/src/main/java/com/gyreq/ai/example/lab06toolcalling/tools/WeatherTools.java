package com.gyreq.ai.example.lab06toolcalling.tools;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

/**
 * 天气查询工具类
 *
 * <p>使用 {@code @Tool} 注解定义工具方法，Spring AI 会自动生成对应的 JSON Schema，
 * 供模型识别和调用。
 *
 * <p>参考 Spring AI 官方文档：
 * <a href="https://docs.spring.io/spring-ai/reference/api/tools.html">Tool Calling API</a>
 *
 * @author gyreq
 * @since 1.0.0
 */
@Slf4j
@Component
public class WeatherTools {

    /**
     * 获取指定城市的当前天气
     *
     * <p>模拟调用第三方天气 API，返回天气信息。
     * 当城市为"火星"时，抛出异常演示错误处理机制。
     *
     * @param city 城市名称
     * @return 天气信息字符串
     * @throws IllegalArgumentException 当城市为"火星"时抛出
     */
    @Tool(description = "获取指定城市的当前天气信息，包括温度、天气状况等")
    public String getWeather(
            @ToolParam(description = "城市名称，如：北京、上海、广州") String city) {

        log.info("工具调用: getWeather(city={})", city);

        // 模拟异常场景：无法查询火星天气
        if ("火星".equals(city)) {
            log.warn("无法查询火星的天气信息");
            throw new IllegalArgumentException("无法查询火星的天气信息，请提供一个地球上的城市名称");
        }

        // 模拟调用第三方天气 API
        String weather = simulateWeatherApi(city);

        log.info("天气查询结果: {}", weather);
        return weather;
    }

    /**
     * 获取未来几天的天气预报
     *
     * @param city 城市名称
     * @param days 预报天数（1-7天）
     * @return 天气预报信息
     */
    @Tool(description = "获取指定城市未来几天的天气预报")
    public String getWeatherForecast(
            @ToolParam(description = "城市名称") String city,
            @ToolParam(description = "预报天数，范围1-7天", required = true) Integer days) {

        log.info("工具调用: getWeatherForecast(city={}, days={})", city, days);

        if (days < 1 || days > 7) {
            throw new IllegalArgumentException("预报天数必须在1-7天之间");
        }

        // 模拟天气预报数据
        StringBuilder forecast = new StringBuilder();
        forecast.append(city).append("未来").append(days).append("天天气预报：\n");

        for (int i = 1; i <= days; i++) {
            int temp = 20 + (int) (Math.random() * 10);
            String[] conditions = {"晴", "多云", "小雨", "阴"};
            String condition = conditions[(int) (Math.random() * conditions.length)];
            forecast.append(String.format("第%d天: %d℃, %s\n", i, temp, condition));
        }

        String result = forecast.toString();
        log.info("天气预报结果: {}", result);
        return result;
    }

    /**
     * 模拟第三方天气 API 调用
     *
     * @param city 城市名称
     * @return 模拟的天气数据
     */
    private String simulateWeatherApi(String city) {
        // 模拟不同城市的天气数据
        return switch (city) {
            case "北京" -> "城市: 北京, 温度: 28℃, 天气: 晴朗, 湿度: 45%, 风速: 3级";
            case "上海" -> "城市: 上海, 温度: 32℃, 天气: 多云, 湿度: 75%, 风速: 2级";
            case "广州" -> "城市: 广州, 温度: 35℃, 天气: 雷阵雨, 湿度: 85%, 风速: 4级";
            case "深圳" -> "城市: 深圳, 温度: 33℃, 天气: 多云, 湿度: 80%, 风速: 3级";
            case "成都" -> "城市: 成都, 温度: 26℃, 天气: 阴, 湿度: 60%, 风速: 1级";
            default -> String.format("城市: %s, 温度: 25℃, 天气: 晴朗, 湿度: 50%%, 风速: 2级", city);
        };
    }

}
