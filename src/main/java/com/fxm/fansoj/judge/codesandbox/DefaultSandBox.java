package com.fxm.fansoj.judge.codesandbox;

import com.fxm.fansoj.model.dto.questionSubmit.ExecuteQuestionRequest;
import com.fxm.fansoj.model.dto.questionSubmit.ExecuteQuestionResponse;

public class DefaultSandBox implements CodeSandBox{
    @Override
    public ExecuteQuestionResponse doExecute(ExecuteQuestionRequest executeQuestionRequest) {
        System.out.println("默认代码沙箱");
        return null;
    }
}
