package com.fxm.fansoj.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fxm.fansoj.model.dto.questionSubmit.QuestionSubmitAddRequest;
import com.fxm.fansoj.model.dto.questionSubmit.QuestionSubmitQueryRequest;
import com.fxm.fansoj.model.entity.QuestionSubmit;
import com.fxm.fansoj.model.entity.User;
import com.fxm.fansoj.model.vo.QuestionSubmitVO;


/**
* @author fansheng
* @description 针对表【questionSubmit_submit(题目提交表)】的数据库操作Service
* @createDate 2023-12-22 09:18:08
*/
public interface QuestionSubmitService extends IService<QuestionSubmit> {
    long doQuestionSubmit(QuestionSubmitAddRequest QuestionSubmitAddRequest, User loginUser);
    
    /**
     * 校验
     *
     * @param questionSubmit
     * @param add
     */
    void validQuestionSubmit(QuestionSubmit questionSubmit, boolean add);

    /**
     * 获取查询条件
     *
     * @param questionSubmitQueryRequest
     * @return
     */
    QueryWrapper<QuestionSubmit> getQueryWrapper(QuestionSubmitQueryRequest questionSubmitQueryRequest);



    /**
     * 获取题目封装
     *
     * @param questionSubmit
     * @param
     * @return
     */
    QuestionSubmitVO getQuestionSubmitVO(QuestionSubmit questionSubmit, User loginUser);

    /**
     * 分页获取题目封装
     *
     * @param questionSubmitPage
     * @param
     * @return
     */
    Page<QuestionSubmitVO> getQuestionSubmitVOPage(Page<QuestionSubmit> questionSubmitPage,User loginUser);
}
