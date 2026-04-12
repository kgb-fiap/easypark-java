package br.com.fiap.easypark.dto.web;

import java.math.BigDecimal;
import java.util.List;

public record EstacionamentoBuscaResultado(
        String destino,
        BigDecimal latitudeReferencia,
        BigDecimal longitudeReferencia,
        boolean referenciaPorEnderecoCadastrado,
        List<EstacionamentoWebDto> estacionamentos
) {}
