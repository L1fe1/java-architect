package com.l1fe1.eurekaclientprovider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.util.Collections;
import java.util.Map;

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

    @GetMapping("/entity")
    public String getForEntity() {
        return "get for entity.";
    }

    @GetMapping("/map")
    public Map<String, String> getForMap() {
        Map<String, String> map = Collections.singletonMap("id", "1");
        return map;
    }

    @GetMapping("/param")
    public Person getForParam(Integer id, String name) {
        Person person = new Person();
        person.setId(id);
        person.setName(name);
        return person;
    }

    @PostMapping("/param")
    public Person postForParam(@RequestBody Person person) {
        return person;
    }

    @PostMapping("/location")
    public URI postForLocation(@RequestBody Person person, HttpServletResponse response) throws Exception {
        URI uri = new URI("https://www.baidu.com/s?wd=" + person.getName());
        response.addHeader("Location", uri.toString());
        return uri;
    }

    @PostMapping("/exchange")
    public Person exchange(@RequestBody Person person) {
        return person;
    }

    @GetMapping("/fallback")
    public String fallback() {
        int i = 1 / 0;
        return String.valueOf(i);
    }
}
