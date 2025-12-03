package utez.edu.mx.hotelback.modules.habitacion;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import utez.edu.mx.hotelback.modules.habitacion.dto.HabitacionCreateDTO;
import utez.edu.mx.hotelback.modules.habitacion.dto.HabitacionUpdateEstadoDTO;
import utez.edu.mx.hotelback.utils.APIResponse;

import java.util.UUID;

@RestController
@RequestMapping("/api/habitaciones")
@AllArgsConstructor
@CrossOrigin(origins = "*")
public class HabitacionController {

    private final HabitacionService habitacionService;

    @GetMapping("")
    public ResponseEntity<APIResponse> findAll() {
        return habitacionService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<APIResponse> findById(@PathVariable("id") UUID id) {
        return habitacionService.findById(id);
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<APIResponse> findByEstado(@PathVariable("estado") EstadoHabitacion estado) {
        return habitacionService.findByEstado(estado);
    }

    @PostMapping("")
    public ResponseEntity<APIResponse> saveHabitacion(@RequestBody HabitacionCreateDTO dto) {
        return habitacionService.saveHabitacion(dto);
    }

    @PutMapping("/estado")
    public ResponseEntity<APIResponse> updateEstado(@RequestBody HabitacionUpdateEstadoDTO dto) {
        return habitacionService.updateEstado(dto);
    }

    @PutMapping("/marcar-limpia/{id}")
    public ResponseEntity<APIResponse> marcarLimpia(@PathVariable("id") UUID id) {
        return habitacionService.marcarLimpia(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponse> deleteHabitacion(@PathVariable("id") UUID id) {
        return habitacionService.deleteHabitacion(id);
    }
}