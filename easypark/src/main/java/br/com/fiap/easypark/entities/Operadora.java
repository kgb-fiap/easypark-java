package br.com.fiap.easypark.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Entity @Table(name = "OPERADORA")
public class Operadora {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID") private Long id;

    @Column(name = "CNPJ", nullable = false, length = 14) private String cnpj;
    @Column(name = "RAZAO_SOCIAL", nullable = false, length = 250) private String razaoSocial;
    @Column(name = "NOME_FANTASIA", length = 250) private String nomeFantasia;
    @Column(name = "TELEFONE", length = 30) private String telefone;

    @Column(name = "CRIADO_EM") private OffsetDateTime criadoEm;
}
