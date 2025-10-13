package br.com.fiap.easypark.dto;

import java.time.OffsetDateTime;

public record VagaStatusOutDto(
        Long vagaId,
        String status,
        OffsetDateTime ultimoOcorrido,
        Long sensorId
) {}