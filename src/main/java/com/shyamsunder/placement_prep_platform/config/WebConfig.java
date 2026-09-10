package com.shyamsunder.placement_prep_platform.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@ConditionalOnProperty(name = "file.public-upload-route.enabled", havingValue = "true", matchIfMissing = false)
public class WebConfig implements WebMvcConfigurer {
    // Public /uploads/** resource handler is disabled by default for security.
    // Resumes are securely served via authenticated endpoint GET /api/resumes/{id}/download
}
