package br.com.fiap.easypark.services.impl;

import br.com.fiap.easypark.dto.EnderecoInDto;
import br.com.fiap.easypark.dto.EnderecoOutDto;
import br.com.fiap.easypark.dto.PageResponse;
import br.com.fiap.easypark.entities.Bairro;
import br.com.fiap.easypark.entities.Endereco;
import br.com.fiap.easypark.exceptions.EntityNotFoundException;
import br.com.fiap.easypark.mappers.EnderecoMapper;
import br.com.fiap.easypark.repositories.BairroRepository;
import br.com.fiap.easypark.repositories.EnderecoRepository;
import br.com.fiap.easypark.services.EnderecoService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EnderecoServiceImpl implements EnderecoService {

    private final EnderecoRepository repository;
    private final BairroRepository bairroRepository;
    private final EnderecoMapper mapper;

    public EnderecoServiceImpl(EnderecoRepository repository,
                               BairroRepository bairroRepository,
                               EnderecoMapper mapper) {
        this.repository = repository;
        this.bairroRepository = bairroRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<EnderecoOutDto> findAll(Pageable pageable) {
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
    public EnderecoOutDto findById(Long id) {
        return mapper.toOut(findEntity(id));
    }

    @Override
    @Transactional
    public EnderecoOutDto create(EnderecoInDto in) {
        Endereco saved = repository.save(mapper.toEntity(in, findBairro(in.bairroId())));
        return mapper.toOut(saved);
    }

    @Override
    @Transactional
    public EnderecoOutDto update(Long id, EnderecoInDto in) {
        Endereco entity = findEntity(id);
        mapper.update(entity, in, findBairro(in.bairroId()));
        return mapper.toOut(entity);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Endereco " + id + " nao encontrado");
        }
        repository.deleteById(id);
    }

    private Endereco findEntity(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Endereco " + id + " nao encontrado"));
    }

    private Bairro findBairro(Long bairroId) {
        if (bairroId == null) {
            return null;
        }

        return bairroRepository.findById(bairroId)
                .orElseThrow(() -> new EntityNotFoundException("Bairro " + bairroId + " nao encontrado"));
    }
}
