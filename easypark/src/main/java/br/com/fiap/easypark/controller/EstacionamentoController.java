package br.com.fiap.easypark.controller;

import br.com.fiap.easypark.dto.EstacionamentoInDto;
import br.com.fiap.easypark.dto.EstacionamentoOutDto;
import br.com.fiap.easypark.services.EstacionamentoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/estacionamentos")
public class EstacionamentoController {

    private final EstacionamentoService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EstacionamentoOutDto create(@RequestBody @Valid EstacionamentoInDto in) {
        return service.create(in);
    }

    @GetMapping
    public List<EstacionamentoOutDto> list() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public EstacionamentoOutDto get(@PathVariable Long id) {
        return service.findById(id);
    }

    @PutMapping("/{id}")
    public EstacionamentoOutDto update(@PathVariable Long id, @RequestBody @Valid EstacionamentoInDto in) {
        return service.update(id, in);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
