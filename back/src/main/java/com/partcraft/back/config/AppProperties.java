package com.partcraft.back.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "app")
@Getter
@Setter
public class AppProperties {

    private Cookie cookie = new Cookie();
    private Cors cors = new Cors();

    @Getter
    @Setter
    public static class Cookie {
        private boolean secure = false;
        private String sameSite = "Lax";
        private int accessTokenMaxAge = 1800;
        private int refreshTokenMaxAge = 86400;
    }

    @Getter
    @Setter
    public static class Cors {
        private List<String> allowedOrigins = List.of("http://localhost:3000");
    }
}
