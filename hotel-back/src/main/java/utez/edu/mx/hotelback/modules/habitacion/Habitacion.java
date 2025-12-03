package utez.edu.mx.hotelback.modules.habitacion;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import utez.edu.mx.hotelback.modules.asignacion.AsignacionHabitacion;
import utez.edu.mx.hotelback.modules.reporte.Reporte;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "habitaciones")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Habitacion {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String numero;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoHabitacion estado;

    @OneToMany(mappedBy = "habitacion")
    private List<AsignacionHabitacion> asignaciones;

    @OneToMany(mappedBy = "habitacion")
    private List<Reporte> reportes;
}

