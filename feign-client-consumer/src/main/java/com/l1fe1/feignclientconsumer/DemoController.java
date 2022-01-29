package com.l1fe1.feignclientconsumer;

import com.l1fe1.feignapiprovider.UserApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
public class DemoController {
    @Autowired
    private UserApi userApi;

    @Value("${server.port}")
    String port;

    private static final Logger logger = LoggerFactory.getLogger(DemoController.class);

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

    @GetMapping("/token")
    public String token(HttpServletRequest request) {
        String token = request.getHeader("token");
        String cookie = request.getHeader("Cookie");
        logger.info("token:{}", token);
        logger.info("cookie:{}", cookie);
        return "token: " + token + ",cookie: " + cookie;
    }
}
