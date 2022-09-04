package com.l1fe1.demoagent;

import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;

import java.util.List;

public class AttachMain {
    public static void main(String[] args) throws Exception {
        List<VirtualMachineDescriptor> listBefore =
             VirtualMachine.list();
        // agentmain() 方法所在 jar 包
        String jar = "E:\\IdeaProjects\\java-architect\\skywalking\\demo-agent\\target\\demo-agent-1.0-SNAPSHOT.jar";
        VirtualMachine vm;
        List<VirtualMachineDescriptor> listAfter;
        while (true) {
            listAfter = VirtualMachine.list();
            for (VirtualMachineDescriptor vmd : listAfter) {
                // 发现新的 JVM
                if (!listBefore.contains(vmd)) {
                    // attach 到新 JVM
                    vm = VirtualMachine.attach(vmd);
                    // 加载 agentmain 所在的 jar 包
                    vm.loadAgent(jar);
                    // detach
                    vm.detach();
                    return;
                }
            }
            Thread.sleep(1000);
        }
    }
}

