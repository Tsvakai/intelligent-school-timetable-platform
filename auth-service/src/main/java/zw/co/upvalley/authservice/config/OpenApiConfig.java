package zw.co.upvalley.authservice.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${app.api.version:v1.0.0}")
    private String apiVersion;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("ISTMS Auth Service")
                        .version(apiVersion)
                        .description("Authentication and Authorization Microservice for Intelligent School Timetable Management System")
                        .contact(new Contact()
                                .name("Upvalley Development Team")
                                .email("dev@upvalley.co.zw"))
                        .license(new License()
                                .name("Proprietary")
                                .url("https://upvalley.co.zw/license")))
                .servers(List.of(
                        new Server()
                                .url("/auth")
                                .description("Auth Service API")
                ))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }
}