package br.com.fiap.easypark.dto;

public record EstacionamentoOutDto(
        Long id,
        String nome,
        Long enderecoId,
        EnderecoResumoOutDto endereco,
        Integer esperaMinutos,
        Integer toleranciaMinutos
) {}