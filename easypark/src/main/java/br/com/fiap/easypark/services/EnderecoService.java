package br.com.fiap.easypark.services;

import br.com.fiap.easypark.dto.EnderecoInDto;
import br.com.fiap.easypark.dto.EnderecoOutDto;
import br.com.fiap.easypark.dto.PageResponse;
import org.springframework.data.domain.Pageable;

public interface EnderecoService {
    PageResponse<EnderecoOutDto> findAll(Pageable pageable);
    EnderecoOutDto findById(Long id);
    EnderecoOutDto create(EnderecoInDto in);
    EnderecoOutDto update(Long id, EnderecoInDto in);
    void delete(Long id);
}
