package br.com.fiap.easypark.dto.web;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ReservaCreateForm {
    @NotNull(message = "Informe o inicio previsto.")
    @FutureOrPresent(message = "O inicio previsto nao pode estar no passado.")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime inicioPrevisto = LocalDateTime.now().plusMinutes(30).withSecond(0).withNano(0);

    @NotNull(message = "Informe a duracao.")
    @Min(value = 15, message = "A duracao minima e de 15 minutos.")
    @Max(value = 720, message = "A duracao maxima e de 720 minutos.")
    private Integer duracaoMinutos = 60;

    @NotNull(message = "Informe a antecedencia.")
    @Min(value = 0, message = "A antecedencia nao pode ser negativa.")
    @Max(value = 240, message = "A antecedencia maxima e de 240 minutos.")
    private Integer antecedenciaMinutos = 15;
}
