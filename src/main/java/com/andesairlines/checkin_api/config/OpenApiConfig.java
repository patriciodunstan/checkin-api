package com.andesairlines.checkin_api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Andes Airlines Check-in API")
                        .description("API for managing flight check-in operations")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Andes Airlines Development Team")
                                .email("dev@andesairlines.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Development server"),
                        new Server().url("https://checkin-api-idfh.onrender.com").description("Production server")
                ));
    }

    @Bean
    public OpenApiCustomizer openApiCustomiser() {
        return openApi -> openApi.getPaths().values().stream()
                .flatMap(pathItem -> pathItem.readOperations().stream())
                .forEach(operation -> {
                    operation.addParametersItem(new Parameter()
                            .name("Accept")
                            .in("header")
                            .required(false)
                            .schema(new StringSchema()._default("application/json")));
                });
    }
}