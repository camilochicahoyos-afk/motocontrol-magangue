package co.edu.unicartagena.motocontrol.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.unicartagena.motocontrol.domain.model.Motocicleta;

public interface MotocicletaRepository extends JpaRepository<Motocicleta, Long> {

    boolean existsByPlaca(String placa);

    Optional<Motocicleta> findByPlaca(String placa);

    List<Motocicleta> findByConductorId(Long conductorId);

    long countByConductorIdAndActivaTrue(Long conductorId);
}
