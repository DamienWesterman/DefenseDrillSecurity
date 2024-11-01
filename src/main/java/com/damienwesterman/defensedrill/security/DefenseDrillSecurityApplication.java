package com.damienwesterman.defensedrill.security;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class DefenseDrillSecurityApplication {

	public static void main(String[] args) {
		SpringApplication.run(DefenseDrillSecurityApplication.class, args);
	}

}
