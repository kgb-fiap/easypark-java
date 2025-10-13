package br.com.fiap.easypark.configs;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.*;
import io.swagger.v3.oas.models.ExternalDocumentation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI easyparkOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("EasyPark API")
                        .version("v1-sprint1")
                        .description("API da Sprint 1 – FIAP")
                        .contact(new Contact().name("Equipe EasyPark"))
                        .license(new License().name("MIT")))
                .externalDocs(new ExternalDocumentation()
                        .description("Repositório")
                        .url("https://github.com/<org>/<repo>"));
    }
}