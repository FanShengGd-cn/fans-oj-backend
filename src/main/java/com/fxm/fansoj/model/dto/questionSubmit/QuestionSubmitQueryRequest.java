package com.fxm.fansoj.model.dto.questionSubmit;

import com.fxm.fansoj.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 创建请求
 *
 * @author fansheng
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class QuestionSubmitQueryRequest extends PageRequest implements Serializable {

    /**
     * language
     */
    private String language;

    /**
     * 用户代码
     */
    private String code;

    /**
     * 题目id
     */
    private Long questionId;

    private static final long serialVersionUID = 1L;
}