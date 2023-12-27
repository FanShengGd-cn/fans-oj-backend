package com.fxm.fansoj.proxy;

import com.fxm.fansoj.factory.SandBoxFactory;
import com.fxm.fansoj.judge.codesandbox.CodeSandBox;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SandBoxProxyTest {

    @Value("${codeSandBox.type}")
    private String type;
    @Test
    void doExecute() {
        // 代理模式和工厂模式体现
//        ExecuteQuestionRequest executeQuestionRequest = new Exe;

        CodeSandBox codeSandBox = SandBoxFactory.getCodeSandBox(type);
        codeSandBox = new SandBoxProxy(codeSandBox);
//        codeSandBox.doExecute(executeQuestionRequest);
    }
}