package com.fxm.fansoj.controller;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fxm.fansoj.annotation.AuthCheck;
import com.fxm.fansoj.common.BaseResponse;
import com.fxm.fansoj.common.DeleteRequest;
import com.fxm.fansoj.common.ErrorCode;
import com.fxm.fansoj.common.ResultUtils;
import com.fxm.fansoj.constant.UserConstant;
import com.fxm.fansoj.exception.BusinessException;
import com.fxm.fansoj.exception.ThrowUtils;
import com.fxm.fansoj.model.dto.question.*;
import com.fxm.fansoj.model.dto.questionSubmit.QuestionSubmitAddRequest;
import com.fxm.fansoj.model.dto.questionSubmit.QuestionSubmitQueryRequest;
import com.fxm.fansoj.model.entity.Question;
import com.fxm.fansoj.model.entity.QuestionSubmit;
import com.fxm.fansoj.model.entity.User;
import com.fxm.fansoj.model.vo.QuestionSubmitVO;
import com.fxm.fansoj.model.vo.QuestionVO;
import com.fxm.fansoj.service.JudgeQuestionService;
import com.fxm.fansoj.service.QuestionService;
import com.fxm.fansoj.service.QuestionSubmitService;
import com.fxm.fansoj.service.UserService;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 问题接口
 *
 * @author fansheng
 */
@RestController
@RequestMapping("/Question")
@Slf4j
public class QuestionController {

    @Resource
    private QuestionService QuestionService;

    @Resource
    private QuestionSubmitService questionSubmitService;

    @Resource
    private JudgeQuestionService judgeQuestionService;

    @Resource
    private UserService userService;

    private final static Gson GSON = new Gson();

    // region 增删改查

    /**
     * 创建
     *
     * @param questionAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addQuestion(@RequestBody QuestionAddRequest questionAddRequest, HttpServletRequest request) {
        if (questionAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Question question = new Question();
        BeanUtils.copyProperties(questionAddRequest, question);
        JudgeConfig judgeConfig = questionAddRequest.getJudgeConfig();
        List<JudgeCase> judgeCase = questionAddRequest.getJudgeCase();
        List<String> tags = questionAddRequest.getTags();

        if(judgeConfig != null){
            question.setJudgeConfig(JSONUtil.toJsonStr(judgeConfig));
        }
        if(judgeCase != null){
            question.setJudgeCase(JSONUtil.toJsonStr(judgeCase));
        }
        if (tags != null) {
            question.setTags(GSON.toJson(tags));
        }
        QuestionService.validQuestion(question, true);
        User loginUser = userService.getLoginUser(request);
        question.setUserId(loginUser.getId());
        question.setFavourNum(0);
        question.setThumbNum(0);
        boolean result = QuestionService.save(question);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        long newQuestionId = question.getId();
        return ResultUtils.success(newQuestionId);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteQuestion(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        Question oldQuestion = QuestionService.getById(id);
        ThrowUtils.throwIf(oldQuestion == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldQuestion.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = QuestionService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新（仅管理员）
     *
     * @param QuestionUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateQuestion(@RequestBody QuestionUpdateRequest QuestionUpdateRequest) {
        if (QuestionUpdateRequest == null || QuestionUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Question Question = new Question();
        BeanUtils.copyProperties(QuestionUpdateRequest, Question);
        List<String> tags = QuestionUpdateRequest.getTags();
        if (tags != null) {
            Question.setTags(GSON.toJson(tags));
        }
        // 参数校验
        QuestionService.validQuestion(Question, false);
        long id = QuestionUpdateRequest.getId();
        // 判断是否存在
        Question oldQuestion = QuestionService.getById(id);
        ThrowUtils.throwIf(oldQuestion == null, ErrorCode.NOT_FOUND_ERROR);
        boolean result = QuestionService.updateById(Question);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<QuestionVO> getQuestionVOById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Question Question = QuestionService.getById(id);
        if (Question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(QuestionService.getQuestionVO(Question, request));
    }

    /**
     * 分页获取列表（封装类）
     *
     * @param QuestionQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<QuestionVO>> listQuestionVOByPage(@RequestBody QuestionQueryRequest QuestionQueryRequest,
            HttpServletRequest request) {
        long current = QuestionQueryRequest.getCurrent();
        long size = QuestionQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Question> QuestionPage = QuestionService.page(new Page<>(current, size),
                QuestionService.getQueryWrapper(QuestionQueryRequest));
        return ResultUtils.success(QuestionService.getQuestionVOPage(QuestionPage, request));
    }

    /**
     * 分页获取当前问题创建的资源列表
     *
     * @param QuestionQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<QuestionVO>> listMyQuestionVOByPage(@RequestBody QuestionQueryRequest QuestionQueryRequest,
            HttpServletRequest request) {
        if (QuestionQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        QuestionQueryRequest.setUserId(loginUser.getId());
        long current = QuestionQueryRequest.getCurrent();
        long size = QuestionQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Question> QuestionPage = QuestionService.page(new Page<>(current, size),
                QuestionService.getQueryWrapper(QuestionQueryRequest));
        return ResultUtils.success(QuestionService.getQuestionVOPage(QuestionPage, request));
    }

    // endregion



    /**
     * 编辑（问题）
     *
     * @param QuestionEditRequest
     * @param request
     * @return
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editQuestion(@RequestBody QuestionEditRequest QuestionEditRequest, HttpServletRequest request) {
        if (QuestionEditRequest == null || QuestionEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Question Question = new Question();
        BeanUtils.copyProperties(QuestionEditRequest, Question);
        // 参数校验
        QuestionService.validQuestion(Question, false);
        User loginUser = userService.getLoginUser(request);
        long id = QuestionEditRequest.getId();
        // 判断是否存在
        Question oldQuestion = QuestionService.getById(id);
        ThrowUtils.throwIf(oldQuestion == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可编辑
        if (!oldQuestion.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = QuestionService.updateById(Question);
        return ResultUtils.success(result);
    }

    /**
     * 管理员接口，不做脱敏处理
     * @param QuestionQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<QuestionVO>> listQuestionByPage(@RequestBody QuestionQueryRequest QuestionQueryRequest,
                                                               HttpServletRequest request) {
        long current = QuestionQueryRequest.getCurrent();
        long size = QuestionQueryRequest.getPageSize();
        // 限制爬虫
        Page<Question> QuestionPage = QuestionService.page(new Page<>(current, size),
                QuestionService.getQueryWrapper(QuestionQueryRequest));
        return ResultUtils.success(QuestionService.getQuestionVOPage(QuestionPage, request));
    }

    @PostMapping("/question_submit/judge")
    public BaseResponse<QuestionSubmit> doQuestionSubmit(@RequestBody QuestionSubmitAddRequest questionSubmitAddRequest, HttpServletRequest request) {
        if (questionSubmitAddRequest == null || questionSubmitAddRequest.getQuestionId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        final User loginUser = userService.getLoginUser(request);
        long addResult = questionSubmitService.doQuestionSubmit(questionSubmitAddRequest, loginUser);
        QuestionSubmit questionSubmit = judgeQuestionService.doJudge(addResult);
        return ResultUtils.success(questionSubmit);
    }

    /**
     *
     * @param questionSubmitQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/question_submit/list/page")
    public BaseResponse<Page<QuestionSubmitVO>> listQuestionSubmitByPage(@RequestBody QuestionSubmitQueryRequest questionSubmitQueryRequest,
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
