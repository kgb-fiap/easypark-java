package br.com.fiap.easypark.controller;

import br.com.fiap.easypark.configs.FirebasePrincipal;
import br.com.fiap.easypark.configs.FirebaseProperties;
import br.com.fiap.easypark.dto.FirebaseAuthStatusOutDto;
import br.com.fiap.easypark.dto.FirebaseUserOutDto;
import com.google.firebase.auth.FirebaseAuth;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/firebase")
public class FirebaseAuthController {
    private final FirebaseProperties firebaseProperties;
    private final ObjectProvider<FirebaseAuth> firebaseAuth;

    public FirebaseAuthController(FirebaseProperties firebaseProperties, ObjectProvider<FirebaseAuth> firebaseAuth) {
        this.firebaseProperties = firebaseProperties;
        this.firebaseAuth = firebaseAuth;
    }

    @GetMapping("/status")
    @Operation(summary = "Consulta o status da integracao Firebase")
    public FirebaseAuthStatusOutDto status() {
        return new FirebaseAuthStatusOutDto(
                firebaseProperties.enabled(),
                firebaseAuth.getIfAvailable() != null,
                firebaseProperties.hasProjectId()
        );
    }

    @GetMapping("/me")
    @Operation(summary = "Consulta o usuario autenticado pelo Firebase")
    @SecurityRequirement(name = "firebaseAuth")
    public FirebaseUserOutDto me(Authentication authentication) {
        var principal = (FirebasePrincipal) authentication.getPrincipal();
        var authorities = authentication.getAuthorities().stream()
                .map(Object::toString)
                .sorted()
                .toList();
        return new FirebaseUserOutDto(
                principal.uid(),
                principal.email(),
                principal.name(),
                authorities,
                principal.claims()
        );
    }
}
