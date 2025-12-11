package utez.edu.mx.hotelback.config;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import utez.edu.mx.hotelback.modules.habitacion.EstadoHabitacion;
import utez.edu.mx.hotelback.modules.habitacion.Habitacion;
import utez.edu.mx.hotelback.modules.habitacion.HabitacionRepository;

import java.util.List;

@Component
public class ScheduledTasks {

    private final HabitacionRepository habitacionRepository;

    public ScheduledTasks(HabitacionRepository habitacionRepository) {
        this.habitacionRepository = habitacionRepository;
    }

    @Scheduled(cron = "0 0 6 * * *")
    @Transactional
    public void cambiarOcupadasASucias() {

        try {
            // Buscar todas las habitaciones ocupadas
            List<Habitacion> habitacionesOcupadas = habitacionRepository.findByEstado(EstadoHabitacion.OCUPADA);

            // Cambiar su estado a SUCIA
            for (Habitacion habitacion : habitacionesOcupadas) {
                habitacion.setEstado(EstadoHabitacion.SUCIA);
                habitacionRepository.save(habitacion);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}