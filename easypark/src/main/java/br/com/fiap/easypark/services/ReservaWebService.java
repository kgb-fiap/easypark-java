package br.com.fiap.easypark.services;

import br.com.fiap.easypark.dto.EtaUpdateOutDto;
import br.com.fiap.easypark.dto.web.PreReservaCreateResult;
import br.com.fiap.easypark.dto.web.ReservaCreateForm;
import br.com.fiap.easypark.dto.web.ReservaWebDto;
import br.com.fiap.easypark.dto.web.SensorEventoForm;
import br.com.fiap.easypark.dto.web.SensorEventoWebDto;
import br.com.fiap.easypark.dto.web.SensorWebDto;
import br.com.fiap.easypark.repositories.UsuarioRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
public class ReservaWebService {
    private static final String DATE_FORMAT = "YYYY-MM-DD HH24:MI TZH:TZM";

    private final EntityManager em;
    private final UsuarioRepository usuarioRepository;
    private final ReservaJobsService reservaJobsService;
    private final ReservaValorService reservaValorService;

    public ReservaWebService(EntityManager em,
                             UsuarioRepository usuarioRepository,
                             ReservaJobsService reservaJobsService,
                             ReservaValorService reservaValorService) {
        this.em = em;
        this.usuarioRepository = usuarioRepository;
        this.reservaJobsService = reservaJobsService;
        this.reservaValorService = reservaValorService;
    }

    @Transactional
    public PreReservaCreateResult criarPreReserva(String email, Long vagaId, ReservaCreateForm form) {
        var usuario = usuarioRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuario autenticado nao encontrado."));
        var valorPrevisto = reservaValorService.calcularPorVaga(
                vagaId,
                form.getDuracaoMinutos(),
                form.getAntecedenciaMinutos()
        );

        var sp = em.createStoredProcedureQuery("reserva_ins");
        sp.registerStoredProcedureParameter("p_usuario_id", Long.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_vaga_id", Long.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_inicio_previsto", OffsetDateTime.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_duracao_minutos", Integer.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_antecedencia_minutos", Integer.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_id", Long.class, ParameterMode.OUT);
        sp.setParameter("p_usuario_id", usuario.getId());
        sp.setParameter("p_vaga_id", vagaId);
        sp.setParameter("p_inicio_previsto", toOffsetDateTime(form.getInicioPrevisto()));
        sp.setParameter("p_duracao_minutos", form.getDuracaoMinutos());
        sp.setParameter("p_antecedencia_minutos", form.getAntecedenciaMinutos());
        executeProcedure(sp);

        return new PreReservaCreateResult(toLong(sp.getOutputParameterValue("p_id")), valorPrevisto);
    }

    @Transactional(readOnly = true)
    public List<ReservaWebDto> listarReservasDoUsuario(String email) {
        return reservaRows("""
                SELECT r.id,
                       r.usuario_id,
                       u.email,
                       r.vaga_id,
                       v.codigo,
                       e.id,
                       e.nome,
                       n.nome,
                       tv.nome,
                       tv.tarifa_por_minuto,
                       r.estado,
                       TO_CHAR(r.criado_em, :dateFormat),
                       TO_CHAR(r.inicio_previsto, :dateFormat),
                       r.duracao_minutos,
                       r.antecedencia_minutos,
                       r.eta_minutos,
                       TO_CHAR(r.confirmado_em, :dateFormat),
                       TO_CHAR(r.ocupado_em, :dateFormat),
                       CASE r.vaga_bloqueada WHEN 'Y' THEN 'Sim' ELSE 'Nao' END,
                       r.motivo_cancelamento
                  FROM reserva r
                  JOIN usuario u ON u.id = r.usuario_id
                  JOIN vaga v ON v.id = r.vaga_id
                  JOIN nivel n ON n.id = v.nivel_id
                  JOIN estacionamento e ON e.id = n.estacionamento_id
                  JOIN tipo_vaga tv ON tv.id = v.tipo_vaga_id
                 WHERE UPPER(u.email) = UPPER(:email)
                 ORDER BY r.criado_em DESC
                 FETCH FIRST 20 ROWS ONLY
                """, email);
    }

    @Transactional(readOnly = true)
    public List<ReservaWebDto> listarReservasRecentes() {
        return reservaRows("""
                SELECT r.id,
                       r.usuario_id,
                       u.email,
                       r.vaga_id,
                       v.codigo,
                       e.id,
                       e.nome,
                       n.nome,
                       tv.nome,
                       tv.tarifa_por_minuto,
                       r.estado,
                       TO_CHAR(r.criado_em, :dateFormat),
                       TO_CHAR(r.inicio_previsto, :dateFormat),
                       r.duracao_minutos,
                       r.antecedencia_minutos,
                       r.eta_minutos,
                       TO_CHAR(r.confirmado_em, :dateFormat),
                       TO_CHAR(r.ocupado_em, :dateFormat),
                       CASE r.vaga_bloqueada WHEN 'Y' THEN 'Sim' ELSE 'Nao' END,
                       r.motivo_cancelamento
                  FROM reserva r
                  JOIN usuario u ON u.id = r.usuario_id
                  JOIN vaga v ON v.id = r.vaga_id
                  JOIN nivel n ON n.id = v.nivel_id
                  JOIN estacionamento e ON e.id = n.estacionamento_id
                  JOIN tipo_vaga tv ON tv.id = v.tipo_vaga_id
                 ORDER BY r.criado_em DESC
                 FETCH FIRST 30 ROWS ONLY
                """, null);
    }

    @Transactional
    public EtaUpdateOutDto atualizarEtaDoUsuario(String email, Long reservaId, Integer minutos) {
        if (!reservaPertenceAoUsuario(email, reservaId)) {
            throw new IllegalArgumentException("Reserva nao encontrada para o usuario autenticado.");
        }
        return reservaJobsService.updateEta(reservaId, minutos);
    }

    @Transactional
    public Long registrarEventoSensor(SensorEventoForm form) {
        var ocorridoEm = form.getOcorridoEm() == null ? LocalDateTime.now() : form.getOcorridoEm();
        var vagaId = buscarVagaIdPorSensor(form.getSensorId());
        var sp = em.createStoredProcedureQuery("sensor_evento_ins");
        sp.registerStoredProcedureParameter("p_sensor_id", Long.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_vaga_id", Long.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_ocorrido", OffsetDateTime.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_status", String.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_payload", String.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_id", Long.class, ParameterMode.OUT);
        sp.setParameter("p_sensor_id", form.getSensorId());
        sp.setParameter("p_vaga_id", vagaId);
        sp.setParameter("p_ocorrido", toOffsetDateTime(ocorridoEm));
        sp.setParameter("p_status", form.getStatus());
        sp.setParameter("p_payload", form.getPayload());
        executeProcedure(sp);

        return toLong(sp.getOutputParameterValue("p_id"));
    }

    private Long buscarVagaIdPorSensor(Long sensorId) {
        if (sensorId == null) {
            throw new IllegalArgumentException("Informe o sensor.");
        }

        var rows = em.createNativeQuery("""
                SELECT vaga_id
                  FROM sensor
                 WHERE id = :sensorId
                   AND ativo = 'Y'
                """)
                .setParameter("sensorId", sensorId)
                .getResultList();

        if (rows.isEmpty()) {
            throw new IllegalArgumentException("Sensor ativo nao encontrado para registrar o evento.");
        }

        return toLong(rows.getFirst());
    }

    private void executeProcedure(jakarta.persistence.StoredProcedureQuery sp) {
        try {
            sp.execute();
        } catch (RuntimeException ex) {
            throw new IllegalArgumentException(databaseMessage(ex), ex);
        }
    }

    @Transactional(readOnly = true)
    public List<SensorWebDto> listarSensoresAtivos() {
        @SuppressWarnings("unchecked")
        List<Object[]> rows = em.createNativeQuery("""
                SELECT s.id,
                       s.vaga_id,
                       v.codigo,
                       s.modelo,
                       s.identificador_externo,
                       s.ativo
                  FROM sensor s
                  JOIN vaga v ON v.id = s.vaga_id
                 WHERE s.ativo = 'Y'
                 ORDER BY s.id
                 FETCH FIRST 50 ROWS ONLY
                """).getResultList();

        return rows.stream()
                .map(row -> new SensorWebDto(
                        toLong(row[0]),
                        toLong(row[1]),
                        text(row[2]),
                        text(row[3]),
                        text(row[4]),
                        text(row[5])
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SensorEventoWebDto> listarEventosRecentes() {
        @SuppressWarnings("unchecked")
        List<Object[]> rows = em.createNativeQuery("""
                SELECT COALESCE(s.identificador_externo, s.modelo, 'Sensor vinculado'),
                       v.codigo,
                       se.status,
                       TO_CHAR(se.ocorrido_em, :dateFormat),
                       TO_CHAR(se.recebido_em, :dateFormat),
                       SUBSTR(se.payload, 1, 160)
                  FROM sensor_evento se
                  LEFT JOIN sensor s ON s.id = se.sensor_id
                  LEFT JOIN vaga v ON v.id = se.vaga_id
                 ORDER BY se.recebido_em DESC
                 FETCH FIRST 15 ROWS ONLY
                """)
                .setParameter("dateFormat", DATE_FORMAT)
                .getResultList();

        return rows.stream()
                .map(row -> new SensorEventoWebDto(
                        text(row[0]),
                        text(row[1]),
                        text(row[2]),
                        text(row[3]),
                        text(row[4]),
                        text(row[5])
                ))
                .toList();
    }

    private List<ReservaWebDto> reservaRows(String sql, String email) {
        var query = em.createNativeQuery(sql).setParameter("dateFormat", DATE_FORMAT);
        if (email != null) {
            query.setParameter("email", email);
        }

        @SuppressWarnings("unchecked")
        List<Object[]> rows = query.getResultList();

        return rows.stream()
                .map(this::toReservaWebDto)
                .toList();
    }

    private ReservaWebDto toReservaWebDto(Object[] row) {
        var tarifaPorMinuto = toBigDecimal(row[9]);
        var duracaoMinutos = toInteger(row[13]);
        var antecedenciaMinutos = toInteger(row[14]);
        return new ReservaWebDto(
                toLong(row[0]),
                toLong(row[1]),
                text(row[2]),
                toLong(row[3]),
                text(row[4]),
                toLong(row[5]),
                text(row[6]),
                text(row[7]),
                text(row[8]),
                tarifaPorMinuto,
                reservaValorService.calcularSePossivel(tarifaPorMinuto, duracaoMinutos, antecedenciaMinutos),
                text(row[10]),
                text(row[11]),
                text(row[12]),
                duracaoMinutos,
                antecedenciaMinutos,
                toInteger(row[15]),
                text(row[16]),
                text(row[17]),
                text(row[18]),
                text(row[19])
        );
    }

    private boolean reservaPertenceAoUsuario(String email, Long reservaId) {
        var total = (Number) em.createNativeQuery("""
                SELECT COUNT(*)
                  FROM reserva r
                  JOIN usuario u ON u.id = r.usuario_id
                 WHERE r.id = :reservaId
                   AND UPPER(u.email) = UPPER(:email)
                """)
                .setParameter("reservaId", reservaId)
                .setParameter("email", email)
                .getSingleResult();
        return total.intValue() > 0;
    }

    private OffsetDateTime toOffsetDateTime(LocalDateTime value) {
        return value.atZone(ZoneId.systemDefault()).toOffsetDateTime();
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

    private static String databaseMessage(Throwable error) {
        var current = error;
        while (current != null) {
            var message = current.getMessage();
            if (message != null) {
                var oraIndex = message.indexOf("ORA-");
                if (oraIndex >= 0) {
                    var lineEnd = message.indexOf('\n', oraIndex);
                    var firstLine = lineEnd >= 0 ? message.substring(oraIndex, lineEnd) : message.substring(oraIndex);
                    return friendlyDatabaseMessage(firstLine.replaceAll("\\s+", " ").trim());
                }
            }
            current = current.getCause();
        }
        return "Nao foi possivel concluir a operacao no banco de dados.";
    }

    private static String friendlyDatabaseMessage(String message) {
        if (message.contains("UQ_VAGA_RESERVA_ATIVA")) {
            return "Esta vaga ja possui uma reserva ativa. Escolha outra vaga ou execute os timeouts antes de tentar novamente.";
        }
        if (message.contains("UQ_USUARIO_PRERESERVA")) {
            return "Este usuario ja possui uma pre-reserva ativa. Atualize ou finalize a reserva atual antes de criar outra.";
        }
        return message;
    }
}
