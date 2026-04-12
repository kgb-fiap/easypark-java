package br.com.fiap.easypark.dto;

import java.math.BigDecimal;

public record EnderecoOutDto(
        Long id,
        String cep,
        String logradouro,
        String numero,
        String complemento,
        Long bairroId,
        String bairroNome,
        Long cidadeId,
        String cidadeNome,
        String ufSigla,
        String ufNome,
        BigDecimal latitude,
        BigDecimal longitude
) {}
