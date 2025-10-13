
package br.com.fiap.easypark.entities;

import br.com.fiap.easypark.entities.converters.YesNoConverter;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Entity
@Table(name = "VAGA", uniqueConstraints =
@UniqueConstraint(name = "UQ_VAGA_CODIGO_NIVEL", columnNames = {"NIVEL_ID","CODIGO"}))
public class Vaga {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID") private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "NIVEL_ID", nullable = false,
            foreignKey = @ForeignKey(name = "FK_VAGA_NIVEL"))
    private Nivel nivel;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "TIPO_VAGA_ID", nullable = false,
            foreignKey = @ForeignKey(name = "FK_VAGA_TIPOVAGA"))
    private TipoVaga tipoVaga;

    @Column(name = "CODIGO", nullable = false, length = 50) private String codigo;

    @Convert(converter = YesNoConverter.class)
    @Column(name = "ATIVA", nullable = false, length = 1) private Boolean ativa;

    @Column(name = "CRIADO_EM") private OffsetDateTime criadoEm;
}
