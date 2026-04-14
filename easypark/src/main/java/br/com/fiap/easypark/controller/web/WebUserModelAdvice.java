package br.com.fiap.easypark.controller.web;

import br.com.fiap.easypark.configs.FirebasePrincipal;
import br.com.fiap.easypark.security.EasyparkUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice(basePackageClasses = WebHomeController.class)
public class WebUserModelAdvice {

    @ModelAttribute("displayName")
    public String displayName(Authentication authentication) {
        if (authentication == null) {
            return "";
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof EasyparkUserDetails userDetails) {
            return userDetails.displayName();
        }
        if (principal instanceof FirebasePrincipal firebasePrincipal && hasText(firebasePrincipal.name())) {
            return firebasePrincipal.name().trim();
        }
        return authentication.getName();
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
