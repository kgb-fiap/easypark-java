
package br.com.fiap.easypark.controller;

import br.com.fiap.easypark.dto.EtaUpdateOutDto;
import br.com.fiap.easypark.dto.JobCountOutDto;
import br.com.fiap.easypark.services.ReservaJobsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequiredArgsConstructor
@RestController @RequestMapping("/jobs")
public class JobsController {

    private final ReservaJobsService jobs;

    @PostMapping("/reservas/timeouts")
    public JobCountOutDto reservaTimeouts() {
        int n = jobs.runReservaTimeouts();
        return new JobCountOutDto(n);
    }

    @PostMapping("/prereservas/timeouts")
    public JobCountOutDto preReservaTimeouts() {
        int n = jobs.runPreReservaTimeouts();
        return new JobCountOutDto(n);
    }

    @PostMapping("/reservas/{id}/eta")
    public EtaUpdateOutDto atualizarEta(@PathVariable long id, @RequestParam int minutos) {
        var out = jobs.updateEta(id, minutos);
        return new EtaUpdateOutDto(out.get("status"), out.get("msg"));
    }
}
