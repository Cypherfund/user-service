package com.cypherfund.campaign.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@EnableDiscoveryClient
@SpringBootApplication
@ComponentScan(basePackages = {
		"com.cypherfund.campaign.user",
		"com.cypherfund.campaign.api.model",
		"com.cypherfund.campaign.api.exceptions",
		"com.cypherfund.campaign.api.configs",
		"com.cypherfund.campaign.api.user",
		"com.cypherfund.campaign.util.configs"
})
public class UserApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserApplication.class, args);
	}

}
