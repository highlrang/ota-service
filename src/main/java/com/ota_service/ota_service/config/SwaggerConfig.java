package com.ota_service.ota_service.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    private static final String BEARER_SCHEME = "bearerAuth";

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("OTA Service API")
                        .description("Seller API / Customer API 문서")
                        .version("v1"))
                .components(new Components()
                        .addSecuritySchemes(BEARER_SCHEME, new SecurityScheme()
                                .name(BEARER_SCHEME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")))
                .addSecurityItem(new SecurityRequirement().addList(BEARER_SCHEME));
    }

    @Bean
    public GroupedOpenApi sellerApi() {
        return GroupedOpenApi.builder()
                .group("Seller API")
                .pathsToMatch("/api/seller/**")
                .build();
    }

    @Bean
    public GroupedOpenApi customerApi() {
        return GroupedOpenApi.builder()
                .group("Customer API")
                .pathsToMatch("/api/customer/**")
                .build();
    }
}
