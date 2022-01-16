package com.l1fe1.feignclientconsumer;

import com.l1fe1.feignapiprovider.UserApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class DemoController {
    @Autowired
    private UserApi userApi;

    @Value("${server.port}")
    String port;

    @GetMapping("/hello")
    public String hello() {
        return userApi.hello();
    }

    @GetMapping("/timeoutRetry")
    public String timeoutRetry() {
        return userApi.timeoutRetry();
    }

    @GetMapping("/param")
    public Map<Integer, String> getForParam(Integer id) {
        return userApi.getForParam(id);
    }

    @GetMapping("/multiParams")
    public Map<String, Object> getForMultiParams(@RequestParam Map<String, Object> map) {
        return userApi.getForMultiParams(map);
    }

    @GetMapping("/fallback")
    public String fallback() {
        return userApi.fallback();
    }

    @GetMapping("/zuul")
    public String zuul() {
        return "Consumer:" + port + "=====>>>>>" + userApi.zuul();
    }
}
