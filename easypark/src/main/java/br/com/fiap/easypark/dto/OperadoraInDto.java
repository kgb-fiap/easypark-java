package br.com.fiap.easypark.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record OperadoraInDto(
        @NotBlank
        @Pattern(regexp = "\\d{14}", message = "deve conter 14 digitos numericos")
        String cnpj,

        @NotBlank
        @Size(max = 250)
        String razaoSocial,

        @Size(max = 250)
        String nomeFantasia,

        @Size(max = 30)
        String telefone
) {}
