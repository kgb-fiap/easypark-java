package br.com.fiap.easypark.entities;

import br.com.fiap.easypark.entities.converters.YesNoConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "USUARIO")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "NOME", length = 250)
    private String nome;

    @Column(name = "EMAIL", nullable = false, length = 320)
    private String email;

    @Column(name = "SENHA_HASH", nullable = false, length = 255)
    private String senhaHash;

    @Column(name = "TELEFONE", length = 30)
    private String telefone;

    @Column(name = "PERFIL", nullable = false, length = 20)
    private String perfil;

    @Convert(converter = YesNoConverter.class)
    @Column(name = "SUSPENSO", nullable = false, length = 1)
    private Boolean suspenso;

    @Column(name = "NO_SHOWS")
    private Integer noShows;

    @Column(name = "SUSPENSAO_ATE")
    private OffsetDateTime suspensaoAte;

    @Column(name = "CRIADO_EM")
    private OffsetDateTime criadoEm;

    public boolean isBloqueado() {
        boolean suspensoAgora = Boolean.TRUE.equals(suspenso);
        boolean dentroDaSuspensao = suspensaoAte != null && suspensaoAte.isAfter(OffsetDateTime.now());
        return suspensoAgora || dentroDaSuspensao;
    }
}
