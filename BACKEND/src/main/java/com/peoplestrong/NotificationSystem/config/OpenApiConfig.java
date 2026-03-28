package com.peoplestrong.NotificationSystem.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Pulse Insights - Notification System API")
                        .version("1.0")
                        .description("Professional API documentation for the Pulse Insights Notification System. " +
                                "This API handles authentication, notification broadcasting, and user dashboard actions.")
                        .contact(new Contact()
                                .name("System Support")
                                .email("support@peoplestrong.com")));
    }
}
