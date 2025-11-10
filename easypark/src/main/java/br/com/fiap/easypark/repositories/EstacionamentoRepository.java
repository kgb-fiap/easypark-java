package br.com.fiap.easypark.repositories;

import br.com.fiap.easypark.entities.Estacionamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import br.com.fiap.easypark.entities.Estacionamento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;

public interface EstacionamentoRepository extends JpaRepository<Estacionamento, Long> {

    @Override
    @EntityGraph(attributePaths = { "operadora", "endereco" })
    Page<Estacionamento> findAll(Pageable pageable);
}