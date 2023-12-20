package com.fxm.fansoj.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 题目提交表
 * @TableName question_submit
 */
@TableName(value ="question_submit")
@Data
public class QuestionSubmit implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
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
    private String judgeinfo;

    /**
     * 判题状态：0-待判题，1-判题中，2-成功，3-失败
     */
    private Integer status;

    /**
     * 题目id
     */
    private Long questionid;

    /**
     * 创建用户id
     */
    private Long userid;

    /**
     * 题目答案
     */
    private String answer;

    /**
     * 提交数
     */
    private Integer submitnum;

    /**
     * 通过数
     */
    private Integer acceptnum;

    /**
     * 创建时间
     */
    private Date createtime;

    /**
     * 更新时间
     */
    private Date updatetime;

    /**
     * 是否删除
     */
    private Integer isdelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}