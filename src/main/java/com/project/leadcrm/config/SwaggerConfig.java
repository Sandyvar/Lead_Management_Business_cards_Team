package com.project.leadcrm.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Lead Management CRM & Digital Visiting Card API")
                        .version("1.0")
                        .description("REST API documentation for the SaaS Lead Management Platform.\n" +
                                "Includes Notification Service (Day 1) and Activity Timeline API (Day 2-3).")
                        .contact(new Contact()
                                .name("Lead CRM Engineering Team")
                                .email("dev@leadcrm.com"))
                        .license(new License().name("Apache 2.0").url("https://springdoc.org")));
    }
}
