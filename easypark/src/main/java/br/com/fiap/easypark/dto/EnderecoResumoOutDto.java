package br.com.fiap.easypark.dto;

import java.math.BigDecimal;

public record EnderecoResumoOutDto(
        Long id,
        String cep,
        String logradouro,
        String numero,
        BigDecimal latitude,
        BigDecimal longitude
) {}
