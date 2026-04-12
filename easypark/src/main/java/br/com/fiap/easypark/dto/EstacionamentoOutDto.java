package br.com.fiap.easypark.dto;

public record EstacionamentoOutDto(
        Long id,
        String nome,
        Long enderecoId,
        EnderecoResumoOutDto endereco,
        Long totalVagas,
        Integer esperaMinutos,
        Integer toleranciaMinutos
) {}
