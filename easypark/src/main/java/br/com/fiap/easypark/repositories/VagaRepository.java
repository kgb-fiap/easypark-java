
package br.com.fiap.easypark.repositories;

import br.com.fiap.easypark.entities.Vaga;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VagaRepository extends JpaRepository<Vaga, Long> {

    boolean existsByNivelIdAndCodigoIgnoreCase(Long nivelId, String codigo);

    // lista vagas de um estacionamento (via relacionamento Vaga -> Nivel -> Estacionamento)
    List<Vaga> findByNivel_Estacionamento_Id(Long estacionamentoId);

    // filtra por status atual (join com VagaStatus)
    @Query("""
           select v
             from Vaga v
             join VagaStatus vs on vs.vaga = v
            where upper(vs.statusOcupacao) = upper(:status)
           """)
    List<Vaga> findByStatusIgnoreCase(@Param("status") String status);
}
