package com.fxm.fansoj.model.dto.questionSubmit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExecuteQuestionRequest {
    private String language;
    private String code;
    private List<String> inputList;
}
