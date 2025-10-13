package br.com.fiap.easypark.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Entity @Table(name = "VAGA_STATUS")
public class VagaStatus {
    @Id
    @Column(name = "VAGA_ID") private Long vagaId; // PK = FK

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "VAGA_ID", insertable = false, updatable = false)
    private Vaga vaga;

    @Column(name = "STATUS_OCUPACAO", length = 30) private String statusOcupacao; // LIVRE|OCUPADA|DESCONHECIDO
    @Column(name = "ULTIMO_OCORRIDO") private OffsetDateTime ultimoOcorrido;

    @Column(name = "SENSOR_ID") private Long sensorId;
}
