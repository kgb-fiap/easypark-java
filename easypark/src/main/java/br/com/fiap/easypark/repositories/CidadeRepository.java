package br.com.fiap.easypark.repositories;

import br.com.fiap.easypark.entities.Cidade;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CidadeRepository extends JpaRepository<Cidade, Long> {}