package com.l1fe1.eurekaclientprovider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {
    @Autowired
    private HealthStatusService healthStatusService;

    @Value("${server.port}")
    private String port;

    @GetMapping("/hello")
    public String hello(@RequestParam(defaultValue = "World") String name) {
        return "Hello, " + name;
    }

    @GetMapping("/port")
    public String url() {
        return "访问端口号为 " + port + " 的服务。";
    }

    @GetMapping("/health")
    public Boolean health(@RequestParam("status") Boolean status) {
        healthStatusService.setStatus(status);
        return healthStatusService.getStatus();
    }
}
