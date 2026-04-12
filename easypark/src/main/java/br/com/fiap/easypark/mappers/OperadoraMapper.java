package br.com.fiap.easypark.mappers;

import br.com.fiap.easypark.dto.OperadoraInDto;
import br.com.fiap.easypark.dto.OperadoraOutDto;
import br.com.fiap.easypark.entities.Operadora;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Component
public class OperadoraMapper {

    public Operadora toEntity(OperadoraInDto in) {
        return Operadora.builder()
                .cnpj(in.cnpj())
                .razaoSocial(in.razaoSocial())
                .nomeFantasia(in.nomeFantasia())
                .telefone(in.telefone())
                .criadoEm(OffsetDateTime.now())
                .build();
    }

    public void update(Operadora entity, OperadoraInDto in) {
        entity.setCnpj(in.cnpj());
        entity.setRazaoSocial(in.razaoSocial());
        entity.setNomeFantasia(in.nomeFantasia());
        entity.setTelefone(in.telefone());
    }

    public OperadoraOutDto toOut(Operadora entity) {
        return new OperadoraOutDto(
                entity.getId(),
                entity.getCnpj(),
                entity.getRazaoSocial(),
                entity.getNomeFantasia(),
                entity.getTelefone(),
                entity.getCriadoEm()
        );
    }
}
