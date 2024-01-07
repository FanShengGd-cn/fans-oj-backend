package com.fxm.fansoj.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.fxm.fansoj.common.ErrorCode;
import com.fxm.fansoj.constant.CommonConstant;
import com.fxm.fansoj.exception.BusinessException;
import com.fxm.fansoj.exception.ThrowUtils;
import com.fxm.fansoj.mapper.QuestionSubmitMapper;
import com.fxm.fansoj.model.dto.questionSubmit.QuestionSubmitAddRequest;
import com.fxm.fansoj.model.dto.questionSubmit.QuestionSubmitQueryRequest;
import com.fxm.fansoj.model.entity.Question;
import com.fxm.fansoj.model.entity.QuestionSubmit;
import com.fxm.fansoj.model.entity.User;
import com.fxm.fansoj.model.enums.QuestionSubmitLanguageEnum;
import com.fxm.fansoj.model.enums.QuestionSubmitStatusEnum;
import com.fxm.fansoj.model.vo.QuestionSubmitVO;
import com.fxm.fansoj.service.QuestionService;
import com.fxm.fansoj.service.QuestionSubmitService;
import com.fxm.fansoj.service.UserService;
import com.fxm.fansoj.utils.SqlUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author fansheng
 * @description 针对表【question_submit(题目提交表)】的数据库操作Service实现
 * @createDate 2023-12-22 09:18:08
 */
@Service
public class QuestionSubmitServiceImpl extends ServiceImpl<QuestionSubmitMapper, QuestionSubmit>
        implements QuestionSubmitService {
    @Resource
    public QuestionService questionService;

    @Resource
    private UserService userService;

    @Override
    public long doQuestionSubmit(QuestionSubmitAddRequest questionSubmitAddRequest, User loginUser) {
        // 判断实体是否存在，根据类别获取实体
        Question question = questionService.getById(questionSubmitAddRequest.getQuestionId());
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        QuestionSubmitLanguageEnum languageEnum = QuestionSubmitLanguageEnum.getEnumByValue(questionSubmitAddRequest.getLanguage());
        if (languageEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "语言类型错误");
        }

        long userId = loginUser.getId();
        QuestionSubmit questionSubmit = new QuestionSubmit();
        questionSubmit.setLanguage(questionSubmitAddRequest.getLanguage());
        questionSubmit.setCode(questionSubmitAddRequest.getCode());
        questionSubmit.setQuestionId(questionSubmitAddRequest.getQuestionId());
        questionSubmit.setUserId(userId);
        questionSubmit.setStatus(QuestionSubmitStatusEnum.WAITING.getValue());
        questionSubmit.setJudgeInfo("{}");
        boolean save = save(questionSubmit);
        if (!save) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }

        return questionSubmit.getId();

    }

    @Override
    public void validQuestionSubmit(QuestionSubmit questionSubmit, boolean add) {
        if (questionSubmit == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String language = questionSubmit.getLanguage();
        String code = questionSubmit.getCode();
        Long questionId = questionSubmit.getQuestionId();

        // 创建时，参数不能为空
        if (add) {
            ThrowUtils.throwIf(StringUtils.isAnyBlank(code, language) || ObjectUtils.isEmpty(questionId), ErrorCode.PARAMS_ERROR);
            // 有参数则校验
            if (StringUtils.isNotBlank(code) && code.length() > 16384) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "代码过长");
            }
        }
        QuestionSubmitLanguageEnum enumByValue = QuestionSubmitLanguageEnum.getEnumByValue(language);
        if (enumByValue == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "语言类型错误");
        }
        if (questionId != null) {
            Long count = Db.lambdaQuery(Question.class).eq(Question::getId, questionId).count();
            if (count != 1) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }
        }
    }

    /**
     * 获取查询包装类
     *
     * @param questionSubmitQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<QuestionSubmit> getQueryWrapper(QuestionSubmitQueryRequest questionSubmitQueryRequest) {
        QueryWrapper<QuestionSubmit> queryWrapper = new QueryWrapper<>();
        if (questionSubmitQueryRequest == null) {
            return queryWrapper;
        }
        String language = questionSubmitQueryRequest.getLanguage();
        String code = questionSubmitQueryRequest.getCode();
        Long questionId = questionSubmitQueryRequest.getQuestionId();
        String sortField = questionSubmitQueryRequest.getSortField();
        String sortOrder = questionSubmitQueryRequest.getSortOrder();

        // 拼接查询条件
        if (StringUtils.isNotBlank(language)) {
            QuestionSubmitLanguageEnum enumByValue = QuestionSubmitLanguageEnum.getEnumByValue(language);
            if (enumByValue == null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "语言类型错误");
            }
            queryWrapper.like("language", language);
        }
        queryWrapper.like(StringUtils.isNotBlank(code), "code", code);
        queryWrapper.like(ObjectUtils.isNotEmpty(questionId), "questionId", questionId);
        queryWrapper.eq("isDelete", false);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }


    @Override
    public QuestionSubmitVO getQuestionSubmitVO(QuestionSubmit questionSubmit, User loginUser) {
        QuestionSubmitVO questionSubmitVO = QuestionSubmitVO.objToVo(questionSubmit);
        long userId = loginUser.getId();
        // 脱敏，仅本人和管理员能看到用户代码
        if (questionSubmit.getUserId() != userId && !userService.isAdmin(loginUser)) {
            questionSubmitVO.setCode(null);
        }
        return questionSubmitVO;
    }

    @Override
    public Page<QuestionSubmitVO> getQuestionSubmitVOPage(Page<QuestionSubmit> questionSubmitPage, User loginUser) {
        List<QuestionSubmit> questionSubmitList = questionSubmitPage.getRecords();
        Page<QuestionSubmitVO> questionSubmitVOPage = new Page<>(questionSubmitPage.getCurrent(), questionSubmitPage.getSize(), questionSubmitPage.getTotal());
        if (CollectionUtils.isEmpty(questionSubmitList)) {
            return questionSubmitVOPage;
        }
        List<QuestionSubmitVO> questionSubmitVOList = questionSubmitList.stream()
                .map(questionSubmit -> getQuestionSubmitVO(questionSubmit, loginUser))
                .collect(Collectors.toList());
        questionSubmitVOPage.setRecords(questionSubmitVOList);
        return questionSubmitVOPage;
    }
}




