package br.com.fiap.easypark.services.impl;

import br.com.fiap.easypark.dto.VagaInDto;
import br.com.fiap.easypark.dto.VagaOutDto;
import br.com.fiap.easypark.entities.Nivel;
import br.com.fiap.easypark.entities.TipoVaga;
import br.com.fiap.easypark.entities.Vaga;
import br.com.fiap.easypark.entities.VagaStatus;
import br.com.fiap.easypark.exceptions.EntityNotFoundException;
import br.com.fiap.easypark.mappers.VagaMapper;
import br.com.fiap.easypark.repositories.NivelRepository;
import br.com.fiap.easypark.repositories.TipoVagaRepository;
import br.com.fiap.easypark.repositories.VagaRepository;
import br.com.fiap.easypark.repositories.VagaStatusRepository;
import br.com.fiap.easypark.services.VagaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import br.com.fiap.easypark.entities.enums.StatusVaga;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class VagaServiceImpl implements VagaService {

    private final VagaRepository vagaRepo;
    private final VagaStatusRepository statusRepo;
    private final NivelRepository nivelRepo;
    private final TipoVagaRepository tipoRepo;
    private final VagaMapper mapper;

    @Transactional
    @Override
    public VagaOutDto create(VagaInDto in) {
        if (vagaRepo.existsByNivelIdAndCodigoIgnoreCase(in.nivelId(), in.codigo())) {
            throw new IllegalArgumentException("Já existe uma vaga com código '" + in.codigo()
                    + "' no nível " + in.nivelId());
        }
        Nivel nivel = nivelRepo.findById(in.nivelId())
                .orElseThrow(() -> new EntityNotFoundException("Nível " + in.nivelId() + " não encontrado"));
        TipoVaga tipo = tipoRepo.findById(in.tipoVagaId())
                .orElseThrow(() -> new EntityNotFoundException("TipoVaga " + in.tipoVagaId() + " não encontrado"));

        Vaga saved = vagaRepo.save(mapper.toEntity(in, nivel, tipo));
        return mapper.toOut(saved);
    }

    @Transactional(readOnly = true)
    @Override
    public List<VagaOutDto> findAll() {
        return vagaRepo.findAll().stream().map(mapper::toOut).toList();
    }

    @Transactional(readOnly = true)
    @Override
    public VagaOutDto findById(Long id) {
        Vaga v = vagaRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Vaga " + id + " não encontrada"));
        return mapper.toOut(v);
    }

    @Transactional
    @Override
    public VagaOutDto update(Long id, VagaInDto in) {
        Vaga vaga = vagaRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Vaga " + id + " não encontrada"));

        boolean mudouCodigo = !vaga.getCodigo().equalsIgnoreCase(in.codigo());
        boolean mudouNivel  = !vaga.getNivel().getId().equals(in.nivelId());
        if (mudouCodigo || mudouNivel) {
            if (vagaRepo.existsByNivelIdAndCodigoIgnoreCase(in.nivelId(), in.codigo())) {
                throw new IllegalArgumentException("Já existe uma vaga com código '" + in.codigo()
                        + "' no nível " + in.nivelId());
            }
        }

        Nivel nivel = nivelRepo.findById(in.nivelId())
                .orElseThrow(() -> new EntityNotFoundException("Nível " + in.nivelId() + " não encontrado"));
        TipoVaga tipo = tipoRepo.findById(in.tipoVagaId())
                .orElseThrow(() -> new EntityNotFoundException("TipoVaga " + in.tipoVagaId() + " não encontrado"));

        mapper.update(vaga, in, nivel, tipo);
        return mapper.toOut(vaga);
    }

    @Transactional
    @Override
    public void delete(Long id) {
        if (!vagaRepo.existsById(id)) {
            throw new EntityNotFoundException("Vaga " + id + " não encontrada");
        }
        vagaRepo.deleteById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public VagaStatus getStatus(Long vagaId) {
        return statusRepo.findById(vagaId).orElse(null);
    }

    @Transactional(readOnly = true)
    @Override
    public List<VagaOutDto> findByStatus(StatusVaga status) {
        // Status em VAGA_STATUS é String (ex.: LIVRE/OCUPADA/DESCONHECIDO)
        var list = vagaRepo.findByStatusIgnoreCase(status.name());
        return list.stream().map(mapper::toOut).toList();
    }

    @Transactional(readOnly = true)
    @Override
    public List<VagaOutDto> findByEstacionamento(Long estacionamentoId) {
        return vagaRepo.findByNivel_Estacionamento_Id(estacionamentoId)
                .stream().map(mapper::toOut).toList();
    }
}
