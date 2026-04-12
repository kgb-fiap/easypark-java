package br.com.fiap.easypark.services;

import br.com.fiap.easypark.exceptions.EntityNotFoundException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class ReservaValorService {
    private static final int DURACAO_MAXIMA_MINUTOS = 1440;

    private final EntityManager em;

    public ReservaValorService(EntityManager em) {
        this.em = em;
    }

    @Transactional(readOnly = true)
    public BigDecimal calcularPorVaga(Long vagaId, Integer duracaoMinutos, Integer antecedenciaMinutos) {
        if (vagaId == null) {
            throw new IllegalArgumentException("Informe a vaga para calcular o valor previsto.");
        }

        try {
            var tarifa = (Number) em.createNativeQuery("""
                    SELECT tv.tarifa_por_minuto
                      FROM vaga v
                      JOIN tipo_vaga tv ON tv.id = v.tipo_vaga_id
                     WHERE v.id = :vagaId
                    """)
                    .setParameter("vagaId", vagaId)
                    .getSingleResult();
            return calcular(toBigDecimal(tarifa), duracaoMinutos, antecedenciaMinutos);
        } catch (NoResultException ex) {
            throw new EntityNotFoundException("Vaga " + vagaId + " nao encontrada para calculo do valor previsto.");
        }
    }

    public BigDecimal calcular(BigDecimal tarifaPorMinuto, Integer duracaoMinutos, Integer antecedenciaMinutos) {
        if (tarifaPorMinuto == null || duracaoMinutos == null || antecedenciaMinutos == null) {
            throw new IllegalArgumentException("Informe tarifa, duracao e antecedencia para calcular o valor previsto.");
        }
        if (duracaoMinutos <= 0) {
            throw new IllegalArgumentException("A duracao deve ser maior que zero.");
        }
        if (duracaoMinutos > DURACAO_MAXIMA_MINUTOS) {
            throw new IllegalArgumentException("A duracao maxima para calculo e de 1440 minutos.");
        }
        if (antecedenciaMinutos < 0) {
            throw new IllegalArgumentException("A antecedencia nao pode ser negativa.");
        }

        var fatorAntecedencia = BigDecimal.ONE.add(BigDecimal.valueOf(antecedenciaMinutos).movePointLeft(2));
        return tarifaPorMinuto
                .multiply(BigDecimal.valueOf(duracaoMinutos))
                .multiply(fatorAntecedencia)
                .setScale(4, RoundingMode.HALF_UP);
    }

    public BigDecimal calcularSePossivel(BigDecimal tarifaPorMinuto, Integer duracaoMinutos, Integer antecedenciaMinutos) {
        try {
            return calcular(tarifaPorMinuto, duracaoMinutos, antecedenciaMinutos);
        } catch (RuntimeException ex) {
            return null;
        }
    }

    private static BigDecimal toBigDecimal(Number value) {
        return value == null ? null : new BigDecimal(value.toString());
    }
}
