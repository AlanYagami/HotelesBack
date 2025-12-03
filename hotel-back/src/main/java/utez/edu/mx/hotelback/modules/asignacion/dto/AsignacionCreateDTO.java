package utez.edu.mx.hotelback.modules.asignacion.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

// DTO para crear asignación
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AsignacionCreateDTO {
    private UUID usuarioId;
    private UUID habitacionId;
}