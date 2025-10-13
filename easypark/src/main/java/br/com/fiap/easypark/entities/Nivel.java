package br.com.fiap.easypark.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Entity @Table(name = "NIVEL")
public class Nivel {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID") private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "ESTACIONAMENTO_ID", nullable = false,
            foreignKey = @ForeignKey(name = "FK_NIVEL_EST"))
    private Estacionamento estacionamento;

    @Column(name = "NOME", length = 150) private String nome;
    @Column(name = "ORDEM") private Integer ordem;
    @Column(name = "CRIADO_EM") private OffsetDateTime criadoEm;
}
