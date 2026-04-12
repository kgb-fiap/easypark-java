package br.com.fiap.easypark.services.impl;

import br.com.fiap.easypark.dto.EstacionamentoInDto;
import br.com.fiap.easypark.dto.EstacionamentoOutDto;
import br.com.fiap.easypark.dto.PageResponse;
import br.com.fiap.easypark.entities.Endereco;
import br.com.fiap.easypark.entities.Estacionamento;
import br.com.fiap.easypark.entities.Operadora;
import br.com.fiap.easypark.exceptions.EntityNotFoundException;
import br.com.fiap.easypark.mappers.EstacionamentoMapper;
import br.com.fiap.easypark.repositories.EnderecoRepository;
import br.com.fiap.easypark.repositories.EstacionamentoRepository;
import br.com.fiap.easypark.repositories.OperadoraRepository;
import br.com.fiap.easypark.repositories.VagaRepository;
import br.com.fiap.easypark.services.EstacionamentoService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EstacionamentoServiceImpl implements EstacionamentoService {

    private final EstacionamentoRepository repository;
    private final OperadoraRepository operadoraRepository;
    private final EnderecoRepository enderecoRepository;
    private final VagaRepository vagaRepository;
    private final EstacionamentoMapper mapper;

    public EstacionamentoServiceImpl(EstacionamentoRepository repository,
                                     OperadoraRepository operadoraRepository,
                                     EnderecoRepository enderecoRepository,
                                     VagaRepository vagaRepository,
                                     EstacionamentoMapper mapper) {
        this.repository = repository;
        this.operadoraRepository = operadoraRepository;
        this.enderecoRepository = enderecoRepository;
        this.vagaRepository = vagaRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<EstacionamentoOutDto> findAll(Pageable pageable) {
        var page = repository.findAll(pageable);
        var totalVagasPorEstacionamento = countVagasByEstacionamento(page.getContent());
        var content = page.getContent().stream()
                .map(estacionamento -> mapper.toOut(
                        estacionamento,
                        totalVagasPorEstacionamento.getOrDefault(estacionamento.getId(), 0L)
                ))
                .toList();
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
                .orElseThrow(() -> new EntityNotFoundException("Operadora " + in.operadoraId() + " nao encontrada"));

        Endereco endereco = enderecoRepository.findById(in.enderecoId())
                .orElseThrow(() -> new EntityNotFoundException("Endereco " + in.enderecoId() + " nao encontrado"));

        Estacionamento saved = repository.save(mapper.toEntity(in, op, endereco));
        return mapper.toOut(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EstacionamentoOutDto> findAll() {
        var estacionamentos = repository.findAll();
        var totalVagasPorEstacionamento = countVagasByEstacionamento(estacionamentos);
        return estacionamentos.stream()
                .map(estacionamento -> mapper.toOut(
                        estacionamento,
                        totalVagasPorEstacionamento.getOrDefault(estacionamento.getId(), 0L)
                ))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public EstacionamentoOutDto findById(Long id) {
        Estacionamento e = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Estacionamento " + id + " nao encontrado"));
        return mapper.toOut(e, vagaRepository.countByNivel_Estacionamento_Id(id));
    }

    @Transactional
    @Override
    public EstacionamentoOutDto update(Long id, EstacionamentoInDto in) {
        Estacionamento e = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Estacionamento " + id + " nao encontrado"));

        Operadora op = operadoraRepository.findById(in.operadoraId())
                .orElseThrow(() -> new EntityNotFoundException("Operadora " + in.operadoraId() + " nao encontrada"));

        Endereco endereco = enderecoRepository.findById(in.enderecoId())
                .orElseThrow(() -> new EntityNotFoundException("Endereco " + in.enderecoId() + " nao encontrado"));

        mapper.update(e, in, op, endereco);
        return mapper.toOut(e, vagaRepository.countByNivel_Estacionamento_Id(id));
    }

    @Transactional
    @Override
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Estacionamento " + id + " nao encontrado");
        }
        repository.deleteById(id);
    }

    private Map<Long, Long> countVagasByEstacionamento(List<Estacionamento> estacionamentos) {
        var ids = estacionamentos.stream()
                .map(Estacionamento::getId)
                .toList();

        if (ids.isEmpty()) {
            return Map.of();
        }

        return vagaRepository.countByEstacionamentoIds(ids).stream()
                .collect(Collectors.toMap(
                        VagaRepository.EstacionamentoVagasCount::getEstacionamentoId,
                        VagaRepository.EstacionamentoVagasCount::getTotalVagas
                ));
    }
}
