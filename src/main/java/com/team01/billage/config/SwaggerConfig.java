package com.team01.billage.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI() {

        return new OpenAPI()
                .info(apiInfo())
                .components(
                        new Components().responses(Map.of())
                );
    }

    private Info apiInfo() {
        return new Info()
                .title("Billage API Docs")
                .description("이 문서는 BILLAGE 서비스에서 제공하는 REST API 명세서입니다.<br/>**참고** websocket publish endpoint는 보이지 않습니다.")
                .version("0.0.4");
    }
}
