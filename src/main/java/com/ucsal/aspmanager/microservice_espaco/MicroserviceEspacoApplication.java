package com.ucsal.aspmanager.microservice_espaco;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class MicroserviceEspacoApplication {

	public static void main(String[] args) {
		SpringApplication.run(MicroserviceEspacoApplication.class, args);
	}

}
