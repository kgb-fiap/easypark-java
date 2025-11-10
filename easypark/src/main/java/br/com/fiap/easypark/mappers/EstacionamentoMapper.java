package br.com.fiap.easypark.mappers;

import br.com.fiap.easypark.dto.EstacionamentoInDto;
import br.com.fiap.easypark.dto.EstacionamentoOutDto;
import br.com.fiap.easypark.dto.EnderecoResumoOutDto;
import br.com.fiap.easypark.entities.Endereco;
import br.com.fiap.easypark.entities.Estacionamento;
import br.com.fiap.easypark.entities.Operadora;
import org.springframework.stereotype.Component;

@Component
public class EstacionamentoMapper {

    public Estacionamento toEntity(EstacionamentoInDto in, Operadora operadora, Endereco endereco) {
        return Estacionamento.builder()
                .operadora(operadora)
                .nome(in.nome())
                .endereco(endereco)
                .esperaMinutos(in.esperaMinutos())
                .toleranciaMinutos(in.toleranciaMinutos())
                .build();
    }

    public void update(Estacionamento entity, EstacionamentoInDto in, Operadora operadora, Endereco endereco) {
        entity.setOperadora(operadora);
        entity.setNome(in.nome());
        entity.setEndereco(endereco);
        entity.setEsperaMinutos(in.esperaMinutos());
        entity.setToleranciaMinutos(in.toleranciaMinutos());
    }

    public EstacionamentoOutDto toOut(Estacionamento e) {
        Endereco endereco = e.getEndereco();
        EnderecoResumoOutDto enderecoResumo = null;
        if (endereco != null) {
            enderecoResumo = new EnderecoResumoOutDto(
                    endereco.getId(),
                    endereco.getCep(),
                    endereco.getLogradouro(),
                    endereco.getNumero()
            );
        }
        return new EstacionamentoOutDto(
                e.getId(),
                e.getNome(),
                endereco != null ? endereco.getId() : null,
                enderecoResumo,
                e.getEsperaMinutos(),
                e.getToleranciaMinutos()
        );
    }
}