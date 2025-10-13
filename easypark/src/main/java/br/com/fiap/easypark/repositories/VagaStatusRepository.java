
package br.com.fiap.easypark.repositories;
import br.com.fiap.easypark.entities.VagaStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VagaStatusRepository extends JpaRepository<VagaStatus, Long> {}
