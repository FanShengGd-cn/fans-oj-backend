package com.fxm.fansoj.model.dto.question;

import lombok.Data;

import java.util.List;

/**
 * 题目编辑
 */

@Data
public class QuestionEditRequest {
    /**
     * id
     */
    private Long id;

    /**
     * 标题
     */
    private String title;
    /**
     * 内容
     */
    private String content;


    /**
     * 题目答案
     */
    private String answer;

    /**
     * 判题用例（json数组）
     */
    private List<JudgeCase> judgeCase;

    /**
     * 判题配置（json对象）
     */
    private JudgeConfig judgeConfig;


    private static final long serialVersionUID = 1L;
}
