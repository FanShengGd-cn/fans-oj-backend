package com.fxm.fansoj.proxy;

import com.fxm.fansoj.factory.SandBoxFactory;
import com.fxm.fansoj.judge.codesandbox.CodeSandBox;
import com.fxm.fansoj.model.dto.questionSubmit.ExecuteQuestionRequest;
import com.fxm.fansoj.model.dto.questionSubmit.ExecuteQuestionResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;

@SpringBootTest
class SandBoxProxyTest {

    @Value("${codeSandBox.type}")
    private String type;
    @Test
    void doExecute() {
        // 代理模式和工厂模式体现
        ExecuteQuestionRequest executeQuestionRequest = ExecuteQuestionRequest
                .builder()
                .code("public class Main {public static void main(String[] args) {System.out.println(args[0] + args[1]);System.out.println(args[0] + args[1]);}}")
                .inputList(Arrays.asList("1 2", "1 3", "1 5", "883283oi2809 123981273981247985", "start end", "赛况饭赛况返矿赛返矿赛 爱空来返矿sad返矿;爱冬季疯狂"))
                .language("java")
                .build();

        CodeSandBox codeSandBox = SandBoxFactory.getCodeSandBox(type);
        codeSandBox = new SandBoxProxy(codeSandBox);
        ExecuteQuestionResponse executeQuestionResponse = codeSandBox.doExecute(executeQuestionRequest);
//        System.out.println(executeQuestionResponse);
    }
}