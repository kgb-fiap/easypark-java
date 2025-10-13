package br.com.fiap.easypark;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(info = @Info(title = "EasyPark API", version = "v1-sprint1", description = "API da Sprint 1 – FIAP"))
@SpringBootApplication
public class EasyparkApplication {

	public static void main(String[] args) {
		SpringApplication.run(EasyparkApplication.class, args);
	}

}
