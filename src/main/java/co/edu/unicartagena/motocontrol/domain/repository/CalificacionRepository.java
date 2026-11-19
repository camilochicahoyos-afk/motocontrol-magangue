package co.edu.unicartagena.motocontrol.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.unicartagena.motocontrol.domain.model.Calificacion;

public interface CalificacionRepository extends JpaRepository<Calificacion, Long> {

    boolean existsByServicioId(Long servicioId);

    List<Calificacion> findByConductorIdOrderByFechaDesc(Long conductorId);
}
