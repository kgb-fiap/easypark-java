package br.com.fiap.easypark.configs;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Configuration
@EnableConfigurationProperties(FirebaseProperties.class)
public class FirebaseAdminConfig {

    @Bean
    @ConditionalOnProperty(name = "firebase.enabled", havingValue = "true")
    public FirebaseApp firebaseApp(FirebaseProperties properties) throws IOException {
        if (!FirebaseApp.getApps().isEmpty()) {
            return FirebaseApp.getInstance();
        }

        FirebaseOptions.Builder options = FirebaseOptions.builder();
        if (properties.hasCredentialsJson()) {
            try (var input = new ByteArrayInputStream(properties.credentialsJson().getBytes(StandardCharsets.UTF_8))) {
                options.setCredentials(GoogleCredentials.fromStream(input));
            }
        } else if (properties.hasCredentialsPath()) {
            try (var input = new FileInputStream(properties.credentialsPath())) {
                options.setCredentials(GoogleCredentials.fromStream(input));
            }
        } else {
            options.setCredentials(GoogleCredentials.getApplicationDefault());
        }

        if (properties.hasProjectId()) {
            options.setProjectId(properties.projectId());
        }

        return FirebaseApp.initializeApp(options.build());
    }

    @Bean
    @ConditionalOnBean(FirebaseApp.class)
    public FirebaseAuth firebaseAuth(FirebaseApp firebaseApp) {
        return FirebaseAuth.getInstance(firebaseApp);
    }
}
