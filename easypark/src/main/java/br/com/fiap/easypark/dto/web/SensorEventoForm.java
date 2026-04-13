package br.com.fiap.easypark.dto.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class SensorEventoForm {
    @NotNull(message = "Informe o sensor.")
    @Positive(message = "O sensor deve ser positivo.")
    private Long sensorId;

    @NotBlank(message = "Informe o status.")
    @Pattern(regexp = "LIVRE|OCUPADA|DESCONHECIDO", message = "Status deve ser LIVRE, OCUPADA ou DESCONHECIDO.")
    private String status = "OCUPADA";

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime ocorridoEm = LocalDateTime.now().withSecond(0).withNano(0);

    @Size(max = 4000, message = "Payload deve ter no maximo 4000 caracteres.")
    private String payload = "{}";
}
