package com.fxm.fansoj.model.dto.questionSubmit;

import lombok.Data;

import java.util.List;

@Data
public class ExecuteQuestionResponse {
    private String message;
    private List<String> OutputList;
    private JudgeInfo judgeInfo;
}
