package com.fxm.fansoj.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fxm.fansoj.common.BaseResponse;
import com.fxm.fansoj.common.ErrorCode;
import com.fxm.fansoj.common.ResultUtils;
import com.fxm.fansoj.exception.BusinessException;
import com.fxm.fansoj.model.dto.questionSubmit.QuestionSubmitAddRequest;
import com.fxm.fansoj.model.dto.questionSubmit.QuestionSubmitQueryRequest;
import com.fxm.fansoj.model.entity.QuestionSubmit;
import com.fxm.fansoj.model.entity.User;
import com.fxm.fansoj.model.vo.QuestionSubmitVO;
import com.fxm.fansoj.service.JudgeQuestionService;
import com.fxm.fansoj.service.QuestionSubmitService;
import com.fxm.fansoj.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

//@RestController
//@Deprecated
@RequestMapping("/question_submit")
public class QuestionSubmitController {
    @Resource
    private QuestionSubmitService questionSubmitService;
    @Resource
    private UserService userService;

    @Resource
    private JudgeQuestionService judgeQuestionService;

    @PostMapping("/")
    public BaseResponse<Long> doQuestionSubmit(@RequestBody QuestionSubmitAddRequest questionSubmitAddRequest, HttpServletRequest request) {
        if (questionSubmitAddRequest == null || questionSubmitAddRequest.getQuestionId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        final User loginUser = userService.getLoginUser(request);
        long result = questionSubmitService.doQuestionSubmit(questionSubmitAddRequest, loginUser);
        judgeQuestionService.doJudge(result);
        return ResultUtils.success(result);
    }

    /**
     *
     * @param questionSubmitQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page")
    public BaseResponse<Page<QuestionSubmitVO>> listQuestionByPage(@RequestBody QuestionSubmitQueryRequest questionSubmitQueryRequest,
                                                                   HttpServletRequest request) {
        long current = questionSubmitQueryRequest.getCurrent();
        long size = questionSubmitQueryRequest.getPageSize();
        // 限制爬虫
        Page<QuestionSubmit> QuestionPage = questionSubmitService.page(new Page<>(current, size),
                questionSubmitService.getQueryWrapper(questionSubmitQueryRequest));
        final User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(questionSubmitService.getQuestionSubmitVOPage(QuestionPage, loginUser));
    }



}
