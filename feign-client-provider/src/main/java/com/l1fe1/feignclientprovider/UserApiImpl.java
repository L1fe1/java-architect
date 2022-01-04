package com.l1fe1.feignclientprovider;

import com.l1fe1.feignapiprovider.UserApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/user")
public class UserApiImpl implements UserApi {

    private static final Logger logger = LoggerFactory.getLogger(UserApiImpl.class);

    @Value("${server.port}")
    String port;

    private AtomicInteger count = new AtomicInteger();

    @Override
    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }

    @Override
    @GetMapping("/timeoutRetry")
    public String timeoutRetry() {
        try {
            logger.info("业务逻辑处理...");
            Thread.sleep(4000);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        logger.info("=====端口为 " + port + " 的服务第" + count.getAndIncrement() + "次调用=====");
        return "port:" + port;
    }

    @Override
    @GetMapping("/param")
    public Map<Integer, String> getForParam(Integer id) {
        return Collections.singletonMap(id, "xiaoming");
    }

    @Override
    @GetMapping("/multiParams")
    public Map<String, Object> getForMultiParams(@RequestParam Map<String, Object> map) {
        return map;
    }

    @Override
    public String fallback() {
        int i = 1 / 0;
        return String.valueOf(i);
    }
}
