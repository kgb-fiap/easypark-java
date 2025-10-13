
package br.com.fiap.easypark.repositories;

import br.com.fiap.easypark.entities.TipoVaga;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoVagaRepository extends JpaRepository<TipoVaga, Long> {}
