package br.com.fiap.easypark.dto.web;

import jakarta.validation.constraints.AssertTrue;
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
    @NotNull(message = "Informe a chegada prevista.")
    @FutureOrPresent(message = "A chegada prevista nao pode estar no passado.")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime inicioPrevisto = LocalDateTime.now().plusMinutes(30).withSecond(0).withNano(0);

    @NotNull(message = "Informe o tempo de uso da vaga.")
    @Min(value = 15, message = "O tempo minimo de uso da vaga e de 15 minutos.")
    @Max(value = 1440, message = "O tempo maximo de uso da vaga e de 1440 minutos.")
    private Integer duracaoMinutos = 60;

    @NotNull(message = "Informe quando a vaga deve ser bloqueada antes da chegada.")
    @Min(value = 0, message = "O bloqueio antes da chegada nao pode ser negativo.")
    @Max(value = 240, message = "O bloqueio antes da chegada deve ser de no maximo 240 minutos.")
    private Integer antecedenciaMinutos = 15;

    @AssertTrue(message = "Escolha um tempo de uso em intervalos de 15 minutos.")
    public boolean isDuracaoEmIntervaloDeQuinzeMinutos() {
        return duracaoMinutos == null || duracaoMinutos % 15 == 0;
    }
}
