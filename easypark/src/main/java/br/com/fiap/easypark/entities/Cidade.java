package br.com.fiap.easypark.entities;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "CIDADE",
        uniqueConstraints = @UniqueConstraint(name = "UQ_CIDADE_NOME_UF", columnNames = {"NOME", "UF_SIGLA"}))
public class Cidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "NOME", length = 80, nullable = false)
    private String nome;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "UF_SIGLA", nullable = false, foreignKey = @ForeignKey(name = "FK_CIDADE_UF"))
    private Uf uf;
}