package utez.edu.mx.hotelback.modules.reporte;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import utez.edu.mx.hotelback.modules.reporte.dto.ReporteCreateDTO;
import utez.edu.mx.hotelback.utils.APIResponse;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/reportes")
@AllArgsConstructor
@CrossOrigin(origins = "*")
public class ReporteController {

    private final ReporteService reporteService;

    @GetMapping("")
    public ResponseEntity<APIResponse> findAll() {
        return reporteService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<APIResponse> findById(@PathVariable("id") UUID id) {
        return reporteService.findById(id);
    }

    @GetMapping("/habitacion/{habitacionId}")
    public ResponseEntity<APIResponse> findByHabitacionId(@PathVariable("habitacionId") UUID habitacionId) {
        return reporteService.findByHabitacionId(habitacionId);
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<APIResponse> findByUsuarioId(@PathVariable("usuarioId") UUID usuarioId) {
        return reporteService.findByUsuarioId(usuarioId);
    }

    @PostMapping("")
    public ResponseEntity<APIResponse> saveReporte(@RequestBody ReporteCreateDTO dto) {
        return reporteService.saveReporte(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponse> deleteReporte(@PathVariable("id") UUID id) {
        return reporteService.deleteReporte(id);
    }

    @GetMapping(value = "/imagen/{fileName}", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getImage(@PathVariable("fileName") String fileName) {
        try {
            byte[] image = reporteService.getImage(fileName);
            return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(image);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}