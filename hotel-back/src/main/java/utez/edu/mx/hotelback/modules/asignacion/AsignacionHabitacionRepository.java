package utez.edu.mx.hotelback.modules.asignacion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface AsignacionHabitacionRepository extends JpaRepository<AsignacionHabitacion, UUID> {

    @Query("SELECT a FROM AsignacionHabitacion a WHERE a.usuario.id = :usuarioId AND a.activa = true")
    List<AsignacionHabitacion> findByUsuarioIdAndActivaTrue(@Param("usuarioId") UUID usuarioId);

    @Query("SELECT a FROM AsignacionHabitacion a WHERE a.habitacion.id = :habitacionId AND a.activa = true")
    List<AsignacionHabitacion> findByHabitacionIdAndActivaTrue(@Param("habitacionId") UUID habitacionId);

    List<AsignacionHabitacion> findByActivaTrue();
}