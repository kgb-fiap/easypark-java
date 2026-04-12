package br.com.fiap.easypark.configs;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "firebase")
public record FirebaseProperties(
        boolean enabled,
        String projectId,
        String credentialsPath,
        String defaultRole,
        String allowedOrigins
) {
    public FirebaseProperties {
        defaultRole = blank(defaultRole) ? "MOTORISTA" : defaultRole;
        allowedOrigins = blank(allowedOrigins) ? "http://localhost:3000,http://localhost:5173" : allowedOrigins;
    }

    public boolean hasProjectId() {
        return !blank(projectId);
    }

    public boolean hasCredentialsPath() {
        return !blank(credentialsPath);
    }

    private static boolean blank(String value) {
        return value == null || value.isBlank();
    }
}
