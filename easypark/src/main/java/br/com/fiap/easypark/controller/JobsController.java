
package br.com.fiap.easypark.controller;

import br.com.fiap.easypark.dto.EtaUpdateOutDto;
import br.com.fiap.easypark.dto.JobCountOutDto;
import br.com.fiap.easypark.services.ReservaJobsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/jobs")
public class JobsController {

    private final ReservaJobsService jobs;

    public JobsController(ReservaJobsService jobs) {
        this.jobs = jobs;
    }

    @PostMapping("/reservas/timeouts")
    public ResponseEntity<JobCountOutDto> reservaTimeouts() {
        int n = jobs.runReservaTimeouts();
        return ResponseEntity.ok(new JobCountOutDto(n));
    }

    @PostMapping("/prereservas/timeouts")
    public ResponseEntity<JobCountOutDto> preReservaTimeouts() {
        int n = jobs.runPreReservaTimeouts();
        return ResponseEntity.ok(new JobCountOutDto(n));
    }

    @PostMapping("/reservas/{id}/eta")
    public ResponseEntity<EtaUpdateOutDto> atualizarEta(@PathVariable long id, @RequestParam int minutos) {
        var out = jobs.updateEta(id, minutos);
        return ResponseEntity.ok(out);
    }
}