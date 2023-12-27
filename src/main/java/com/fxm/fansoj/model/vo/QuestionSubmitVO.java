package com.fxm.fansoj.model.vo;

import cn.hutool.json.JSONUtil;
import com.fxm.fansoj.model.dto.questionSubmit.JudgeInfo;
import com.fxm.fansoj.model.entity.QuestionSubmit;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;

@Data

public class QuestionSubmitVO implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * language
     */
    private String language;

    /**
     * 用户代码
     */
    private String code;

    /**
     * 判题信息（json对象）
     */
    private JudgeInfo judgeInfo;

    /**
     * 判题状态：0-待判题，1-判题中，2-成功，3-失败
     */
    private Integer status;

    /**
     * 题目id
     */
    private Long questionId;

    /**
     * 创建用户id
     */
    private Long userId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    private UserVO userVO;

    private QuestionVO questionVO;


    private static final long serialVersionUID = 1L;

    /**
     * 包装类转对象
     *
     * @param questionVO
     * @return
     */
    public static QuestionSubmit voToObj(QuestionSubmitVO questionVO) {
        if (questionVO == null) {
            return null;
        }
        QuestionSubmit questionSubmit = new QuestionSubmit();
        BeanUtils.copyProperties(questionVO, questionSubmit);

        JudgeInfo JudgeInfo = questionVO.getJudgeInfo();
        if (JudgeInfo != null) {
            questionSubmit.setJudgeInfo(JSONUtil.toJsonStr(JudgeInfo));
        }
        return questionSubmit;
    }

    /**
     * 对象转包装类
     *
     * @param question
     * @return
     */
    public static QuestionSubmitVO objToVo(QuestionSubmit question) {
        if (question == null) {
            return null;
        }
        QuestionSubmitVO questionVO = new QuestionSubmitVO();
        BeanUtils.copyProperties(question, questionVO);
        questionVO.setJudgeInfo(JSONUtil.toBean(question.getJudgeInfo(), JudgeInfo.class));
        return questionVO;
    }

}
