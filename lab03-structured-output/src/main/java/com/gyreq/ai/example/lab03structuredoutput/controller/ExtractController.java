package com.gyreq.ai.example.lab03structuredoutput.controller;

import com.gyreq.ai.example.common.model.Result;
import com.gyreq.ai.example.lab03structuredoutput.dto.ExtractRequestDTO;
import com.gyreq.ai.example.lab03structuredoutput.dto.ExtractResponseDTO;
import com.gyreq.ai.example.lab03structuredoutput.service.ExtractService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 信息提取控制器
 *
 * <p>提供从自然语言文本中提取结构化信息的 REST API 接口。
 *
 * @author gyreq
 * @since 1.0.0
 */
@RestController
@RequestMapping("/lab03")
@RequiredArgsConstructor
public class ExtractController {

    private final ExtractService extractService;

    /**
     * 提取人员信息接口
     *
     * <p>从自然语言文本中提取姓名、年龄、爱好等结构化信息。
     *
     * @param request 提取请求
     * @return 提取的人员信息
     */
    @PostMapping("/extract")
    public Result<ExtractResponseDTO> extract(@Valid @RequestBody ExtractRequestDTO request) {
        ExtractResponseDTO response = extractService.extractPersonInfo(request.text());
        return Result.success(response);
    }

}
