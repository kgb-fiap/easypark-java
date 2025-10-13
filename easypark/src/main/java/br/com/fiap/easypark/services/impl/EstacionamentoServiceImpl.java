
package br.com.fiap.easypark.services.impl;

import br.com.fiap.easypark.dto.EstacionamentoInDto;
import br.com.fiap.easypark.dto.EstacionamentoOutDto;
import br.com.fiap.easypark.entities.Estacionamento;
import br.com.fiap.easypark.entities.Operadora;
import br.com.fiap.easypark.exceptions.EntityNotFoundException;
import br.com.fiap.easypark.mappers.EstacionamentoMapper;
import br.com.fiap.easypark.repositories.EstacionamentoRepository;
import br.com.fiap.easypark.repositories.OperadoraRepository;
import br.com.fiap.easypark.services.EstacionamentoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class EstacionamentoServiceImpl implements EstacionamentoService {

    private final EstacionamentoRepository repository;
    private final OperadoraRepository operadoraRepository;
    private final EstacionamentoMapper mapper;

    @Transactional
    @Override
    public EstacionamentoOutDto create(EstacionamentoInDto in) {
        Operadora op = operadoraRepository.findById(in.operadoraId())
                .orElseThrow(() -> new EntityNotFoundException("Operadora " + in.operadoraId() + " não encontrada"));

        Estacionamento saved = repository.save(mapper.toEntity(in, op));
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

        mapper.update(e, in, op);
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
