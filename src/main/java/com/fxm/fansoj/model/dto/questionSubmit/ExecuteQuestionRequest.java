package com.fxm.fansoj.model.dto.questionSubmit;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ExecuteQuestionRequest {
    private String language;
    private String code;
    private List<String> inputList;
}
