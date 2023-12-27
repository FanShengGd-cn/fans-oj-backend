package com.fxm.fansoj.service.impl;

import cn.hutool.json.JSONUtil;
import com.fxm.fansoj.common.ErrorCode;
import com.fxm.fansoj.exception.BusinessException;
import com.fxm.fansoj.factory.SandBoxFactory;
import com.fxm.fansoj.judge.codesandbox.CodeSandBox;
import com.fxm.fansoj.judge.strategy.JudgeContext;
import com.fxm.fansoj.judge.strategy.JudgeStrategyManager;
import com.fxm.fansoj.model.dto.question.JudgeCase;
import com.fxm.fansoj.model.dto.questionSubmit.ExecuteQuestionRequest;
import com.fxm.fansoj.model.dto.questionSubmit.ExecuteQuestionResponse;
import com.fxm.fansoj.model.dto.questionSubmit.JudgeInfo;
import com.fxm.fansoj.model.entity.Question;
import com.fxm.fansoj.model.entity.QuestionSubmit;
import com.fxm.fansoj.model.enums.QuestionSubmitStatusEnum;
import com.fxm.fansoj.proxy.SandBoxProxy;
import com.fxm.fansoj.service.JudgeQuestionService;
import com.fxm.fansoj.service.QuestionService;
import com.fxm.fansoj.service.QuestionSubmitService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class JudgeQuestionServiceImpl implements JudgeQuestionService {
    @Value("${codeSandBox.type}")
    private String type;
    @Resource
    private QuestionService questionService;
    @Resource
    private QuestionSubmitService questionSubmitService;

    @Resource
    private JudgeStrategyManager judgeStrategyManager;

    @Override
    public QuestionSubmit doJudge(Long questionSubmitId) {
        // Todo 非原子操作，待加锁
        QuestionSubmit questionSubmit = questionSubmitService.getById(questionSubmitId);
        if (questionSubmit == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "提交信息不存在");
        }

        Long id = questionSubmit.getId();

        String judgeInfo = questionSubmit.getJudgeInfo();
        Long userId = questionSubmit.getUserId();
        Integer status = questionSubmit.getStatus();
        Long questionId = questionSubmit.getQuestionId();

        if (!Objects.equals(status, QuestionSubmitStatusEnum.WAITING.getValue())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "正在判题中");
        }
        Question question = questionService.lambdaQuery()
                .select(Question::getJudgeCase, Question::getJudgeConfig)
                .eq(Question::getId, questionId).one();
        if (question == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "问题不存在");
        }
        // 更改题目状态
        QuestionSubmit questionSubmitUpdate = new QuestionSubmit();
        questionSubmitUpdate.setId(id);
        questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.RUNNING.getValue());
        boolean updateStatus = questionSubmitService.updateById(questionSubmitUpdate);
        if (!updateStatus) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        String language = questionSubmit.getLanguage();
        String code = questionSubmit.getCode();
        String judgeCase = question.getJudgeCase();
        List<JudgeCase> judgeCaseList = JSONUtil.toList(judgeCase, JudgeCase.class);
        List<String> inputList = judgeCaseList.stream().map(JudgeCase::getInput).collect(Collectors.toList());
        ExecuteQuestionRequest executeQuestionRequest = ExecuteQuestionRequest.builder()
                .code(code)
                .language(language)
                .inputList(inputList)
                .build();
        // 应用代理模式和工厂模式调用代码沙箱
        CodeSandBox codeSandBox = new SandBoxProxy(SandBoxFactory.getCodeSandBox(type));
        ExecuteQuestionResponse executeQuestionResponse = codeSandBox.doExecute(executeQuestionRequest);
        // 检查沙箱输出结果
        JudgeContext judgeContext = new JudgeContext();
        judgeContext.setJudgeInfo(executeQuestionResponse.getJudgeInfo());
        judgeContext.setInputList(inputList);
        judgeContext.setOutputList(executeQuestionResponse.getOutputList());
        judgeContext.setQuestion(question);
        judgeContext.setJudgeCaseList(judgeCaseList);

        JudgeInfo judgeInfoRes = judgeStrategyManager.doJudge(judgeContext);
        // 修改提交结果状态
        questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.SUCCEED.getValue());
        questionSubmitUpdate.setJudgeInfo(JSONUtil.toJsonStr(judgeInfoRes));
        boolean finalUpdate = questionSubmitService.updateById(questionSubmitUpdate);
        if(!finalUpdate){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        QuestionSubmit resQS = questionSubmitService.getById(questionSubmitId);
        return resQS;
    }
}