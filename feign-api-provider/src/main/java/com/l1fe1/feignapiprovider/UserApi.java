package com.l1fe1.feignapiprovider;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/user")
public interface UserApi {
    /**
     * api 接口 hello
     * @return hello
     */
    @GetMapping("/hello")
    String hello();
}
