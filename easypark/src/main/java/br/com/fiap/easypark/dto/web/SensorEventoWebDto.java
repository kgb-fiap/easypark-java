package br.com.fiap.easypark.dto.web;

public record SensorEventoWebDto(
        String sensor,
        String vagaCodigo,
        String status,
        String ocorridoEm,
        String recebidoEm,
        String payload
) {}
