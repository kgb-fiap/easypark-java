package br.com.fiap.easypark.controller;

import br.com.fiap.easypark.dto.OperadoraInDto;
import br.com.fiap.easypark.dto.OperadoraOutDto;
import br.com.fiap.easypark.dto.PageResponse;
import br.com.fiap.easypark.services.OperadoraService;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/operadoras")
public class OperadoraController {

    private final OperadoraService service;

    public OperadoraController(OperadoraService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<PageResponse<OperadoraOutDto>> list(
            @ParameterObject @PageableDefault(size = 20, sort = "razaoSocial") Pageable pageable
    ) {
        return ResponseEntity.ok(service.findAll(pageable));
    }

    @PostMapping
    public ResponseEntity<OperadoraOutDto> create(@RequestBody @Valid OperadoraInDto in) {
        var saved = service.create(in);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OperadoraOutDto> get(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OperadoraOutDto> update(@PathVariable Long id,
                                                  @RequestBody @Valid OperadoraInDto in) {
        return ResponseEntity.ok(service.update(id, in));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
