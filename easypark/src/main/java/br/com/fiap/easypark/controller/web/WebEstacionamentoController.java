package br.com.fiap.easypark.controller.web;

import br.com.fiap.easypark.dto.web.ReservaCreateForm;
import br.com.fiap.easypark.services.EstacionamentoWebService;
import br.com.fiap.easypark.services.ReservaWebService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.security.Principal;

@Controller
@RequestMapping("/web/estacionamentos")
public class WebEstacionamentoController {

    private final EstacionamentoWebService estacionamentoWebService;
    private final ReservaWebService reservaWebService;

    public WebEstacionamentoController(EstacionamentoWebService estacionamentoWebService,
                                       ReservaWebService reservaWebService) {
        this.estacionamentoWebService = estacionamentoWebService;
        this.reservaWebService = reservaWebService;
    }

    @GetMapping
    public String list(@RequestParam(required = false) String destino,
                       @RequestParam(required = false) BigDecimal lat,
                       @RequestParam(required = false) BigDecimal lng,
                       Model model,
                       Principal principal) {
        model.addAttribute("username", principal.getName());
        model.addAttribute("resultado", estacionamentoWebService.buscar(destino, lat, lng));
        model.addAttribute("destino", destino);
        model.addAttribute("lat", lat);
        model.addAttribute("lng", lng);
        return "web/estacionamentos";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id,
                         @RequestParam(required = false) String erro,
                         Model model,
                         Principal principal) {
        if (!model.containsAttribute("preReservaForm")) {
            model.addAttribute("preReservaForm", new ReservaCreateForm());
        }
        fillDetailModel(id, model, principal);
        model.addAttribute("erro", erro);
        return "web/estacionamento-detalhe";
    }

    @PostMapping("/{estacionamentoId}/vagas/{vagaId}/pre-reservas")
    public String criarPreReserva(@PathVariable Long estacionamentoId,
                                  @PathVariable Long vagaId,
                                  @Valid @ModelAttribute("preReservaForm") ReservaCreateForm form,
                                  BindingResult bindingResult,
                                  Model model,
                                  Principal principal,
                                  RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            fillDetailModel(estacionamentoId, model, principal);
            model.addAttribute("erro", "Revise os campos da pre-reserva.");
            return "web/estacionamento-detalhe";
        }

        try {
            var result = reservaWebService.criarPreReserva(principal.getName(), vagaId, form);
            redirectAttributes.addAttribute("created", result.reservaId());
            redirectAttributes.addAttribute("valorPrevisto", result.valorPrevisto());
            return "redirect:/web/minhas-reservas";
        } catch (RuntimeException ex) {
            redirectAttributes.addAttribute("erro", ex.getMessage());
            return "redirect:/web/estacionamentos/" + estacionamentoId;
        }
    }

    private void fillDetailModel(Long estacionamentoId, Model model, Principal principal) {
        model.addAttribute("username", principal.getName());
        model.addAttribute("estacionamento", estacionamentoWebService.detalhar(estacionamentoId));
        model.addAttribute("vagas", estacionamentoWebService.listarVagas(estacionamentoId));
    }
}
