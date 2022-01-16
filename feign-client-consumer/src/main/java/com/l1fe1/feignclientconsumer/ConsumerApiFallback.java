package com.l1fe1.feignclientconsumer;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ConsumerApiFallback implements ConsumerApi {

    public String hello() {
        return null;
    }

    @Override
    public String timeoutRetry() {
        return null;
    }

    @Override
    public Map<Integer, String> getForParam(Integer id) {
        return null;
    }

    @Override
    public Map<String, Object> getForMultiParams(Map<String, Object> map) {
        return null;
    }

    @Override
    public String fallback() {
        return "fallback() 接口请求失败，执行 fallback 逻辑";
    }

    @Override
    public String zuul() {
        return null;
    }
}
