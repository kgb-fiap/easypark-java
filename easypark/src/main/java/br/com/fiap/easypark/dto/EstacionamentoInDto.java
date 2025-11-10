package br.com.fiap.easypark.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EstacionamentoInDto(
        @NotNull Long operadoraId,
        @NotBlank String nome,
        @NotNull Long enderecoId,
        Integer esperaMinutos,
        Integer toleranciaMinutos
) {}