package com.l1fe1.hystrixclient;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class StandaloneHystrix extends HystrixCommand {

    private static final Logger logger = LoggerFactory.getLogger(StandaloneHystrix.class);

    protected StandaloneHystrix(HystrixCommandGroupKey group) {
        super(group);
    }

    @Override
    protected Object run() throws Exception {
        logger.info("执行业务逻辑");
        int i = 1 / 0;
        return i;
    }

    @Override
    protected Object getFallback() {
        logger.info("异常 fallback 逻辑");
        return "-1";
    }

    public static void main(String[] args) {
        /**
         * execute()：以同步阻塞方式执行 run()。
         * 调用 execute() 后，hystrix 先创建一个新线程运行 run()。
         * 接着调用程序要在 execute() 调用处一直阻塞着，直到 run() 运行完成。
         * queue()：以异步非阻塞方式执行 run()。
         * 一调用 queue() 就直接返回一个Future对象，同时 hystrix 创建一个新线程运行 run()。
         * 调用程序通过 Future.get() 拿到 run() 的返回结果，而 Future.get() 是阻塞执行的。
         */
        Future<String> future = new StandaloneHystrix(HystrixCommandGroupKey.Factory.asKey("ext")).queue();
        String result = "";
        try {
            result = future.get();
        } catch (InterruptedException | ExecutionException e) {
            logger.error(e.getMessage());
        }
        logger.info("程序的执行结果为：{}", result);
    }
}
