package br.com.fiap.easypark.dto.web;

import java.math.BigDecimal;

public record ReservaWebDto(
        Long id,
        Long usuarioId,
        String usuarioEmail,
        Long vagaId,
        String vagaCodigo,
        Long estacionamentoId,
        String estacionamentoNome,
        String nivelNome,
        String tipoVagaNome,
        BigDecimal tarifaPorMinuto,
        BigDecimal valorPrevisto,
        String estado,
        String criadoEm,
        String inicioPrevisto,
        Integer duracaoMinutos,
        Integer antecedenciaMinutos,
        Integer etaMinutos,
        String confirmadoEm,
        String ocupadoEm,
        String vagaBloqueada,
        String motivoCancelamento
) {}
