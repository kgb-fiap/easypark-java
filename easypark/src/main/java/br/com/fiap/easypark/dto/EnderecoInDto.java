package br.com.fiap.easypark.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record EnderecoInDto(
        @Size(max = 10)
        String cep,

        @Size(max = 150)
        String logradouro,

        @Size(max = 20)
        String numero,

        @Size(max = 50)
        String complemento,

        Long bairroId,

        @DecimalMin("-90.0")
        @DecimalMax("90.0")
        BigDecimal latitude,

        @DecimalMin("-180.0")
        @DecimalMax("180.0")
        BigDecimal longitude
) {}
