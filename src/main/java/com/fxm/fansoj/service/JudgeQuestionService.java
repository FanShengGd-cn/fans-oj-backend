package com.fxm.fansoj.service;

import com.fxm.fansoj.model.entity.QuestionSubmit;

public interface JudgeQuestionService {
    QuestionSubmit doJudge(Long questionSubmitId);
}
