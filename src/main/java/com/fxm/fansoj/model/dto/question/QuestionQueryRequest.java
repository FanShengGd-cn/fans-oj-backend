package com.fxm.fansoj.model.dto.question;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fxm.fansoj.common.PageRequest;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 题目查询
 */
@Data
public class QuestionQueryRequest extends PageRequest implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;
    /**
     * 搜索词
     */
    private String searchText;

    /**
     * 标签列表（json 数组）
     */
    private List<String> tags;


    /**
     * 创建用户 id
     */
    private Long userId;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
