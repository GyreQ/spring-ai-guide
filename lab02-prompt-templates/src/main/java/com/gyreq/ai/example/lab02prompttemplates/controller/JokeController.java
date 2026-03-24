package com.gyreq.ai.example.lab02prompttemplates.controller;

import com.gyreq.ai.example.common.model.Result;
import com.gyreq.ai.example.lab02prompttemplates.dto.JokeResponseDTO;
import com.gyreq.ai.example.lab02prompttemplates.service.JokeService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 笑话控制器
 *
 * <p>提供笑话生成相关的 REST API 接口。
 *
 * @author gyreq
 * @since 1.0.0
 */
@RestController
@RequestMapping("/lab02")
@RequiredArgsConstructor
@Validated
public class JokeController {

    private final JokeService jokeService;

    /**
     * 生成笑话接口
     *
     * <p>根据人名和主题生成一个幽默的笑话。
     *
     * @param name  笑话主角名字
     * @param topic 笑话主题
     * @return 生成的笑话
     */
    @GetMapping("/joke")
    public Result<JokeResponseDTO> generateJoke(
            @RequestParam @NotBlank(message = "名字不能为空") String name,
            @RequestParam @NotBlank(message = "主题不能为空") String topic) {
        JokeResponseDTO response = jokeService.generateJoke(name, topic);
        return Result.success(response);
    }

}
