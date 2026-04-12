package br.com.fiap.easypark.dto;

public record FirebaseAuthStatusOutDto(
        boolean enabled,
        boolean initialized,
        boolean projectIdConfigured
) {}
