package com.fxm.fansoj.judge.strategy;

import com.fxm.fansoj.model.dto.question.JudgeCase;
import com.fxm.fansoj.model.dto.questionSubmit.JudgeInfo;
import com.fxm.fansoj.model.entity.Question;
import com.fxm.fansoj.model.entity.QuestionSubmit;
import lombok.Data;

import java.util.List;

@Data
public class JudgeContext {
    private JudgeInfo judgeInfo;
    private List<String> inputList;
    private List<String> outputList;
    private List<JudgeCase> judgeCaseList;
    private Question question;
    private QuestionSubmit questionSubmit;
}
