package br.com.fiap.easypark.controller.web;

import br.com.fiap.easypark.dto.web.ReservaCreateForm;
import br.com.fiap.easypark.services.EstacionamentoWebService;
import br.com.fiap.easypark.services.ReservaWebService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequestMapping("/web/vagas")
public class WebVagaController {

    private final EstacionamentoWebService estacionamentoWebService;
    private final ReservaWebService reservaWebService;

    public WebVagaController(EstacionamentoWebService estacionamentoWebService,
                             ReservaWebService reservaWebService) {
        this.estacionamentoWebService = estacionamentoWebService;
        this.reservaWebService = reservaWebService;
    }

    @GetMapping
    public String list() {
        return "redirect:/web/estacionamentos";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id) {
        Long estacionamentoId = estacionamentoWebService.buscarEstacionamentoIdPorVaga(id);
        return "redirect:/web/estacionamentos/" + estacionamentoId;
    }

    @PostMapping("/{id}/pre-reservas")
    public String criarPreReserva(@PathVariable Long id,
                                  @Valid @ModelAttribute("preReservaForm") ReservaCreateForm form,
                                  BindingResult bindingResult,
                                  Principal principal,
                                  RedirectAttributes redirectAttributes) {
        Long estacionamentoId = estacionamentoWebService.buscarEstacionamentoIdPorVaga(id);

        if (bindingResult.hasErrors()) {
            redirectAttributes.addAttribute("erro", "Revise os campos da pre-reserva.");
            return "redirect:/web/estacionamentos/" + estacionamentoId;
        }

        try {
            var result = reservaWebService.criarPreReserva(principal.getName(), id, form);
            redirectAttributes.addAttribute("created", result.reservaId());
            redirectAttributes.addAttribute("valorPrevisto", result.valorPrevisto());
            return "redirect:/web/minhas-reservas";
        } catch (RuntimeException ex) {
            redirectAttributes.addAttribute("erro", ex.getMessage());
            return "redirect:/web/estacionamentos/" + estacionamentoId;
        }
    }
}
