package com.l1fe1.demobytebuddy;

import net.bytebuddy.implementation.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

public class AnnotatedInterceptor {
    @RuntimeType
    public static Object intercept(
			// 目标对象
			@This Object obj,
			// 注入目标方法的全部参数
			@AllArguments Object[] allArguments,
			// 调用目标方法，必不可少
			@SuperCall Callable<?> superCall,
			// 目标方法
			@Origin Method method,
			// 目标对象
			@Super DB db
    ) throws Exception {
		System.out.println(obj);
		System.out.println(db);
		// 从上面两行输出可以看出，obj 和 db 是一个对象
		// 调用目标方法
		return superCall.call();
	}
}
