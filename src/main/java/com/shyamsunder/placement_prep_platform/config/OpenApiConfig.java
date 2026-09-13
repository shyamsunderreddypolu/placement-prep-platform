package com.shyamsunder.placement_prep_platform.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    public static final String SECURITY_SCHEME_NAME = "BearerAuth";

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Placement Readiness & Preparation Platform API")
                        .description("Production-grade RESTful API providing DSA practice repositories, " +
                                "1-4-7 spaced repetition revision tracking, transparent ATS resume scoring, " +
                                "and deterministic placement readiness index calculations.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Engineering Team")
                                .email("support@placementprep.dev"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Local Development Server")
                ))
                .tags(List.of(
                        new Tag().name("Authentication").description("Endpoints for user registration, authentication, JWT tokens, and session management"),
                        new Tag().name("DSA Problems").description("Endpoints for querying problem repositories, patterns, and administrative problem creation"),
                        new Tag().name("Submissions & History").description("Endpoints for logging problem attempts and retrieving submission history"),
                        new Tag().name("1-4-7 Spaced Repetition").description("Endpoints for spaced repetition revision queues and interval feedback"),
                        new Tag().name("Placement Readiness").description("Endpoints for multi-dimensional placement readiness score calculations"),
                        new Tag().name("Resume Management").description("Endpoints for resume uploads, listings, and secure downloads with ownership validation"),
                        new Tag().name("ATS Resume Evaluator").description("Endpoints for transparent, weighted resume scoring and keyword extraction"),
                        new Tag().name("Dashboard & Analytics").description("Endpoints for dashboard metrics, streaks, and progress visualization")
                ))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME, new SecurityScheme()
                                .name(SECURITY_SCHEME_NAME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Provide your JWT Bearer token to authorize requests. Example: 'eyJhbGciOiJIUzI1NiIsIn...'")));
    }
}