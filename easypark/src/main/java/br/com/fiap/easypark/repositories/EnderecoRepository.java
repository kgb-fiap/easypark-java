package br.com.fiap.easypark.repositories;

import br.com.fiap.easypark.entities.Endereco;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnderecoRepository extends JpaRepository<Endereco, Long> {}