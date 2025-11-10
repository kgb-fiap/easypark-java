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
@Table(name = "BAIRRO",
        uniqueConstraints = @UniqueConstraint(name = "UQ_BAIRRO_NOME_CIDADE", columnNames = {"NOME", "CIDADE_ID"}))
public class Bairro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "NOME", length = 80, nullable = false)
    private String nome;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "CIDADE_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_BAIRRO_CIDADE"))
    private Cidade cidade;
}