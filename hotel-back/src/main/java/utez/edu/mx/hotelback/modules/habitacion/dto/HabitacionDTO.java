package utez.edu.mx.hotelback.modules.habitacion.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import utez.edu.mx.hotelback.modules.habitacion.EstadoHabitacion;

import java.util.UUID;

// DTO de respuesta
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class HabitacionDTO {
    private UUID id;
    private String numero;
    private EstadoHabitacion estado;
    private byte[] qr;
    private boolean tieneAsignacion;
}