package br.com.fiap.easypark.repositories;

import br.com.fiap.easypark.entities.Uf;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UfRepository extends JpaRepository<Uf, String> {}