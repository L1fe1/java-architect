package com.l1fe1.eurekaserverha;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class EurekaServerHaApplication {

	public static void main(String[] args) {
		SpringApplication.run(EurekaServerHaApplication.class, args);
	}

}
