package com.gyreq.ai.example.lab03structuredoutput.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 人员信息 POJO
 *
 * <p>用于接收大模型返回的结构化数据。
 *
 * @author gyreq
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonInfo {

    /**
     * 姓名
     */
    private String name;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 爱好列表
     */
    private List<String> hobbies;

}
