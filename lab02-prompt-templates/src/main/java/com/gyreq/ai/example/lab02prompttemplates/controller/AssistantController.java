package com.gyreq.ai.example.lab02prompttemplates.controller;

import com.gyreq.ai.example.common.model.Result;
import com.gyreq.ai.example.lab02prompttemplates.dto.AssistantResponseDTO;
import com.gyreq.ai.example.lab02prompttemplates.service.AssistantService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 助手控制器
 *
 * <p>提供角色扮演助手的 REST API 接口。
 * 使用 SystemPromptTemplate 实现系统角色设定。
 *
 * @author gyreq
 * @since 1.0.0
 */
@RestController
@RequestMapping("/lab02")
@RequiredArgsConstructor
@Validated
public class AssistantController {

    private final AssistantService assistantService;

    /**
     * 角色扮演对话接口
     *
     * <p>根据指定的名字和风格创建一个角色扮演助手，并回答用户的问题。
     *
     * @param name     助手名字
     * @param voice    助手风格（如：莎士比亚、科幻小说家、幽默大师等）
     * @param question 用户问题
     * @return 助手响应
     */
    @GetMapping("/assistant")
    public Result<AssistantResponseDTO> chat(
            @RequestParam @NotBlank(message = "名字不能为空") String name,
            @RequestParam @NotBlank(message = "风格不能为空") String voice,
            @RequestParam @NotBlank(message = "问题不能为空") String question) {
        AssistantResponseDTO response = assistantService.chat(name, voice, question);
        return Result.success(response);
    }
}
