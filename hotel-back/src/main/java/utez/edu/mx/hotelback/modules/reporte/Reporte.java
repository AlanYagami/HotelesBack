package utez.edu.mx.hotelback.modules.reporte;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import utez.edu.mx.hotelback.modules.habitacion.Habitacion;
import utez.edu.mx.hotelback.modules.user.User;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "reportes")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Reporte {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 1000)
    private String descripcion;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @Column(length = 500)
    private String imagenUrl; // URL o path de la imagen

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String imagenBase64; // Imagen en base64 para modo offline

    @ManyToOne
    @JoinColumn(nullable = false)
    private User usuario;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Habitacion habitacion;
}