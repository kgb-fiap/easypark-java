package br.com.fiap.easypark.dto.web;

public record ReservaWebDto(
        Long id,
        Long usuarioId,
        String usuarioEmail,
        Long vagaId,
        String vagaCodigo,
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
