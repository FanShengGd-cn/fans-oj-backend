package com.fxm.fansoj.judge.strategy;

import cn.hutool.json.JSONUtil;
import com.fxm.fansoj.model.dto.question.JudgeCase;
import com.fxm.fansoj.model.dto.question.JudgeConfig;
import com.fxm.fansoj.model.dto.questionSubmit.JudgeInfo;
import com.fxm.fansoj.model.entity.Question;
import com.fxm.fansoj.model.enums.JudgeInfoMessageEnum;

import java.util.List;

public class JavaJudgeStrategyImpl implements JudgeStrategy{
    long JAVA_TIME_COST = 2000L;
    @Override
    public JudgeInfo doJudge(JudgeContext judgeContext) {
        JudgeInfo outputJudgeInfo = judgeContext.getJudgeInfo();
        List<String> inputList = judgeContext.getInputList();
        List<String> outputList = judgeContext.getOutputList();
        Question question = judgeContext.getQuestion();
        List<JudgeCase> judgeCaseList = judgeContext.getJudgeCaseList();

        // 待返回运行结果
        JudgeInfo judgeInfoResp = new JudgeInfo();


        JudgeInfoMessageEnum judgeInfoMessageEnum = JudgeInfoMessageEnum.ACCEPTED;
        if (outputList.size() != inputList.size()) {
            judgeInfoMessageEnum = JudgeInfoMessageEnum.WRONG_ANSWER;
            judgeInfoResp.setMessage(judgeInfoMessageEnum.getValue());
            return judgeInfoResp;
        }
        for (int i = 0; i < judgeCaseList.size(); i++) {
            if (!judgeCaseList.get(i).getOutput().equals(outputList.get(i))) {
                judgeInfoMessageEnum = JudgeInfoMessageEnum.WRONG_ANSWER;
                judgeInfoResp.setMessage(judgeInfoMessageEnum.getValue());
                return judgeInfoResp;
            }
        }
        Long memory = outputJudgeInfo.getMemory();
        Long time = outputJudgeInfo.getTime();
        String judgeConfig = question.getJudgeConfig();
        JudgeConfig judgeConfigBean = JSONUtil.toBean(judgeConfig, JudgeConfig.class);
        Long timeLimit = judgeConfigBean.getTimeLimit();
        Long memoryLimit = judgeConfigBean.getMemoryLimit();
        if (memory > memoryLimit) {
            judgeInfoMessageEnum = JudgeInfoMessageEnum.MEMORY_LIMIT_EXCEEDED;
            judgeInfoResp.setMessage(judgeInfoMessageEnum.getValue());
            return judgeInfoResp;
        }
        if (time-JAVA_TIME_COST > timeLimit) {
            judgeInfoMessageEnum = JudgeInfoMessageEnum.TIME_LIMIT_EXCEEDED;
            judgeInfoResp.setMessage(judgeInfoMessageEnum.getValue());
            return judgeInfoResp;
        }


        judgeInfoResp.setMessage(judgeInfoMessageEnum.getValue());
        judgeInfoResp.setMemory(memory);
        judgeInfoResp.setTime(time);


        return judgeInfoResp;
    }
}
