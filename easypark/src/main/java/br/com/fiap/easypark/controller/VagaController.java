package br.com.fiap.easypark.controller;

import br.com.fiap.easypark.dto.VagaInDto;
import br.com.fiap.easypark.dto.VagaOutDto;
import br.com.fiap.easypark.dto.VagaStatusOutDto;
import br.com.fiap.easypark.entities.enums.StatusVaga;
import br.com.fiap.easypark.services.VagaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping
public class VagaController {

    private final VagaService service;

    @PostMapping("/vagas")
    @ResponseStatus(HttpStatus.CREATED)
    public VagaOutDto create(@RequestBody @Valid VagaInDto in) {
        return service.create(in);
    }

    @GetMapping("/vagas")
    public List<VagaOutDto> list(@RequestParam(required = false) StatusVaga status) {
        return status != null ? service.findByStatus(status) : service.findAll();
    }

    @GetMapping("/vagas/{id}")
    public VagaOutDto get(@PathVariable Long id) {
        return service.findById(id);
    }

    @PutMapping("/vagas/{id}")
    public VagaOutDto update(@PathVariable Long id, @RequestBody @Valid VagaInDto in) {
        return service.update(id, in);
    }

    @DeleteMapping("/vagas/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @GetMapping("/vagas/{id}/status")
    public ResponseEntity<VagaStatusOutDto> status(@PathVariable Long id) {
        var st = service.getStatus(id);
        var dto = (st == null)
                ? new VagaStatusOutDto(id, "DESCONHECIDO", null, null)
                : new VagaStatusOutDto(st.getVagaId(), st.getStatusOcupacao(), st.getUltimoOcorrido(), st.getSensorId());
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/estacionamentos/{estacionamentoId}/vagas")
    public List<VagaOutDto> listByEstacionamento(@PathVariable Long estacionamentoId) {
        return service.findByEstacionamento(estacionamentoId);
    }
}
