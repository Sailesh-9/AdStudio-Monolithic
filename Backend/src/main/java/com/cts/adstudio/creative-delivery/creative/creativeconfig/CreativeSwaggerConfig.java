package com.cts.creative.creativeconfig;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CreativeSwaggerConfig {

    @Bean
    public OpenAPI creativeAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Creative Module API")
                        .version("1.0"));
    }
}