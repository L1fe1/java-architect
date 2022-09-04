package com.l1fe1.demoagent;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.instrument.Instrumentation;

public class DemoAgent {
    public static void premain(String agentArgs, Instrumentation inst) {
        System.out.println("this is a java agent with two args");
        System.out.println("参数:" + agentArgs + "\n");

        // Byte Buddy 会根据 Transformer 指定的规则进行拦截并增强代码
        AgentBuilder.Transformer transformer =
                (builder, typeDescription, classLoader, module, protectionDomain) -> {
                    // method() 指定哪些方法需要被拦截，ElementMatchers.any() 表示拦截所有方法
                    return builder.method(
                            ElementMatchers.any())
                            // intercept() 指明拦截上述方法的拦截器
                            .intercept(MethodDelegation.to(TimeInterceptor.class));
                };
        // Byte Buddy 专门有个 AgentBuilder 来处理 Java Agent 的场景
        new AgentBuilder
                .Default()
                // 根据包名前缀拦截类
                .type(ElementMatchers.nameStartsWith("com.l1fe1"))
                // 拦截到的类由 transformer 处理
                .transform(transformer)
                // 安装到 Instrumentation
                .installOn(inst);
    }

    public static void premain(String agentArgs) {
        System.out.println("this is a java only one arg");
        System.out.println("参数:" + agentArgs + "\n");
    }
}
