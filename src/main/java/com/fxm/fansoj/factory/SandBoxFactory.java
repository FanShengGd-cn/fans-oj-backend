package com.fxm.fansoj.factory;

import com.fxm.fansoj.judge.codesandbox.CodeSandBox;
import com.fxm.fansoj.judge.codesandbox.DefaultSandBox;
import com.fxm.fansoj.judge.codesandbox.RemoteSandBox;
import com.fxm.fansoj.judge.codesandbox.ThirdPartySandBox;

public class SandBoxFactory {

    public static CodeSandBox getCodeSandBox(String type){
        switch (type){
            case "remote":
                return new RemoteSandBox();
            case "third":
                return new ThirdPartySandBox();
            default:
                return new DefaultSandBox();
        }
    }
}
