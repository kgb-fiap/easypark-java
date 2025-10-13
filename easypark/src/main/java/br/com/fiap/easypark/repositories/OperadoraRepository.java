
package br.com.fiap.easypark.repositories;

import br.com.fiap.easypark.entities.Operadora;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OperadoraRepository extends JpaRepository<Operadora, Long> {}
