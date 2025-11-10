package br.com.fiap.easypark.controller;

import br.com.fiap.easypark.dto.VagaInDto;
import br.com.fiap.easypark.dto.VagaOutDto;
import br.com.fiap.easypark.dto.VagaStatusOutDto;
import br.com.fiap.easypark.entities.enums.StatusVaga;
import br.com.fiap.easypark.services.VagaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping
public class VagaController {

    private final VagaService service;

    public VagaController(VagaService service) {
        this.service = service;
    }

    @PostMapping("/vagas")
    public ResponseEntity<VagaOutDto> create(@RequestBody @Valid VagaInDto in) {
        var saved = service.create(in);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping("/vagas")
    public ResponseEntity<List<VagaOutDto>> list(@RequestParam(required = false) StatusVaga status) {
        var list = status != null ? service.findByStatus(status) : service.findAll();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/vagas/{id}")
    public ResponseEntity<VagaOutDto> get(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PutMapping("/vagas/{id}")
    public ResponseEntity<VagaOutDto> update(@PathVariable Long id, @RequestBody @Valid VagaInDto in) {
        return ResponseEntity.ok(service.update(id, in));
    }

    @DeleteMapping("/vagas/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
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
    public ResponseEntity<List<VagaOutDto>> listByEstacionamento(@PathVariable Long estacionamentoId) {
        return ResponseEntity.ok(service.findByEstacionamento(estacionamentoId));
    }
}