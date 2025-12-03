package utez.edu.mx.hotelback.modules.reporte;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface ReporteRepository extends JpaRepository<Reporte, UUID> {

    List<Reporte> findByHabitacionId(UUID habitacionId);

    List<Reporte> findByUsuarioId(UUID usuarioId);

    @Query("SELECT r FROM Reporte r ORDER BY r.fecha DESC")
    List<Reporte> findAllOrderByFechaDesc();
}