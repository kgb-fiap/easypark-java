package br.com.fiap.easypark.services;

import br.com.fiap.easypark.dto.EstacionamentoInDto;
import br.com.fiap.easypark.dto.EstacionamentoOutDto;
import br.com.fiap.easypark.dto.*;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EstacionamentoService {
    EstacionamentoOutDto create(EstacionamentoInDto in);
    List<EstacionamentoOutDto> findAll();
    EstacionamentoOutDto findById(Long id);
    EstacionamentoOutDto update(Long id, EstacionamentoInDto in);
    void delete(Long id);
    PageResponse<EstacionamentoOutDto> findAll(Pageable pageable);
}
