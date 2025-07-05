package com.example.minet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "com.example.minet")
@EntityScan(basePackages = "com.example.minet.entities")
@EnableScheduling
public class MinetApplication {

	public static void main(String[] args) {
		SpringApplication.run(MinetApplication.class, args);
	}

}
