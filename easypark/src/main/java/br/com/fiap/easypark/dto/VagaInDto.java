
package br.com.fiap.easypark.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record VagaInDto(
        @NotNull Long nivelId,
        @NotNull Long tipoVagaId,
        @NotBlank String codigo,
        @NotNull Boolean ativa
) {}
