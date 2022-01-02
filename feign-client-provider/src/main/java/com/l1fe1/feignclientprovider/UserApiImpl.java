package com.l1fe1.feignclientprovider;

import com.l1fe1.feignapiprovider.UserApi;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserApiImpl implements UserApi {
    @Override
    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }

    @GetMapping("/param")
    public Map<Integer, String> getForParam(Integer id) {
        return Collections.singletonMap(id, "xiaoming");
    }
}
