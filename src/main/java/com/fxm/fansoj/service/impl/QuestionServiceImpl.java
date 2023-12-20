package com.fxm.fansoj.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fxm.fansoj.model.entity.Question;
import com.fxm.fansoj.service.QuestionService;
import com.fxm.fansoj.mapper.QuestionMapper;
import org.springframework.stereotype.Service;

/**
* @author xiang
* @description 针对表【question(题目)】的数据库操作Service实现
* @createDate 2023-12-20 23:08:25
*/
@Service
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question>
    implements QuestionService{

}




