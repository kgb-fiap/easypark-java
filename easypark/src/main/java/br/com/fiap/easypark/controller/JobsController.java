
package br.com.fiap.easypark.controller;

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
    public Map<String, Object> reservaTimeouts() {
        int n = jobs.runReservaTimeouts();
        return Map.of("canceladas", n);
    }

    @PostMapping("/prereservas/timeouts")
    public Map<String, Object> preReservaTimeouts() {
        int n = jobs.runPreReservaTimeouts();
        return Map.of("canceladas", n);
    }

    @PostMapping("/reservas/{id}/eta")
    public ResponseEntity<?> atualizarEta(@PathVariable long id, @RequestParam int minutos) {
        var out = jobs.updateEta(id, minutos);
        return ResponseEntity.ok(out);
    }
}
