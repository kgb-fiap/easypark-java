package br.com.fiap.easypark.repositories;

import br.com.fiap.easypark.entities.Bairro;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BairroRepository extends JpaRepository<Bairro, Long> {}