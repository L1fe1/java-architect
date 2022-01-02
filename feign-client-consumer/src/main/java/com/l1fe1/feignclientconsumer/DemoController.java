package com.l1fe1.feignclientconsumer;

import com.l1fe1.feignapiprovider.UserApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {
    @Autowired
    private UserApi userApi;

    @GetMapping("/hello")
    public String hello() {
        return userApi.hello();
    }
}
