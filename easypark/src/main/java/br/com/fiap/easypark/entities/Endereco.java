package br.com.fiap.easypark.entities;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "ENDERECO",
        indexes = {
                @Index(name = "IDX_ENDERECO_BAIRRO", columnList = "BAIRRO_ID"),
                @Index(name = "IDX_ENDERECO_CEP", columnList = "CEP")
        })
public class Endereco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "CEP", length = 10)
    private String cep;

    @Column(name = "LOGRADOURO", length = 150)
    private String logradouro;

    @Column(name = "NUMERO", length = 20)
    private String numero;

    @Column(name = "COMPLEMENTO", length = 50)
    private String complemento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BAIRRO_ID", foreignKey = @ForeignKey(name = "FK_ENDERECO_BAIRRO"))
    private Bairro bairro;

    @Column(name = "LATITUDE", precision = 9, scale = 6)
    private BigDecimal latitude;

    @Column(name = "LONGITUDE", precision = 9, scale = 6)
    private BigDecimal longitude;
}