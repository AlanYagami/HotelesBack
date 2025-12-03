package utez.edu.mx.hotelback.modules.reporte.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

// DTO para crear reporte
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ReporteCreateDTO {
    private String descripcion;
    private String imagenBase64; // Imagen en base64
    private UUID usuarioId;
    private UUID habitacionId;
}