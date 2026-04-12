package br.com.fiap.easypark.dto;

import java.time.OffsetDateTime;

public record OperadoraOutDto(
        Long id,
        String cnpj,
        String razaoSocial,
        String nomeFantasia,
        String telefone,
        OffsetDateTime criadoEm
) {}
