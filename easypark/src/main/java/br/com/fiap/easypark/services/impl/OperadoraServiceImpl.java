package br.com.fiap.easypark.services.impl;

import br.com.fiap.easypark.dto.OperadoraInDto;
import br.com.fiap.easypark.dto.OperadoraOutDto;
import br.com.fiap.easypark.dto.PageResponse;
import br.com.fiap.easypark.entities.Operadora;
import br.com.fiap.easypark.exceptions.EntityNotFoundException;
import br.com.fiap.easypark.mappers.OperadoraMapper;
import br.com.fiap.easypark.repositories.OperadoraRepository;
import br.com.fiap.easypark.services.OperadoraService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OperadoraServiceImpl implements OperadoraService {

    private final OperadoraRepository repository;
    private final OperadoraMapper mapper;

    public OperadoraServiceImpl(OperadoraRepository repository, OperadoraMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<OperadoraOutDto> findAll(Pageable pageable) {
        var page = repository.findAll(pageable);
        var content = page.getContent().stream().map(mapper::toOut).toList();
        return new PageResponse<>(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public OperadoraOutDto findById(Long id) {
        return mapper.toOut(findEntity(id));
    }

    @Override
    @Transactional
    public OperadoraOutDto create(OperadoraInDto in) {
        if (repository.existsByCnpj(in.cnpj())) {
            throw new IllegalArgumentException("Ja existe uma operadora com CNPJ " + in.cnpj());
        }

        Operadora saved = repository.save(mapper.toEntity(in));
        return mapper.toOut(saved);
    }

    @Override
    @Transactional
    public OperadoraOutDto update(Long id, OperadoraInDto in) {
        Operadora entity = findEntity(id);
        if (repository.existsByCnpjAndIdNot(in.cnpj(), id)) {
            throw new IllegalArgumentException("Ja existe uma operadora com CNPJ " + in.cnpj());
        }

        mapper.update(entity, in);
        return mapper.toOut(entity);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Operadora " + id + " nao encontrada");
        }
        repository.deleteById(id);
    }

    private Operadora findEntity(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Operadora " + id + " nao encontrada"));
    }
}
