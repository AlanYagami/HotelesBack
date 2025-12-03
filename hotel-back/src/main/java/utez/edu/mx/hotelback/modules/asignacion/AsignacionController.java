package utez.edu.mx.hotelback.modules.asignacion;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import utez.edu.mx.hotelback.modules.asignacion.dto.AsignacionCreateDTO;
import utez.edu.mx.hotelback.modules.asignacion.dto.AsignacionDesactivarDTO;
import utez.edu.mx.hotelback.utils.APIResponse;

import java.util.UUID;

@RestController
@RequestMapping("/api/asignaciones")
@AllArgsConstructor
@CrossOrigin(origins = "*")
public class AsignacionController {

    private final AsignacionService asignacionService;

    @GetMapping("")
    public ResponseEntity<APIResponse> findAll() {
        return asignacionService.findAll();
    }

    @GetMapping("/activas")
    public ResponseEntity<APIResponse> findActivas() {
        return asignacionService.findActivas();
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<APIResponse> findByUsuarioId(@PathVariable("usuarioId") UUID usuarioId) {
        return asignacionService.findByUsuarioId(usuarioId);
    }

    @GetMapping("/habitacion/{habitacionId}")
    public ResponseEntity<APIResponse> findByHabitacionId(@PathVariable("habitacionId") UUID habitacionId) {
        return asignacionService.findByHabitacionId(habitacionId);
    }

    @PostMapping("")
    public ResponseEntity<APIResponse> saveAsignacion(@RequestBody AsignacionCreateDTO dto) {
        return asignacionService.saveAsignacion(dto);
    }

    @PutMapping("/desactivar")
    public ResponseEntity<APIResponse> desactivarAsignacion(@RequestBody AsignacionDesactivarDTO dto) {
        return asignacionService.desactivarAsignacion(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponse> deleteAsignacion(@PathVariable("id") UUID id) {
        return asignacionService.deleteAsignacion(id);
    }
}