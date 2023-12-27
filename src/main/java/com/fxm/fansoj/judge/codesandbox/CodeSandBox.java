package com.fxm.fansoj.judge.codesandbox;

import com.fxm.fansoj.model.dto.questionSubmit.ExecuteQuestionRequest;
import com.fxm.fansoj.model.dto.questionSubmit.ExecuteQuestionResponse;

public interface CodeSandBox {
    public ExecuteQuestionResponse doExecute(ExecuteQuestionRequest executeQuestionRequest);
}
