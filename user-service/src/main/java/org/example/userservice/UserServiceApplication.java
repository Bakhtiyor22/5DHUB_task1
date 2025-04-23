package org.example.userservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
// Add annotations for Cloud features later:
// import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
// import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
// @EnableDiscoveryClient // Add later for Eureka
// @EnableFeignClients // Add later for Feign
public class UserServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserServiceApplication.class, args);
	}

	// Define RestTemplate Bean if using it (add in Phase 2)
    /*
    @Bean
    @LoadBalanced // Add later - needed when using Service Discovery with RestTemplate
    public RestTemplate restTemplate() {
         return new RestTemplate();
    }
    */
}
