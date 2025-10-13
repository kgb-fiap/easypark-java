package br.com.fiap.easypark.repositories;

import br.com.fiap.easypark.entities.Estacionamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstacionamentoRepository extends JpaRepository<Estacionamento, Long> {}
