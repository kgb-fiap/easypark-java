package br.com.fiap.easypark.controller.web;

import br.com.fiap.easypark.dto.web.EtaForm;
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

import java.security.Principal;

@Controller
@RequestMapping("/web/minhas-reservas")
public class WebReservaController {
    private final ReservaWebService reservaWebService;

    public WebReservaController(ReservaWebService reservaWebService) {
        this.reservaWebService = reservaWebService;
    }

    @GetMapping
    public String list(@RequestParam(required = false) Long created,
                       @RequestParam(required = false) String etaStatus,
                       @RequestParam(required = false) String etaMsg,
                       @RequestParam(required = false) String erro,
                       Model model,
                       Principal principal) {
        model.addAttribute("username", principal.getName());
        model.addAttribute("reservas", reservaWebService.listarReservasDoUsuario(principal.getName()));
        model.addAttribute("etaForm", new EtaForm());
        model.addAttribute("created", created);
        model.addAttribute("etaStatus", etaStatus);
        model.addAttribute("etaMsg", etaMsg);
        model.addAttribute("erro", erro);
        return "web/minhas-reservas";
    }

    @PostMapping("/{id}/eta")
    public String atualizarEta(@PathVariable Long id,
                               @Valid @ModelAttribute("etaForm") EtaForm form,
                               BindingResult bindingResult,
                               Principal principal,
                               RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addAttribute("erro", "Informe um ETA entre 0 e 240 minutos.");
            return "redirect:/web/minhas-reservas";
        }

        try {
            var out = reservaWebService.atualizarEtaDoUsuario(principal.getName(), id, form.getMinutos());
            redirectAttributes.addAttribute("etaStatus", out.status());
            redirectAttributes.addAttribute("etaMsg", out.msg());
        } catch (RuntimeException ex) {
            redirectAttributes.addAttribute("erro", ex.getMessage());
        }
        return "redirect:/web/minhas-reservas";
    }
}
