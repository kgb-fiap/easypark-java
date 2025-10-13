package br.com.fiap.easypark.services;

import jakarta.persistence.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@RequiredArgsConstructor
@Service
public class ReservaJobsService {
    @PersistenceContext private final EntityManager em;

    @Transactional
    public int runReservaTimeouts() {
        var sp = em.createStoredProcedureQuery("reserva_timeouts");
        sp.registerStoredProcedureParameter("p_out_canceladas", Integer.class, ParameterMode.OUT);
        sp.execute();
        return ((Number) sp.getOutputParameterValue("p_out_canceladas")).intValue();
    }

    @Transactional
    public int runPreReservaTimeouts() {
        var sp = em.createStoredProcedureQuery("reserva_prereserva_timeouts");
        sp.registerStoredProcedureParameter("p_out_canceladas", Integer.class, ParameterMode.OUT);
        sp.execute();
        return ((Number) sp.getOutputParameterValue("p_out_canceladas")).intValue();
    }

    @Transactional
    public Map<String,String> updateEta(long reservaId, int etaMinutos) {
        var sp = em.createStoredProcedureQuery("user_eta_update_process");
        sp.registerStoredProcedureParameter("p_reserva_id", Long.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_eta_minutos", Integer.class, ParameterMode.IN);
        sp.registerStoredProcedureParameter("p_status", String.class, ParameterMode.OUT);
        sp.registerStoredProcedureParameter("p_msg", String.class, ParameterMode.OUT);
        sp.setParameter("p_reserva_id", reservaId);
        sp.setParameter("p_eta_minutos", etaMinutos);
        sp.execute();
        return Map.of(
                "status", (String) sp.getOutputParameterValue("p_status"),
                "msg", (String) sp.getOutputParameterValue("p_msg")
        );
    }
}
