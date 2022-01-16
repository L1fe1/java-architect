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
     * 超时重试
     * @return 端口号
     */
    @GetMapping("/timeoutRetry")
    String timeoutRetry();

    /**
     * 传参调用，@RequestParam("id") 必须要写，否则报 500
     * @param id id
     * @return map
     */
    @GetMapping("/param")
    Map<Integer, String> getForParam(@RequestParam("id") Integer id);

    /**
     * 通过 map 传递多个参数
     * @param map 参数 map
     * @return map
     */
    @GetMapping("/multiParams")
    Map<String, Object> getForMultiParams(@RequestParam Map<String, Object> map);

    /**
     * api 接口 fallback
     * @return fallback
     */
    @GetMapping("/fallback")
    String fallback();

    /**
     * api 接口 zuul
     * @return provider consumer 端口
     */
    @GetMapping("/zuul")
    String zuul();
}
