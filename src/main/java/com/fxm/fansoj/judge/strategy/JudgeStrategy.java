package com.fxm.fansoj.judge.strategy;

import com.fxm.fansoj.model.dto.questionSubmit.JudgeInfo;

/**
 * 判题策略
 */
public interface JudgeStrategy {
    /**
     * 判题
     */
    JudgeInfo doJudge(JudgeContext judgeContext);
}
