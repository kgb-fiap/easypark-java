package br.com.fiap.easypark.dto.web;

import java.math.BigDecimal;

public record VagaWebDto(
        Long id,
        String codigo,
        boolean ativa,
        String nivelNome,
        Integer nivelOrdem,
        String tipoNome,
        BigDecimal tarifaPorMinuto,
        boolean eletrica,
        boolean acessivel,
        boolean moto,
        String status,
        String ultimoOcorrido,
        Long sensorId,
        BigDecimal valorPrevistoPadrao
) {
    public boolean podeReservar() {
        return ativa && "LIVRE".equalsIgnoreCase(status);
    }

    public String caracteristicas() {
        var text = new StringBuilder();
        append(text, eletrica, "Eletrica");
        append(text, acessivel, "Acessivel");
        append(text, moto, "Moto");
        return text.isEmpty() ? "Convencional" : text.toString();
    }

    private static void append(StringBuilder text, boolean enabled, String label) {
        if (!enabled) {
            return;
        }
        if (!text.isEmpty()) {
            text.append(", ");
        }
        text.append(label);
    }
}
