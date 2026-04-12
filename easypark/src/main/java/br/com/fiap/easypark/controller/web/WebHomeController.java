package br.com.fiap.easypark.controller.web;

import br.com.fiap.easypark.services.EstacionamentoService;
import br.com.fiap.easypark.services.VagaService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
@RequestMapping("/web")
public class WebHomeController {

    private final EstacionamentoService estacionamentoService;
    private final VagaService vagaService;

    public WebHomeController(EstacionamentoService estacionamentoService, VagaService vagaService) {
        this.estacionamentoService = estacionamentoService;
        this.vagaService = vagaService;
    }

    @GetMapping("/login")
    public String login(@RequestParam(required = false) String error,
                        @RequestParam(required = false) String logout,
                        Model model) {
        model.addAttribute("loginError", error != null);
        model.addAttribute("logoutSuccess", logout != null);
        return "web/login";
    }

    @GetMapping
    public String dashboard(Model model, Principal principal) {
        model.addAttribute("username", principal.getName());
        model.addAttribute("estacionamentos", estacionamentoService.findAll(PageRequest.of(0, 5)).content());
        model.addAttribute("vagas", vagaService.search(null, null, null, null, PageRequest.of(0, 5)).content());
        return "web/dashboard";
    }

}
