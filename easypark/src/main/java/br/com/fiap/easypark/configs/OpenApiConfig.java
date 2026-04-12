package br.com.fiap.easypark.configs;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI easyparkOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("EasyPark API")
                        .version("1.0.0-sprint3")
                        .description("API da Sprint 3 FIAP para gestao de estacionamentos smart")
                        .contact(new Contact()
                                .name("Equipe EasyPark")
                                .email("contato@easypark.fiap.com"))
                        .license(new License()
                                .name("MIT")))
                .components(new Components()
                        .addSecuritySchemes("firebaseAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("Firebase ID Token")))
                .externalDocs(new ExternalDocumentation()
                        .description("Repositorio")
                        .url("https://github.com/fiap/easypark"));
    }
}
