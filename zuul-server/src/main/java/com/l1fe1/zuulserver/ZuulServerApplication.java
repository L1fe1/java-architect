package com.l1fe1.zuulserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

@SpringBootApplication
/**
 * 该注解声明这是一个 zuul 代理。
 * 该代理使用 ribbon 来定位注册到 eureka server 上的微服务，同时整合了 hystrix，实现了容错。
 */
@EnableZuulProxy
public class ZuulServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ZuulServerApplication.class, args);
	}

}
