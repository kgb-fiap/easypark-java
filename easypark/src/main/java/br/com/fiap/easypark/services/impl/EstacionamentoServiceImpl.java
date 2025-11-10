package br.com.fiap.easypark.services.impl;

import br.com.fiap.easypark.dto.EstacionamentoInDto;
import br.com.fiap.easypark.dto.EstacionamentoOutDto;
import br.com.fiap.easypark.dto.PageResponse;
import br.com.fiap.easypark.entities.Endereco;
import br.com.fiap.easypark.entities.Estacionamento;
import br.com.fiap.easypark.entities.Operadora;
import br.com.fiap.easypark.exceptions.EntityNotFoundException;
import br.com.fiap.easypark.mappers.EstacionamentoMapper;
import br.com.fiap.easypark.repositories.EstacionamentoRepository;
import br.com.fiap.easypark.repositories.EnderecoRepository;
import br.com.fiap.easypark.repositories.OperadoraRepository;
import br.com.fiap.easypark.services.EstacionamentoService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EstacionamentoServiceImpl implements EstacionamentoService {

    private final EstacionamentoRepository repository;
    private final OperadoraRepository operadoraRepository;
    private final EnderecoRepository enderecoRepository;
    private final EstacionamentoMapper mapper;

    public EstacionamentoServiceImpl(EstacionamentoRepository repository,
                                     OperadoraRepository operadoraRepository,
                                     EnderecoRepository enderecoRepository,
                                     EstacionamentoMapper mapper) {
        this.repository = repository;
        this.operadoraRepository = operadoraRepository;
        this.enderecoRepository = enderecoRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<EstacionamentoOutDto> findAll(Pageable pageable) {
        var page = repository.findAll(pageable);
        var content = page.getContent().stream().map(mapper::toOut).toList();
        return new PageResponse<>(
                content, page.getNumber(), page.getSize(),
                page.getTotalElements(), page.getTotalPages(),
                page.isFirst(), page.isLast()
        );
    }

    @Transactional
    @Override
    public EstacionamentoOutDto create(EstacionamentoInDto in) {
        Operadora op = operadoraRepository.findById(in.operadoraId())
                .orElseThrow(() -> new EntityNotFoundException("Operadora " + in.operadoraId() + " não encontrada"));

        Endereco endereco = enderecoRepository.findById(in.enderecoId())
                .orElseThrow(() -> new EntityNotFoundException("Endereco " + in.enderecoId() + " não encontrado"));

        Estacionamento saved = repository.save(mapper.toEntity(in, op, endereco));
        return mapper.toOut(saved);
    }

    @Override
    public List<EstacionamentoOutDto> findAll() {
        return repository.findAll().stream().map(mapper::toOut).toList();
    }

    @Override
    public EstacionamentoOutDto findById(Long id) {
        Estacionamento e = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Estacionamento " + id + " não encontrado"));
        return mapper.toOut(e);
    }

    @Transactional
    @Override
    public EstacionamentoOutDto update(Long id, EstacionamentoInDto in) {
        Estacionamento e = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Estacionamento " + id + " não encontrado"));

        Operadora op = operadoraRepository.findById(in.operadoraId())
                .orElseThrow(() -> new EntityNotFoundException("Operadora " + in.operadoraId() + " não encontrada"));

        Endereco endereco = enderecoRepository.findById(in.enderecoId())
                .orElseThrow(() -> new EntityNotFoundException("Endereco " + in.enderecoId() + " não encontrado"));

        mapper.update(e, in, op, endereco);
        return mapper.toOut(e);
    }

    @Transactional
    @Override
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Estacionamento " + id + " não encontrado");
        }
        repository.deleteById(id);
    }
}