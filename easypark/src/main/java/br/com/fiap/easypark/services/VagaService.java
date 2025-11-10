
package br.com.fiap.easypark.services;

import br.com.fiap.easypark.dto.VagaInDto;
import br.com.fiap.easypark.dto.VagaOutDto;
import br.com.fiap.easypark.entities.VagaStatus;
import br.com.fiap.easypark.entities.enums.StatusVaga;
import br.com.fiap.easypark.dto.*;
import br.com.fiap.easypark.entities.enums.StatusVaga;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface VagaService {
    VagaOutDto create(VagaInDto in);
    List<VagaOutDto> findAll();
    VagaOutDto findById(Long id);
    VagaOutDto update(Long id, VagaInDto in);
    void delete(Long id);

    List<VagaOutDto> findByStatus(StatusVaga status);
    List<VagaOutDto> findByEstacionamento(Long estacionamentoId);

    // status atual (cache populado por trigger/processo)
    VagaStatus getStatus(Long vagaId);

    PageResponse<VagaOutDto> search(StatusVaga status, Long nivelId, Long tipoVagaId, Long estacionamentoId, Pageable pageable);
}
