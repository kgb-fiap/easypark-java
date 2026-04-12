package br.com.fiap.easypark.configs;

import java.util.Map;

public record FirebasePrincipal(
        String uid,
        String email,
        String name,
        Map<String, Object> claims
) {}
