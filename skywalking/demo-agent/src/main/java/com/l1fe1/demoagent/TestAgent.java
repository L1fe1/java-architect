package com.l1fe1.demoagent;

import java.lang.instrument.Instrumentation;

public class TestAgent { 
    public static void premain(String agentArgs, Instrumentation inst) throws Exception {
        // 注册一个 Transformer，该 Transformer 在类加载时被调用
        inst.addTransformer(new Transformer(), true);
        inst.retransformClasses(DemoClass.class);
        System.out.println("premain done");
    }
}
