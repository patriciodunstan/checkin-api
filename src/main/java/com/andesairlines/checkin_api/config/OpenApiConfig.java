package com.andesairlines.checkin_api.config;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI(){
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
                        new Server().url("http://localhost:8080/api").description("Developmnent server"),
                        new Server().url("https://api.andesairlines.com").description("Production server")
                ));
    }
}
