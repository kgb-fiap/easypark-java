
package br.com.fiap.easypark.mappers;

import br.com.fiap.easypark.dto.EstacionamentoInDto;
import br.com.fiap.easypark.dto.EstacionamentoOutDto;
import br.com.fiap.easypark.entities.Estacionamento;
import br.com.fiap.easypark.entities.Operadora;
import org.springframework.stereotype.Component;

@Component
public class EstacionamentoMapper {

    public Estacionamento toEntity(EstacionamentoInDto in, Operadora operadora) {
        return Estacionamento.builder()
                .operadora(operadora)
                .nome(in.nome())
                .endereco(in.endereco())
                .latitude(in.latitude())
                .longitude(in.longitude())
                .esperaMinutos(in.esperaMinutos())
                .toleranciaMinutos(in.toleranciaMinutos())
                .build();
    }

    public void update(Estacionamento entity, EstacionamentoInDto in, Operadora operadora) {
        entity.setOperadora(operadora);
        entity.setNome(in.nome());
        entity.setEndereco(in.endereco());
        entity.setLatitude(in.latitude());
        entity.setLongitude(in.longitude());
        entity.setEsperaMinutos(in.esperaMinutos());
        entity.setToleranciaMinutos(in.toleranciaMinutos());
    }

    public EstacionamentoOutDto toOut(Estacionamento e) {
        // ajuste conforme seu OutDto atual (se quiser expor mais campos, inclua lá também)
        return new EstacionamentoOutDto(e.getId(), e.getNome(), e.getEndereco());
    }
}
