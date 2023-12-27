package com.fxm.fansoj.judge.strategy;

import com.fxm.fansoj.model.dto.questionSubmit.JudgeInfo;
import com.fxm.fansoj.model.entity.QuestionSubmit;
import org.springframework.stereotype.Service;

@Service
public class JudgeStrategyManager {
    public JudgeInfo doJudge(JudgeContext judgeContext){
        QuestionSubmit questionSubmit = judgeContext.getQuestionSubmit();
        String language = questionSubmit.getLanguage();
        JudgeStrategy judgeStrategy;
        if("java".equals(language)){
            judgeStrategy = new JavaJudgeStrategyImpl();
        }else {
            judgeStrategy = new DefaultJudgeStrategyImpl();
        }
        return judgeStrategy.doJudge(judgeContext);
    }
}
