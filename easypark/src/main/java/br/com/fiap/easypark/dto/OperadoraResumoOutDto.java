package br.com.fiap.easypark.dto;

public record OperadoraResumoOutDto(
        Long id,
        String cnpj,
        String razaoSocial,
        String nomeFantasia,
        String telefone
) {}
