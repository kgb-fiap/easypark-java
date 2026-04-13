package br.com.fiap.easypark.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RootRedirectController {

    @GetMapping("/")
    public String index() {
        return "redirect:/web";
    }
}
