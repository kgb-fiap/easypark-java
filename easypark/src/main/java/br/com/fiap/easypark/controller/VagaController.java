package br.com.fiap.easypark.controller;

import br.com.fiap.easypark.dto.PageResponse;
import br.com.fiap.easypark.dto.VagaInDto;
import br.com.fiap.easypark.dto.VagaOutDto;
import br.com.fiap.easypark.dto.VagaStatusOutDto;
import br.com.fiap.easypark.entities.enums.StatusVaga;
import br.com.fiap.easypark.hateoas.VagaModelAssembler;
import br.com.fiap.easypark.services.VagaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import java.util.List;

@RestController
@RequestMapping
public class VagaController {

    private final VagaService service;
    private final VagaModelAssembler assembler;

    public VagaController(VagaService service, VagaModelAssembler assembler) {
        this.service = service;
        this.assembler = assembler;
    }


    @GetMapping("/vagas")
    @Operation(summary = "Lista vagas com paginação e filtros opcionais")
    public ResponseEntity<PageResponse<VagaOutDto>> list(
            @RequestParam(required = false) StatusVaga status,
            @RequestParam(required = false) Long nivelId,
            @RequestParam(required = false) Long tipoVagaId,
            @RequestParam(required = false) Long estacionamentoId,
            @Parameter(description = "Paginação e ordenação (ex.: ?page=0&size=20&sort=codigo,asc)")
            @PageableDefault(size = 20, sort = "codigo") Pageable pageable
    ) {
        return ResponseEntity.ok(service.search(status, nivelId, tipoVagaId, estacionamentoId, pageable));
    }

    @GetMapping("/vagas/{id}")
    public ResponseEntity<org.springframework.hateoas.EntityModel<VagaOutDto>> get(@PathVariable Long id) {
        var dto = service.findById(id);
        return ResponseEntity.ok(assembler.toModel(dto));
    }

    @PostMapping("/vagas")
    public ResponseEntity<VagaOutDto> create(@RequestBody @Valid VagaInDto in) {
        var saved = service.create(in);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
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