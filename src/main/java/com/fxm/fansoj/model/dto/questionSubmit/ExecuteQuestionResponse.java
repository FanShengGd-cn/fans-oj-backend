package com.fxm.fansoj.model.dto.questionSubmit;

import lombok.Data;

import java.util.List;

@Data
public class ExecuteQuestionResponse {
    private String message;
    private Integer status;
    private List<String> OutputList;
    private JudgeInfo judgeInfo;
}
