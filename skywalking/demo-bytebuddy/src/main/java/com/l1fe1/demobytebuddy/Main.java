package com.l1fe1.demobytebuddy;

import lombok.SneakyThrows;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.SuperMethodCall;
import net.bytebuddy.implementation.bind.annotation.Morph;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static net.bytebuddy.dynamic.loading.ClassLoadingStrategy.Default.INJECTION;
import static net.bytebuddy.matcher.ElementMatchers.*;

public class Main {
    @SneakyThrows
    public static void enhanceToString() {
        String str = new ByteBuddy() // 创建 ByteBuddy 对象
                // subclass 增强方式
                .subclass(Object.class)
                // 新类型的类名
                .name("com.example.Type")
                // 拦截其中的 toString() 方法
                .method(named("toString"))
                // 让 toString() 方法返回固定值
                .intercept(FixedValue.value("Hello World!"))
                .make()
                // 加载新类型，默认 WRAPPER 策略
                .load(ByteBuddy.class.getClassLoader())
                .getLoaded() // 通过 Java反射创建 com.example.Type 实例
                .newInstance()  // 调用 toString() 方法
                .toString();
        System.out.println(str);
    }

    @SneakyThrows
    public void multiMethod() {
        Foo dynamicFoo = new ByteBuddy()
                .subclass(Foo.class)
                // 匹配 Foo 中所有的方法
                .method(isDeclaredBy(Foo.class))
                .intercept(FixedValue.value("One!"))
                // 匹配名为 foo 的方法
                .method(named("foo"))
                .intercept(FixedValue.value("Two!"))
                // 匹配名为 foo 且只有一个参数的方法
                .method(named("foo").and(takesArguments(1)))
                .intercept(FixedValue.value("Three!"))
                .make()
                .load(getClass().getClassLoader(), INJECTION)
                .getLoaded()
                .newInstance();
        System.out.println(dynamicFoo.bar());
        System.out.println(dynamicFoo.foo());
        System.out.println(dynamicFoo.foo(null));
    }

    @SneakyThrows
    public static void methodDelegation() {
        String helloWorld = new ByteBuddy()
                .subclass(DB.class)
                .method(named("hello"))
                // 拦截 DB.hello() 方法，并委托给 Interceptor 中的静态方法处理
                .intercept(MethodDelegation.to(Interceptor.class))
                .make()
                .load(ClassLoader.getSystemClassLoader(), INJECTION)
                .getLoaded()
                .newInstance()
                .hello("World");
        System.out.println(helloWorld);
    }

    @SneakyThrows
    public static void annotatedInterceptor() {
        String helloWorld = new ByteBuddy()
                .subclass(DB.class)
                .method(named("hello"))
                // 拦截 DB.hello() 方法，并委托给 Interceptor 中的静态方法处理
                .intercept(MethodDelegation.to(AnnotatedInterceptor.class))
                .make()
                .load(ClassLoader.getSystemClassLoader(), INJECTION)
                .getLoaded()
                .newInstance()
                .hello("World");
        System.out.println(helloWorld);
    }

    @SneakyThrows
    public static void constructorInterceptor() {
        Constructor<? extends DB2> constructor = new ByteBuddy()
                .subclass(DB2.class)
                // 通过constructor() 方法拦截所有构造方法
                .constructor(any())
                // 拦截的操作：首先调用目标对象的构造方法，根据前面自动匹配，这里直接匹配到参数为 String.class 的构造方法
                .intercept(SuperMethodCall.INSTANCE.andThen(
                        // 执行完原始构造方法，再开始执行 interceptor 的代码
                        MethodDelegation.withDefaultConfiguration().to(new ConstructorInterceptor())))
                .make().load(Main.class.getClassLoader(), INJECTION)
                .getLoaded()
                .getConstructor(String.class);
        // 下面通过反射创建生成类型的对象
        constructor.newInstance("MySQL");
    }

    @SneakyThrows
    public static void dynamicModifyParam() {
        String hello = new ByteBuddy()
                .subclass(DB.class)
                .method(named("hello"))
                .intercept(MethodDelegation.withDefaultConfiguration()
                        .withBinders(
                                // 要用@Morph注解之前，需要通过 Morph.Binder 告诉 Byte Buddy 要注入的参数是什么类型
                                Morph.Binder.install(OverrideCallable.class)
                        )
                        .to(new MorphInterceptor()))
                .make()
                .load(Main.class.getClassLoader(), INJECTION)
                .getLoaded()
                .newInstance()
                .hello("World");
    }

    @SneakyThrows
    public static void defineMethodAndFieldImplementInterface() {
        Class<? extends Foo> loaded = new ByteBuddy()
                .subclass(Foo.class)
                // 定义方法的名称
                .defineMethod("moon",
                        // 方法的返回值
                        String.class,
                        // public 修饰
                        Modifier.PUBLIC)
                // 新增方法的参数
                .withParameter(String.class, "s")
                // 方法的具体实现，返回固定值
                .intercept(FixedValue.value("Zero!"))
                // 新增一个字段，该字段名称为 "name"，类型是 String，且用 public 修饰
                .defineField("name", String.class, Modifier.PUBLIC)
                // 实现 DemoInterface 接口
                .implement(DemoInterface.class)
                // 实现 DemoInterface 接口的方式是读写 name 字段
                .intercept(FieldAccessor.ofField("name"))
                .make().load(Main.class.getClassLoader(),
                ClassLoadingStrategy.Default.INJECTION)
                // 获取加载后的 Class
                .getLoaded();

        // 反射
        Foo dynamicFoo = loaded.newInstance();
        // 要调用新定义的 moon() 方法，只能通过反射方式
        Method m = loaded.getDeclaredMethod("moon", String.class);
        System.out.println(m.invoke(dynamicFoo, ""));
        // 通过反射方式读写新增的 name 字段
        Field field = loaded.getField("name");
        field.set(dynamicFoo, "Zero-Name");
        System.out.println(field.get(dynamicFoo));
       // 通过反射调用 DemoInterface 接口中定义的 get() 和 set() 方法，读取 name 字段的值
        Method setNameMethod = loaded.getDeclaredMethod("set", String.class);
        setNameMethod.invoke(dynamicFoo, "Zero-Name2");
        Method getNameMethod = loaded.getDeclaredMethod("get");
        System.out.println(getNameMethod.invoke(dynamicFoo));
    }

    @SneakyThrows
    public static void main(String[] args) {
        Main main = new Main();

        enhanceToString();

        main.multiMethod();

        methodDelegation();

        annotatedInterceptor();

        dynamicModifyParam();

        constructorInterceptor();

        defineMethodAndFieldImplementInterface();
    }
}

// Foo 中定义了三个方法
class Foo {
    public String bar() { return null; }
    public String foo() { return null; }
    public String foo(Object o) { return null;
    }
}

class DB {
    public String hello(String name) {
        System.out.println("DB:" + name);
        return null;
    }
}

// 只有一个有参数的构造方法
class DB2 {
    public DB2(String name) { System.out.println("DB2:" + name); }
}

class Interceptor {
    public static String intercept(String name) { return "String"; }
    public static String intercept(int i) { return "int"; }
    public static String intercept(Object o) { return "Object";}
}