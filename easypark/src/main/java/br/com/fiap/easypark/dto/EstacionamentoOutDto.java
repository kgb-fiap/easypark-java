package br.com.fiap.easypark.dto;

public record EstacionamentoOutDto(
        Long id,
        String nome,
        Long operadoraId,
        OperadoraResumoOutDto operadora,
        Long enderecoId,
        EnderecoResumoOutDto endereco,
        Long totalVagas,
        Integer esperaMinutos,
        Integer toleranciaMinutos
) {}
