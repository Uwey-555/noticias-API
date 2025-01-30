package com.api.bbc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class BbcApplication {

	public static void main(String[] args) {
		SpringApplication.run(BbcApplication.class, args);
	}

}
