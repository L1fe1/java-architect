package com.l1fe1.demoagent;

import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

public class TimeInterceptor {
    @RuntimeType
    public static Object intercept(@Origin Method method,
          @SuperCall Callable<?> callable) throws Exception {
        long start = System.currentTimeMillis();
        try {
			// 执行原函数
            return callable.call();
        } finally {
            System.out.println(method.getName() + ":"
                    + (System.currentTimeMillis() - start) + "ms");
        }
    }
}

