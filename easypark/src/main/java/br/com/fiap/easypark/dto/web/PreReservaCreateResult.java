package br.com.fiap.easypark.dto.web;

import java.math.BigDecimal;

public record PreReservaCreateResult(
        Long reservaId,
        BigDecimal valorPrevisto
) {}
