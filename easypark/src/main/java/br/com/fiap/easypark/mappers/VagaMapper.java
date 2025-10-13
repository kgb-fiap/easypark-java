
package br.com.fiap.easypark.mappers;

import br.com.fiap.easypark.dto.VagaInDto;
import br.com.fiap.easypark.dto.VagaOutDto;
import br.com.fiap.easypark.entities.Nivel;
import br.com.fiap.easypark.entities.TipoVaga;
import br.com.fiap.easypark.entities.Vaga;
import org.springframework.stereotype.Component;

@Component
public class VagaMapper {

    public Vaga toEntity(VagaInDto in, Nivel nivel, TipoVaga tipo) {
        return Vaga.builder()
                .nivel(nivel)
                .tipoVaga(tipo)
                .codigo(in.codigo())
                .ativa(in.ativa())
                .build();
    }

    public void update(Vaga entity, VagaInDto in, Nivel nivel, TipoVaga tipo) {
        entity.setNivel(nivel);
        entity.setTipoVaga(tipo);
        entity.setCodigo(in.codigo());
        entity.setAtiva(in.ativa());
    }


    public VagaOutDto toOut(Vaga v) {
        return new VagaOutDto(
                v.getId(),
                v.getCodigo(),
                v.getAtiva(),
                v.getNivel() != null ? v.getNivel().getId() : null,
                v.getTipoVaga() != null ? v.getTipoVaga().getId() : null
        );
    }
}
