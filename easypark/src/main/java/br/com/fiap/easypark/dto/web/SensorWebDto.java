package br.com.fiap.easypark.dto.web;

public record SensorWebDto(
        Long id,
        Long vagaId,
        String vagaCodigo,
        String modelo,
        String identificadorExterno,
        String ativo
) {}
