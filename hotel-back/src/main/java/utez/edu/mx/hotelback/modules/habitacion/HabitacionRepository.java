package utez.edu.mx.hotelback.modules.habitacion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface HabitacionRepository extends JpaRepository<Habitacion, UUID> {
    Optional<Habitacion> findByNumero(String numero);
    List<Habitacion> findByEstado(EstadoHabitacion estado);

    @Query("SELECT h FROM Habitacion h LEFT JOIN FETCH h.asignaciones WHERE h.id = :id")
    Optional<Habitacion> findByIdWithAsignaciones(UUID id);
}