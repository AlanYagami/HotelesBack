package utez.edu.mx.hotelback.modules.asignacion;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import utez.edu.mx.hotelback.modules.habitacion.Habitacion;
import utez.edu.mx.hotelback.modules.user.User;

import java.util.UUID;

@Entity
@Table(name = "asignaciones")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AsignacionHabitacion {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User usuario;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Habitacion habitacion;

    private boolean activa;
}
