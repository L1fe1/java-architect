package com.l1fe1.demoagent;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        System.out.println(new DemoClass().getNumber());

        while (true) {
            Thread.sleep(1000);
            // 注意，这里是新建的 DemoClass 对象
            System.out.println(new DemoClass().getNumber());
        }
    }
}
