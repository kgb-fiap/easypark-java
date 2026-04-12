package br.com.fiap.easypark.dto;

import java.util.List;
import java.util.Map;

public record FirebaseUserOutDto(
        String uid,
        String email,
        String name,
        List<String> authorities,
        Map<String, Object> claims
) {}
