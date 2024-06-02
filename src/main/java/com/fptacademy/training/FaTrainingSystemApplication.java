package com.fptacademy.training;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;


@SpringBootApplication
@EnableJpaAuditing
@SecurityScheme(
		name = "token_auth",
		type = SecuritySchemeType.HTTP,
		scheme = "bearer",
		bearerFormat = "JWT"
)
@OpenAPIDefinition(
		info = @Info(
				title = "Swagger for FPT Academy Training System",
				description = "This is list of endpoints and documentations of REST API for FPT Academy Training System",
				version = "1.0"
		),
		servers = {
				@Server(url = "http://localhost:8080", description = "Local development server"),
				@Server(url = "http://localhost:5000", description = "Local production server")
		},
		tags = {
				@Tag(name = "authentication", description = "REST API endpoints for authentication"),
				@Tag(name = "program", description = "REST API endpoints for training program"),
				@Tag(name = "class", description = "REST API endpoints for classes"),
				@Tag(name = "Syllabus", description = "REST API endpoints for syllabuses")
		}
)
public class FaTrainingSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(FaTrainingSystemApplication.class, args);
	}
}
