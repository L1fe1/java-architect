package com.l1fe1.feignapiprovider;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@RequestMapping("/user")
public interface UserApi {
    /**
     * api 接口 hello
     * @return hello
     */
    @GetMapping("/hello")
    String hello();

    /**
     * 传参调用，@RequestParam("id") 必须要写，否则报 500
     * @param id id
     * @return map
     */
    @GetMapping("/param")
    Map<Integer, String> getForParam(@RequestParam("id") Integer id);
}
