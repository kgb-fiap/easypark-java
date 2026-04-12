package br.com.fiap.easypark.dto.web;

import java.math.BigDecimal;

public record EstacionamentoWebDto(
        Long id,
        String nome,
        String operadoraNome,
        String operadoraRazaoSocial,
        String operadoraCnpj,
        String operadoraTelefone,
        String cep,
        String logradouro,
        String numero,
        String complemento,
        String bairro,
        String cidade,
        String uf,
        BigDecimal latitude,
        BigDecimal longitude,
        Integer esperaMinutos,
        Integer toleranciaMinutos,
        Long totalVagas,
        Long vagasLivres,
        BigDecimal distanciaKm
) {
    public String enderecoCompleto() {
        var parts = new StringBuilder();
        append(parts, logradouro);
        append(parts, numero);
        append(parts, bairro);
        append(parts, cidade);
        append(parts, uf);
        append(parts, cep);
        return parts.isEmpty() ? "Endereco nao informado" : parts.toString();
    }

    public String distanciaTexto() {
        return distanciaKm == null ? "Distancia indisponivel" : distanciaKm + " km";
    }

    private static void append(StringBuilder parts, String value) {
        if (value == null || value.isBlank()) {
            return;
        }
        if (!parts.isEmpty()) {
            parts.append(", ");
        }
        parts.append(value);
    }
}
