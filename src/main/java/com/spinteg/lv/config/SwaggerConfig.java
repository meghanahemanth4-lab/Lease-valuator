package com.spinteg.lv.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        Server qaServer = new Server()
                .url("https://qa-lva-api.spironet.com")
                .description("QA API server");
        Server localServer = new Server()
                .url("http://localhost:8080")
                .description("Local API server");
        Server localServer2 = new Server()
                .url("http://192.168.0.217:8080")
                .description("Local API server");

        return new OpenAPI()
                .info(new Info()
                        .title("Lease Valuator API")
                        .version("1.0.0")
                        .description("API documentation for Lease Valuator QA environment"))
                .servers(List.of(qaServer, localServer, localServer2));
    }
}


