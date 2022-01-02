package com.l1fe1.feignclientconsumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class FeignClientConsumerApplication {

	public static void main(String[] args) {
		SpringApplication.run(FeignClientConsumerApplication.class, args);
	}

}
