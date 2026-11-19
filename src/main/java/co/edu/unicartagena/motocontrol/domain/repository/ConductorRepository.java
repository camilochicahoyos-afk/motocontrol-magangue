package co.edu.unicartagena.motocontrol.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.unicartagena.motocontrol.domain.model.Conductor;
import co.edu.unicartagena.motocontrol.domain.model.EstadoConductor;

public interface ConductorRepository extends JpaRepository<Conductor, Long> {

    boolean existsByNumeroDocumento(String numeroDocumento);

    boolean existsByNumeroLicencia(String numeroLicencia);

    Optional<Conductor> findByNumeroDocumento(String numeroDocumento);

    List<Conductor> findByEstado(EstadoConductor estado);
}
