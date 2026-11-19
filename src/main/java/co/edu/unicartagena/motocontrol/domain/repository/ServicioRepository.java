package co.edu.unicartagena.motocontrol.domain.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.unicartagena.motocontrol.domain.model.EstadoServicio;
import co.edu.unicartagena.motocontrol.domain.model.Servicio;

public interface ServicioRepository extends JpaRepository<Servicio, Long> {

    boolean existsByConductorIdAndEstado(Long conductorId, EstadoServicio estado);

    List<Servicio> findByConductorId(Long conductorId);

    List<Servicio> findByFechaHoraInicioBetween(LocalDateTime desde, LocalDateTime hasta);
}
