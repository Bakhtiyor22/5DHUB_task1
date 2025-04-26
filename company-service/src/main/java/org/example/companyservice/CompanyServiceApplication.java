package org.example.companyservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
// Add annotations for Cloud features later:
// import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
// import org.springframework.cloud.openfeign.EnableFeignClients;


@SpringBootApplication
// @EnableDiscoveryClient // Add later for Eureka
// @EnableFeignClients // Add later for Feign
public class CompanyServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CompanyServiceApplication.class, args);
	}

	// Define RestTemplate Bean
	@Bean
	// @LoadBalanced // Add later - needed when using Service Discovery with RestTemplate
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

}