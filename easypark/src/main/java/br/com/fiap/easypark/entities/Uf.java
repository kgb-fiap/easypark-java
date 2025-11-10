package br.com.fiap.easypark.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "UF")
public class Uf {

    @Id
    @EqualsAndHashCode.Include
    @Column(name = "SIGLA", length = 2, nullable = false)
    private String sigla;

    @Column(name = "NOME", length = 50, nullable = false)
    private String nome;
}