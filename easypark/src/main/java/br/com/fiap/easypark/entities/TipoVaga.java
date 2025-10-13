
package br.com.fiap.easypark.entities;

import br.com.fiap.easypark.entities.converters.YesNoConverter;
import jakarta.persistence.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Entity @Table(name = "TIPO_VAGA")
public class TipoVaga {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID") private Long id;

    @Column(name = "NOME", nullable = false, length = 50) private String nome;

    @Convert(converter = YesNoConverter.class)
    @Column(name = "EH_ELETRICA", nullable = false, length = 1) private Boolean ehEletrica;

    @Convert(converter = YesNoConverter.class)
    @Column(name = "EH_ACESSIVEL", nullable = false, length = 1) private Boolean ehAcessivel;

    @Convert(converter = YesNoConverter.class)
    @Column(name = "EH_MOTO", nullable = false, length = 1) private Boolean ehMoto;

    @Column(name = "TARIFA_POR_MINUTO", precision = 12, scale = 4, nullable = false)
    private java.math.BigDecimal tarifaPorMinuto;
}
