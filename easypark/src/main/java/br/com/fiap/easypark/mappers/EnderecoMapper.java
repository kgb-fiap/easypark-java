package br.com.fiap.easypark.mappers;

import br.com.fiap.easypark.dto.EnderecoInDto;
import br.com.fiap.easypark.dto.EnderecoOutDto;
import br.com.fiap.easypark.entities.Bairro;
import br.com.fiap.easypark.entities.Cidade;
import br.com.fiap.easypark.entities.Endereco;
import br.com.fiap.easypark.entities.Uf;
import org.springframework.stereotype.Component;

@Component
public class EnderecoMapper {

    public Endereco toEntity(EnderecoInDto in, Bairro bairro) {
        var entity = new Endereco();
        update(entity, in, bairro);
        return entity;
    }

    public void update(Endereco entity, EnderecoInDto in, Bairro bairro) {
        entity.setCep(in.cep());
        entity.setLogradouro(in.logradouro());
        entity.setNumero(in.numero());
        entity.setComplemento(in.complemento());
        entity.setBairro(bairro);
        entity.setLatitude(in.latitude());
        entity.setLongitude(in.longitude());
    }

    public EnderecoOutDto toOut(Endereco entity) {
        Bairro bairro = entity.getBairro();
        Cidade cidade = bairro != null ? bairro.getCidade() : null;
        Uf uf = cidade != null ? cidade.getUf() : null;

        return new EnderecoOutDto(
                entity.getId(),
                entity.getCep(),
                entity.getLogradouro(),
                entity.getNumero(),
                entity.getComplemento(),
                bairro != null ? bairro.getId() : null,
                bairro != null ? bairro.getNome() : null,
                cidade != null ? cidade.getId() : null,
                cidade != null ? cidade.getNome() : null,
                uf != null ? uf.getSigla() : null,
                uf != null ? uf.getNome() : null,
                entity.getLatitude(),
                entity.getLongitude()
        );
    }
}
