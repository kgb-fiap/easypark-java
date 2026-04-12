package br.com.fiap.easypark.dto.web;

public record SensorEventoWebDto(
        Long id,
        Long sensorId,
        Long vagaId,
        String status,
        String ocorridoEm,
        String recebidoEm,
        String payload
) {}
