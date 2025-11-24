package com.partcraft.back.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "PartCraft API",
                version = "1.0.0",
                description = "REST API for PartCraft - PC component management and configuration platform. " +
                        "This API allows users to browse PC components, create custom PC builds, track prices, and manage their profiles.",
                contact = @Contact(
                        name = "PartCraft Team",
                        email = "support@partcraft.com"
                ),
                license = @License(
                        name = "MIT License",
                        url = "https://opensource.org/licenses/MIT"
                )
        ),
        servers = {
                @Server(
                        url = "http://localhost:8080",
                        description = "Local Development Server"
                ),
                @Server(
                        url = "http://20.19.80.10:8080/",
                        description = "Production Server"
                )
        }
)
@SecurityScheme(
        name = "cookieAuth",
        type = SecuritySchemeType.APIKEY,
        in = io.swagger.v3.oas.annotations.enums.SecuritySchemeIn.COOKIE,
        paramName = "accessToken",
        description = "HttpOnly cookie-based authentication using JWT access tokens"
)
public class OpenApiConfig {

    @Bean
    public OpenApiCustomizer sortTagsAlphabetically() {
        return openApi -> {
            // Define custom tag order
            Map<String, Integer> tagOrder = Map.ofEntries(
                    Map.entry("Authentication", 1),
                    Map.entry("User Management", 2),
                    Map.entry("PC Management", 3),
                    Map.entry("Component Links", 4),
                    Map.entry("Component Prices", 5),
                    Map.entry("CPU", 6),
                    Map.entry("CPU Cooler", 7),
                    Map.entry("Motherboard", 8),
                    Map.entry("RAM Kit", 9),
                    Map.entry("GPU", 10),
                    Map.entry("Storage", 11),
                    Map.entry("PSU", 12),
                    Map.entry("Case", 13),
                    Map.entry("Case Cooler", 14)
            );

            if (openApi.getTags() != null) {
                List<Tag> sortedTags = openApi.getTags().stream()
                        .sorted(Comparator.comparingInt(tag ->
                                tagOrder.getOrDefault(tag.getName(), 999)))
                        .collect(Collectors.toList());
                openApi.setTags(sortedTags);
            }
        };
    }
}

