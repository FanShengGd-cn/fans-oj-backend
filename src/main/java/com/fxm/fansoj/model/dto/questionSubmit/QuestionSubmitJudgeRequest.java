package com.fxm.fansoj.model.dto.questionSubmit;

import lombok.Data;

import java.io.Serializable;

@Data
public class QuestionSubmitJudgeRequest implements Serializable {
    /**
     * language
     */
    private String language;

    /**
     * 用户代码
     */
    private String code;

    /**
     * 题目id
     */
    private Long questionId;

    private static final long serialVersionUID = 1L;
}
