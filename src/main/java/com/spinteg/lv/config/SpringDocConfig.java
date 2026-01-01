package com.spinteg.lv.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringDocConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String scheme = "bearerAuth";

        return new OpenAPI()
                .openapi("3.0.1")
                .info(new Info()
                        .title("Lease Valuator API")
                        .version("1.0.0")
                        .description("API documentation for the Lease Valuator Service"))
                .addSecurityItem(new SecurityRequirement().addList(scheme))
                .components(new Components()
                        .addSecuritySchemes(scheme,
                                new SecurityScheme()
                                        .name(scheme)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }

    /**
     * Configure Swagger to include all endpoints, regardless of prefix.
     */
    @Bean
    public GroupedOpenApi leaseValuatorApi() {
        return GroupedOpenApi.builder()
                .group("lease-valuator")
                .pathsToMatch("/**") // ✅ Matches all controllers (no need for /api/)
                .build();
    }
}
