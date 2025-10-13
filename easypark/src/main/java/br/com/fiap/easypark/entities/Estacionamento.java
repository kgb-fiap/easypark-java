package br.com.fiap.easypark.entities;
import java.math.BigDecimal;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Entity @Table(name = "ESTACIONAMENTO")
public class Estacionamento {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID") private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "OPERADORA_ID", nullable = false,
            foreignKey = @ForeignKey(name = "FK_EST_OPERADORA"))
    private Operadora operadora;

    @Column(name = "NOME", nullable = false, length = 250)
    private String nome;

    @Column(name = "ENDERECO", length = 500)
    private String endereco;

    @Column(name = "LATITUDE",  precision = 9, scale = 6)
    private BigDecimal latitude;

    @Column(name = "LONGITUDE", precision = 9, scale = 6)
    private BigDecimal longitude;

    @Column(name = "ESPERA_MINUTOS")
    private Integer esperaMinutos;

    @Column(name = "TOLERANCIA_MINUTOS")
    private Integer toleranciaMinutos;


    @Column(name = "LIMITE_NO_SHOW")
    private Integer limiteNoShow;

    @Column(name = "MAX_ANTECEDENCIA_MINUTOS")
    private Integer maxAntecedenciaMin;

    @Column(name = "MAX_ANTECEDENCIA_MINUTOS_SUSPENSO")
    private Integer maxAntecedenciaMinSuspenso;

    @Column(name = "CRIADO_EM")
    private OffsetDateTime criadoEm;
}
