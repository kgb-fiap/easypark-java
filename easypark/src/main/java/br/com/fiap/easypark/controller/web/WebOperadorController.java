package br.com.fiap.easypark.controller.web;

import br.com.fiap.easypark.dto.web.SensorEventoForm;
import br.com.fiap.easypark.services.ReservaJobsService;
import br.com.fiap.easypark.services.ReservaWebService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequestMapping("/web/operador")
public class WebOperadorController {
    private final ReservaWebService reservaWebService;
    private final ReservaJobsService reservaJobsService;

    public WebOperadorController(ReservaWebService reservaWebService, ReservaJobsService reservaJobsService) {
        this.reservaWebService = reservaWebService;
        this.reservaJobsService = reservaJobsService;
    }

    @GetMapping
    public String painel(@RequestParam(required = false) Long sensorEventoId,
                         @RequestParam(required = false) Integer reservasCanceladas,
                         @RequestParam(required = false) Integer preReservasCanceladas,
                         @RequestParam(required = false) String erro,
                         Model model,
                         Principal principal) {
        if (!model.containsAttribute("sensorEventoForm")) {
            model.addAttribute("sensorEventoForm", new SensorEventoForm());
        }
        model.addAttribute("username", principal.getName());
        model.addAttribute("reservas", reservaWebService.listarReservasRecentes());
        model.addAttribute("sensores", reservaWebService.listarSensoresAtivos());
        model.addAttribute("eventos", reservaWebService.listarEventosRecentes());
        model.addAttribute("sensorEventoId", sensorEventoId);
        model.addAttribute("reservasCanceladas", reservasCanceladas);
        model.addAttribute("preReservasCanceladas", preReservasCanceladas);
        model.addAttribute("erro", erro);
        return "web/operador";
    }

    @PostMapping("/sensor-eventos")
    public String registrarSensorEvento(@Valid @ModelAttribute("sensorEventoForm") SensorEventoForm form,
                                        BindingResult bindingResult,
                                        RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addAttribute("erro", "Revise os campos da leitura do sensor de presenca.");
            return "redirect:/web/operador";
        }

        try {
            var id = reservaWebService.registrarEventoSensor(form);
            redirectAttributes.addAttribute("sensorEventoId", id);
        } catch (RuntimeException ex) {
            redirectAttributes.addAttribute("erro", ex.getMessage());
        }
        return "redirect:/web/operador";
    }

    @PostMapping("/timeouts/reservas")
    public String executarTimeoutReservas(RedirectAttributes redirectAttributes) {
        redirectAttributes.addAttribute("reservasCanceladas", reservaJobsService.runReservaTimeouts());
        return "redirect:/web/operador";
    }

    @PostMapping("/timeouts/pre-reservas")
    public String executarTimeoutPreReservas(RedirectAttributes redirectAttributes) {
        redirectAttributes.addAttribute("preReservasCanceladas", reservaJobsService.runPreReservaTimeouts());
        return "redirect:/web/operador";
    }
}
