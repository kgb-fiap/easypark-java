
package br.com.fiap.easypark.repositories;

import br.com.fiap.easypark.entities.Vaga;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import br.com.fiap.easypark.entities.enums.StatusVaga;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Repository
public interface VagaRepository extends JpaRepository<Vaga, Long> {

    boolean existsByNivelIdAndCodigoIgnoreCase(Long nivelId, String codigo);

    @EntityGraph(attributePaths = { "nivel", "nivel.estacionamento", "tipoVaga" })
    @Query("""
      select v from Vaga v
      left join VagaStatus vs on vs.vagaId = v.id
      where (:status is null or vs.statusOcupacao = :status)
        and (:nivelId is null or v.nivel.id = :nivelId)
        and (:tipoVagaId is null or v.tipoVaga.id = :tipoVagaId)
        and (:estacionamentoId is null or v.nivel.estacionamento.id = :estacionamentoId)
    """)
    Page<Vaga> search(@Param("status") StatusVaga status,
                      @Param("nivelId") Long nivelId,
                      @Param("tipoVagaId") Long tipoVagaId,
                      @Param("estacionamentoId") Long estacionamentoId,
                      Pageable pageable);

    @Override
    @EntityGraph(attributePaths = { "nivel", "nivel.estacionamento", "tipoVaga" })
    Page<Vaga> findAll(Pageable pageable);

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
