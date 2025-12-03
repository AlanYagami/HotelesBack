package utez.edu.mx.hotelback.modules.reporte.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

// DTO de respuesta
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ReporteDTO {
    private UUID id;
    private String descripcion;
    private LocalDateTime fecha;
    private String imagenUrl;
    private UUID usuarioId;
    private String usuarioNombre;
    private UUID habitacionId;
    private String habitacionNumero;
}