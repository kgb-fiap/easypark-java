package br.com.fiap.easypark.services;

import br.com.fiap.easypark.dto.web.EstacionamentoBuscaResultado;
import br.com.fiap.easypark.dto.web.EstacionamentoWebDto;
import br.com.fiap.easypark.dto.web.VagaWebDto;
import br.com.fiap.easypark.exceptions.EntityNotFoundException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class EstacionamentoWebService {
    private final EntityManager em;
    private final ReservaValorService reservaValorService;

    public EstacionamentoWebService(EntityManager em, ReservaValorService reservaValorService) {
        this.em = em;
        this.reservaValorService = reservaValorService;
    }

    @Transactional(readOnly = true)
    public EstacionamentoBuscaResultado buscar(String destino, BigDecimal latitude, BigDecimal longitude) {
        var referencia = referencia(destino, latitude, longitude);
        return new EstacionamentoBuscaResultado(
                destino,
                referencia.latitude(),
                referencia.longitude(),
                referencia.porEnderecoCadastrado(),
                listarEstacionamentos(null, destino, referencia.latitude(), referencia.longitude())
        );
    }

    @Transactional(readOnly = true)
    public EstacionamentoWebDto detalhar(Long id) {
        return listarEstacionamentos(id, null, null, null).stream()
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Estacionamento " + id + " nao encontrado"));
    }

    @Transactional(readOnly = true)
    public List<VagaWebDto> listarVagas(Long estacionamentoId) {
        @SuppressWarnings("unchecked")
        List<Object[]> rows = em.createNativeQuery("""
                SELECT v.id,
                       v.codigo,
                       v.ativa,
                       n.nome,
                       n.ordem,
                       tv.nome,
                       tv.tarifa_por_minuto,
                       tv.eh_eletrica,
                       tv.eh_acessivel,
                       tv.eh_moto,
                       NVL(vs.status_ocupacao, 'DESCONHECIDO'),
                       TO_CHAR(vs.ultimo_ocorrido, 'YYYY-MM-DD HH24:MI TZH:TZM'),
                       vs.sensor_id,
                       MAX(CASE
                             WHEN r.estado IN ('PRE_RESERVA','RESERVA','OCUPADA') THEN r.estado
                             ELSE NULL
                           END) AS reserva_ativa_estado
                  FROM vaga v
                  JOIN nivel n ON n.id = v.nivel_id
                  JOIN tipo_vaga tv ON tv.id = v.tipo_vaga_id
                  LEFT JOIN vaga_status vs ON vs.vaga_id = v.id
                  LEFT JOIN reserva r ON r.vaga_id = v.id
                 WHERE n.estacionamento_id = :estacionamentoId
                 GROUP BY v.id, v.codigo, v.ativa, n.nome, n.ordem, tv.nome, tv.tarifa_por_minuto,
                          tv.eh_eletrica, tv.eh_acessivel, tv.eh_moto, vs.status_ocupacao,
                          vs.ultimo_ocorrido, vs.sensor_id
                 ORDER BY NVL(n.ordem, 999), n.nome, v.codigo
                """)
                .setParameter("estacionamentoId", estacionamentoId)
                .getResultList();

        return rows.stream()
                .map(this::toVaga)
                .toList();
    }

    @Transactional(readOnly = true)
    public Long buscarEstacionamentoIdPorVaga(Long vagaId) {
        try {
            return toLong(em.createNativeQuery("""
                    SELECT n.estacionamento_id
                      FROM vaga v
                      JOIN nivel n ON n.id = v.nivel_id
                     WHERE v.id = :vagaId
                    """)
                    .setParameter("vagaId", vagaId)
                    .getSingleResult());
        } catch (NoResultException ex) {
            throw new EntityNotFoundException("Vaga " + vagaId + " nao encontrada");
        }
    }

    private List<EstacionamentoWebDto> listarEstacionamentos(Long id,
                                                             String destino,
                                                             BigDecimal latitude,
                                                             BigDecimal longitude) {
        var destinoBusca = likePattern(destino);
        @SuppressWarnings("unchecked")
        List<Object[]> rows = em.createNativeQuery("""
                SELECT e.id,
                       e.nome,
                       NVL(op.nome_fantasia, op.razao_social),
                       op.razao_social,
                       op.cnpj,
                       op.telefone,
                       en.cep,
                       en.logradouro,
                       en.numero,
                       en.complemento,
                       b.nome,
                       c.nome,
                       uf.sigla,
                       en.latitude,
                       en.longitude,
                       e.espera_minutos,
                       e.tolerancia_minutos,
                       COUNT(v.id),
                       SUM(CASE WHEN v.id IS NOT NULL
                                  AND NVL(v.ativa, 'N') = 'Y'
                                  AND NVL(vs.status_ocupacao, 'DESCONHECIDO') = 'LIVRE'
                                  AND ar.vaga_id IS NULL
                                THEN 1 ELSE 0 END),
                       CASE
                         WHEN :latitude IS NOT NULL
                          AND :longitude IS NOT NULL
                          AND en.latitude IS NOT NULL
                          AND en.longitude IS NOT NULL
                         THEN ROUND(SQRT(
                                POWER((en.latitude - :latitude) * 111, 2) +
                                POWER((en.longitude - :longitude) * 111 * COS(:latitude * 3.141592653589793 / 180), 2)
                              ), 2)
                         ELSE NULL
                       END AS distancia_km
                  FROM estacionamento e
                  JOIN operadora op ON op.id = e.operadora_id
                  LEFT JOIN endereco en ON en.id = e.endereco_id
                  LEFT JOIN bairro b ON b.id = en.bairro_id
                  LEFT JOIN cidade c ON c.id = b.cidade_id
                  LEFT JOIN uf ON uf.sigla = c.uf_sigla
                  LEFT JOIN nivel n ON n.estacionamento_id = e.id
                  LEFT JOIN vaga v ON v.nivel_id = n.id
                  LEFT JOIN vaga_status vs ON vs.vaga_id = v.id
                  LEFT JOIN (
                    SELECT DISTINCT vaga_id
                      FROM reserva
                     WHERE estado IN ('PRE_RESERVA','RESERVA','OCUPADA')
                  ) ar ON ar.vaga_id = v.id
                 WHERE (:id IS NULL OR e.id = :id)
                   AND (:destinoBusca IS NULL OR UPPER(
                        e.nome || ' ' ||
                        NVL(en.cep, '') || ' ' ||
                        NVL(en.logradouro, '') || ' ' ||
                        NVL(b.nome, '') || ' ' ||
                        NVL(c.nome, '') || ' ' ||
                        NVL(uf.sigla, '')
                   ) LIKE :destinoBusca)
                 GROUP BY e.id, e.nome, op.nome_fantasia, op.razao_social, op.cnpj, op.telefone,
                          en.cep, en.logradouro, en.numero, en.complemento, b.nome, c.nome, uf.sigla,
                          en.latitude, en.longitude, e.espera_minutos, e.tolerancia_minutos
                 ORDER BY CASE WHEN distancia_km IS NULL THEN 1 ELSE 0 END, distancia_km, e.nome
                 FETCH FIRST 30 ROWS ONLY
                """)
                .setParameter("id", id)
                .setParameter("destinoBusca", destinoBusca)
                .setParameter("latitude", latitude)
                .setParameter("longitude", longitude)
                .getResultList();

        return rows.stream().map(this::toEstacionamento).toList();
    }

    private Referencia referencia(String destino, BigDecimal latitude, BigDecimal longitude) {
        if (latitude != null && longitude != null) {
            return new Referencia(latitude, longitude, false);
        }
        if (destino == null || destino.isBlank()) {
            return new Referencia(null, null, false);
        }

        try {
            Object[] row = (Object[]) em.createNativeQuery("""
                    SELECT en.latitude, en.longitude
                      FROM endereco en
                      LEFT JOIN bairro b ON b.id = en.bairro_id
                      LEFT JOIN cidade c ON c.id = b.cidade_id
                      LEFT JOIN uf ON uf.sigla = c.uf_sigla
                     WHERE en.latitude IS NOT NULL
                       AND en.longitude IS NOT NULL
                       AND UPPER(
                           NVL(en.cep, '') || ' ' ||
                           NVL(en.logradouro, '') || ' ' ||
                           NVL(b.nome, '') || ' ' ||
                           NVL(c.nome, '') || ' ' ||
                           NVL(uf.sigla, '')
                       ) LIKE :destinoBusca
                     FETCH FIRST 1 ROWS ONLY
                    """)
                    .setParameter("destinoBusca", likePattern(destino))
                    .getSingleResult();
            return new Referencia(toBigDecimal(row[0]), toBigDecimal(row[1]), true);
        } catch (NoResultException ex) {
            return new Referencia(null, null, false);
        }
    }

    private EstacionamentoWebDto toEstacionamento(Object[] row) {
        return new EstacionamentoWebDto(
                toLong(row[0]),
                text(row[1]),
                text(row[2]),
                text(row[3]),
                text(row[4]),
                text(row[5]),
                text(row[6]),
                text(row[7]),
                text(row[8]),
                text(row[9]),
                text(row[10]),
                text(row[11]),
                text(row[12]),
                toBigDecimal(row[13]),
                toBigDecimal(row[14]),
                toInteger(row[15]),
                toInteger(row[16]),
                toLong(row[17]),
                toLong(row[18]),
                toBigDecimal(row[19])
        );
    }

    private VagaWebDto toVaga(Object[] row) {
        var tarifa = toBigDecimal(row[6]);
        return new VagaWebDto(
                toLong(row[0]),
                text(row[1]),
                "Y".equalsIgnoreCase(text(row[2])),
                text(row[3]),
                toInteger(row[4]),
                text(row[5]),
                tarifa,
                "Y".equalsIgnoreCase(text(row[7])),
                "Y".equalsIgnoreCase(text(row[8])),
                "Y".equalsIgnoreCase(text(row[9])),
                text(row[10]),
                text(row[11]),
                toLong(row[12]),
                text(row[13]),
                reservaValorService.calcularSePossivel(tarifa, 60, 15)
        );
    }

    private static String likePattern(String value) {
        return value == null || value.isBlank() ? null : "%" + value.trim().toUpperCase() + "%";
    }

    private static Long toLong(Object value) {
        return value == null ? null : ((Number) value).longValue();
    }

    private static Integer toInteger(Object value) {
        return value == null ? null : ((Number) value).intValue();
    }

    private static BigDecimal toBigDecimal(Object value) {
        return value == null ? null : new BigDecimal(value.toString());
    }

    private static String text(Object value) {
        return value == null ? null : value.toString();
    }

    private record Referencia(BigDecimal latitude, BigDecimal longitude, boolean porEnderecoCadastrado) {}
}
