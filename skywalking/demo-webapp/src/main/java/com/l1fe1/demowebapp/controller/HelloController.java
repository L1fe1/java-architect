package com.l1fe1.demowebapp.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.l1fe1.demoapi.service.HelloService;
import org.apache.skywalking.apm.toolkit.trace.ActiveSpan;
import org.apache.skywalking.apm.toolkit.trace.TraceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class HelloController {
    @Reference
    private HelloService helloService;

    private static final Logger logger = LoggerFactory.getLogger(HelloController.class);

    @GetMapping("/hello/{words}")
    public String hello(@PathVariable("words") String words) throws Exception {
        Thread.sleep(1000);
        // TraceContext 工具类定义在 apm-toolkit-trace 依赖包中
        logger.info("traceId:{}", TraceContext.traceId());
        ActiveSpan.tag("hello-trace", words);
        String say = helloService.say(words);
        return say;
    }

    @GetMapping("/err")
    public String err() {
        String traceId = TraceContext.traceId();
        logger.info("traceId:{}", traceId);
        ActiveSpan.tag("error-trace activation", "error");
        throw new RuntimeException("err");
    }
}
