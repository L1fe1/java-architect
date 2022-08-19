package com.l1fe1.demoagent;

import java.lang.instrument.Instrumentation;

public class DemoAgent {
    public static void premain(String agentArgs, Instrumentation inst) {
        System.out.println("this is a java agent with two args");
        System.out.println("参数:" + agentArgs + "\n");
    }

    public static void premain(String agentArgs) {
        System.out.println("this is a java only one arg");
        System.out.println("参数:" + agentArgs + "\n");
    }
}
