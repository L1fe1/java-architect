package com.l1fe1.demobytebuddy;

import net.bytebuddy.implementation.bind.annotation.*;

import java.lang.reflect.Method;

public class MorphInterceptor {
    @RuntimeType
    public Object intercept(@This Object obj,
                            // 注入目标方法的全部参数
                            @AllArguments Object[] allArguments,
                            @Origin Method method,
                            @Super DB db,
                            // 通过 @Morph 注解注入
                            @Morph OverrideCallable callable
    ) throws Throwable {
        try {
            System.out.println("before");
            // 通过 OverrideCallable.call() 方法调用目标方法，此时需要传递参数
            Object result = callable.call(allArguments);
            System.out.println("after");
            return result;
        } catch (Throwable t) {
            throw t;
        } finally {
            System.out.println("finally");
        }
    }
}