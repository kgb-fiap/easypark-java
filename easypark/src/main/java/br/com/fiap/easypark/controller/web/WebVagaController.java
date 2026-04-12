package br.com.fiap.easypark.controller.web;

import br.com.fiap.easypark.dto.VagaStatusOutDto;
import br.com.fiap.easypark.dto.web.ReservaCreateForm;
import br.com.fiap.easypark.entities.enums.StatusVaga;
import br.com.fiap.easypark.services.EstacionamentoService;
import br.com.fiap.easypark.services.ReservaWebService;
import br.com.fiap.easypark.services.VagaService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequestMapping("/web/vagas")
public class WebVagaController {

    private final VagaService vagaService;
    private final EstacionamentoService estacionamentoService;
    private final ReservaWebService reservaWebService;

    public WebVagaController(VagaService vagaService,
                             EstacionamentoService estacionamentoService,
                             ReservaWebService reservaWebService) {
        this.vagaService = vagaService;
        this.estacionamentoService = estacionamentoService;
        this.reservaWebService = reservaWebService;
    }

    @GetMapping
    public String list(@RequestParam(required = false) StatusVaga status,
                       @RequestParam(required = false) Long nivelId,
                       @RequestParam(required = false) Long tipoVagaId,
                       @RequestParam(required = false) Long estacionamentoId,
                       @PageableDefault(size = 20, sort = "codigo") Pageable pageable,
                       Model model,
                       Principal principal) {
        model.addAttribute("username", principal.getName());
        model.addAttribute("statusOptions", StatusVaga.values());
        model.addAttribute("selectedStatus", status);
        model.addAttribute("nivelId", nivelId);
        model.addAttribute("tipoVagaId", tipoVagaId);
        model.addAttribute("estacionamentoId", estacionamentoId);
        model.addAttribute("estacionamentos", estacionamentoService.findAll(Pageable.ofSize(100)).content());
        model.addAttribute("page", vagaService.search(status, nivelId, tipoVagaId, estacionamentoId, pageable));
        return "web/vagas";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id,
                         @RequestParam(required = false) String erro,
                         @RequestParam(required = false) String sucesso,
                         Model model,
                         Principal principal) {
        if (!model.containsAttribute("preReservaForm")) {
            model.addAttribute("preReservaForm", new ReservaCreateForm());
        }
        model.addAttribute("erro", erro);
        model.addAttribute("sucesso", sucesso);
        fillVagaDetailModel(id, model, principal);
        return "web/vaga-detalhe";
    }

    @PostMapping("/{id}/pre-reservas")
    public String criarPreReserva(@PathVariable Long id,
                                  @Valid @ModelAttribute("preReservaForm") ReservaCreateForm form,
                                  BindingResult bindingResult,
                                  Model model,
                                  Principal principal,
                                  RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            fillVagaDetailModel(id, model, principal);
            model.addAttribute("erro", "Revise os campos da pre-reserva.");
            return "web/vaga-detalhe";
        }

        try {
            var reservaId = reservaWebService.criarPreReserva(principal.getName(), id, form);
            redirectAttributes.addAttribute("created", reservaId);
            return "redirect:/web/minhas-reservas";
        } catch (RuntimeException ex) {
            redirectAttributes.addAttribute("erro", ex.getMessage());
            return "redirect:/web/vagas/{id}";
        }
    }

    private void fillVagaDetailModel(Long id, Model model, Principal principal) {
        var vaga = vagaService.findById(id);
        var status = vagaService.getStatus(id);
        var statusDto = status == null
                ? new VagaStatusOutDto(id, "DESCONHECIDO", null, null)
                : new VagaStatusOutDto(status.getVagaId(), status.getStatusOcupacao(), status.getUltimoOcorrido(), status.getSensorId());

        model.addAttribute("username", principal.getName());
        model.addAttribute("vaga", vaga);
        model.addAttribute("status", statusDto);
    }
}
