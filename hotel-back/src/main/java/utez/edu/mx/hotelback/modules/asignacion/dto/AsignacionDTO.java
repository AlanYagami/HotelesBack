package utez.edu.mx.hotelback.modules.asignacion.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import utez.edu.mx.hotelback.modules.habitacion.EstadoHabitacion;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AsignacionDTO {
    private UUID id;
    private UUID usuarioId;
    private String usuarioNombre;
    private UUID habitacionId;
    private String habitacionNumero;
    private EstadoHabitacion habitacionEstado;
    private boolean activa;
}