
package br.com.fiap.easypark.dto;

public record VagaOutDto(
        Long id,
        String codigo,
        Boolean ativa,
        Long nivelId,
        Long tipoVagaId
) {}
