package br.com.fiap.easypark.services;

import br.com.fiap.easypark.dto.OperadoraInDto;
import br.com.fiap.easypark.dto.OperadoraOutDto;
import br.com.fiap.easypark.dto.PageResponse;
import org.springframework.data.domain.Pageable;

public interface OperadoraService {
    PageResponse<OperadoraOutDto> findAll(Pageable pageable);
    OperadoraOutDto findById(Long id);
    OperadoraOutDto create(OperadoraInDto in);
    OperadoraOutDto update(Long id, OperadoraInDto in);
    void delete(Long id);
}
