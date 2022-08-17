package com.l1fe1.demoprovider.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.l1fe1.demoapi.service.HelloService;
import org.springframework.stereotype.Component;

@Service
@Component
public class DefaultHelloService implements HelloService {
    @Override
    public String say(String name) throws Exception {
        Thread.sleep(2000);
        return "hello" + name;
    }
}
