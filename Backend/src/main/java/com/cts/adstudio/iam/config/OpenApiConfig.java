package com.cts.adstudio.iam.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger / OpenAPI configuration. Registers a Bearer-JWT scheme so the
 * "Authorize" button in Swagger UI accepts the login token.
 */
@Configuration
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "Paste the JWT returned by POST /api/auth/login"
)
public class OpenApiConfig {

    @Bean
    public OpenAPI adStudioOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("AdStudio - Identity & Access Management API")
                        .description("Authentication, RBAC, and audit-trail APIs for the AdStudio platform (Phase 1).")
                        .version("v1")
                        .contact(new Contact().name("AdStudio Team").email("support@adstudio.com"))
                        .license(new License().name("Proprietary")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
}
