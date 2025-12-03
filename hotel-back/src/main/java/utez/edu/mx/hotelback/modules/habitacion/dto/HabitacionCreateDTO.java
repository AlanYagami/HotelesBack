package utez.edu.mx.hotelback.modules.habitacion.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import utez.edu.mx.hotelback.modules.habitacion.EstadoHabitacion;

import java.util.UUID;

// DTO para crear habitación
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class HabitacionCreateDTO {
    private String numero;
    private EstadoHabitacion estado;
}
