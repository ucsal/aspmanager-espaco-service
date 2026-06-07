package com.ucsal.aspmanager.microservice_espaco;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import io.swagger.v3.oas.annotations.servers.Server;

@SpringBootApplication
@OpenAPIDefinition(
	info = @Info(
		title = "ASPManager API - Espaços", version = "1.0", 
		description = "Microserviço de Gestão e Solicitação de Espaços Físicos"),
 	servers = {
        @Server(url = "http://localhost:8084", description = "Ambiente Local (Desenvolvimento)"),
        @Server(url = "http://localhost:8080/api/v1/orq/espaco", description = "API Gateway (Produção)")
    })
@EnableDiscoveryClient
public class MicroserviceEspacoApplication {

	public static void main(String[] args) {
		SpringApplication.run(MicroserviceEspacoApplication.class, args);
	}

}
