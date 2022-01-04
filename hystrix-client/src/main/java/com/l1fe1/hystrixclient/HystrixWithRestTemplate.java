package com.l1fe1.hystrixclient;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class HystrixWithRestTemplate {
    @Autowired
    private RestTemplate lbRestTemplate;

    @GetMapping("/fallback")
    @HystrixCommand(fallbackMethod = "defaultFallback")
    public String fallback() {
        String url ="http://eureka-client-provider/fallback";
        String object = lbRestTemplate.getForObject(url, String.class);
        return object;
    }

    public String defaultFallback() {
        return "请求失败，执行 fallback 逻辑";
    }

}
