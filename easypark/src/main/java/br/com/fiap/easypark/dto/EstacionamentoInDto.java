
package br.com.fiap.easypark.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record EstacionamentoInDto(
        @NotNull Long operadoraId,
        @NotBlank String nome,
        String endereco,
        BigDecimal latitude,
        BigDecimal longitude,
        Integer esperaMinutos,
        Integer toleranciaMinutos
) {}
