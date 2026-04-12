package br.com.fiap.easypark.dto.web;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EtaForm {
    @NotNull(message = "Informe o ETA.")
    @Min(value = 0, message = "O ETA nao pode ser negativo.")
    @Max(value = 240, message = "O ETA maximo aceito nesta tela e de 240 minutos.")
    private Integer minutos = 10;
}
