package utez.edu.mx.hotelback.modules.reporte;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import utez.edu.mx.hotelback.modules.habitacion.EstadoHabitacion;
import utez.edu.mx.hotelback.modules.habitacion.Habitacion;
import utez.edu.mx.hotelback.modules.habitacion.HabitacionRepository;
import utez.edu.mx.hotelback.modules.reporte.dto.ReporteCreateDTO;
import utez.edu.mx.hotelback.modules.reporte.dto.ReporteDTO;
import utez.edu.mx.hotelback.modules.user.User;
import utez.edu.mx.hotelback.modules.user.UserRepository;
import utez.edu.mx.hotelback.utils.APIResponse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
public class ReporteService {

    private final ReporteRepository reporteRepository;
    private final UserRepository userRepository;
    private final HabitacionRepository habitacionRepository;

    // Directorio donde se guardarán las imágenes
    private static final String UPLOAD_DIR = "uploads/reportes/";

    public ReporteService(ReporteRepository reporteRepository,
                          UserRepository userRepository,
                          HabitacionRepository habitacionRepository) {
        this.reporteRepository = reporteRepository;
        this.userRepository = userRepository;
        this.habitacionRepository = habitacionRepository;

        // Crear directorio si no existe
        try {
            Files.createDirectories(Paths.get(UPLOAD_DIR));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ReporteDTO convertEntityToDTO(Reporte r) {
        return new ReporteDTO(
                r.getId(),
                r.getDescripcion(),
                r.getFecha(),
                r.getImagenUrl(),
                r.getUsuario().getId(),
                r.getUsuario().getUsername(),
                r.getHabitacion().getId(),
                r.getHabitacion().getNumero()
        );
    }

    private List<ReporteDTO> convertEntitiesToDTO(List<Reporte> reportes) {
        List<ReporteDTO> list = new ArrayList<>();
        for (Reporte r : reportes) {
            list.add(convertEntityToDTO(r));
        }
        return list;
    }

    private String saveImage(String base64Image) throws IOException {
        if (base64Image == null || base64Image.isEmpty()) {
            return null;
        }

        // Remover el prefijo data:image si existe
        String base64Data = base64Image;
        if (base64Image.contains(",")) {
            base64Data = base64Image.split(",")[1];
        }

        byte[] imageBytes = Base64.getDecoder().decode(base64Data);
        String fileName = UUID.randomUUID().toString() + ".jpg";
        Path filePath = Paths.get(UPLOAD_DIR + fileName);

        Files.write(filePath, imageBytes);

        return "/api/reportes/imagen/" + fileName;
    }

    @Transactional(readOnly = true)
    public ResponseEntity<APIResponse> findAll() {
        APIResponse body = new APIResponse(
                "Operación exitosa",
                convertEntitiesToDTO(reporteRepository.findAllOrderByFechaDesc()),
                HttpStatus.OK
        );
        return new ResponseEntity<>(body, body.getStatus());
    }

    @Transactional(readOnly = true)
    public ResponseEntity<APIResponse> findById(UUID id) {
        APIResponse body;
        Reporte found = reporteRepository.findById(id).orElse(null);
        if (found != null) {
            body = new APIResponse("Operación exitosa", convertEntityToDTO(found), HttpStatus.OK);
        } else {
            body = new APIResponse("El reporte no existe", true, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(body, body.getStatus());
    }

    @Transactional(readOnly = true)
    public ResponseEntity<APIResponse> findByHabitacionId(UUID habitacionId) {
        APIResponse body = new APIResponse(
                "Operación exitosa",
                convertEntitiesToDTO(reporteRepository.findByHabitacionId(habitacionId)),
                HttpStatus.OK
        );
        return new ResponseEntity<>(body, body.getStatus());
    }

    @Transactional(readOnly = true)
    public ResponseEntity<APIResponse> findByUsuarioId(UUID usuarioId) {
        APIResponse body = new APIResponse(
                "Operación exitosa",
                convertEntitiesToDTO(reporteRepository.findByUsuarioId(usuarioId)),
                HttpStatus.OK
        );
        return new ResponseEntity<>(body, body.getStatus());
    }

    @Transactional(rollbackFor = {SQLException.class, Exception.class})
    public ResponseEntity<APIResponse> saveReporte(ReporteCreateDTO dto) {
        APIResponse body;
        try {
            User user = userRepository.findById(dto.getUsuarioId()).orElse(null);
            if (user == null) {
                body = new APIResponse("El usuario no existe", true, HttpStatus.NOT_FOUND);
                return new ResponseEntity<>(body, body.getStatus());
            }

            Habitacion habitacion = habitacionRepository.findById(dto.getHabitacionId()).orElse(null);
            if (habitacion == null) {
                body = new APIResponse("La habitación no existe", true, HttpStatus.NOT_FOUND);
                return new ResponseEntity<>(body, body.getStatus());
            }

            Reporte reporte = new Reporte();
            reporte.setDescripcion(dto.getDescripcion());
            reporte.setFecha(LocalDateTime.now());
            reporte.setUsuario(user);
            reporte.setHabitacion(habitacion);

            // Guardar imagen
            if (dto.getImagenBase64() != null && !dto.getImagenBase64().isEmpty()) {
                try {
                    String imagenUrl = saveImage(dto.getImagenBase64());
                    reporte.setImagenUrl(imagenUrl);
                    // Guardar también en base64 para modo offline
                    reporte.setImagenBase64(dto.getImagenBase64());
                } catch (IOException e) {
                    e.printStackTrace();
                    // Continuar sin imagen si falla
                }
            }

            reporteRepository.saveAndFlush(reporte);

            // Bloquear automáticamente la habitación cuando se reporta un siniestro
            habitacion.setEstado(EstadoHabitacion.BLOQUEADA);
            habitacionRepository.saveAndFlush(habitacion);

            body = new APIResponse("Operación exitosa", convertEntityToDTO(reporte), HttpStatus.CREATED);
        } catch (Exception ex) {
            ex.printStackTrace();
            body = new APIResponse("No se pudo crear el reporte", true, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(body, body.getStatus());
    }

    @Transactional(rollbackFor = {SQLException.class, Exception.class})
    public ResponseEntity<APIResponse> deleteReporte(UUID id) {
        APIResponse body;
        Reporte found = reporteRepository.findById(id).orElse(null);
        if (found != null) {
            try {
                // Eliminar imagen del servidor si existe
                if (found.getImagenUrl() != null) {
                    try {
                        String fileName = found.getImagenUrl().substring(found.getImagenUrl().lastIndexOf("/") + 1);
                        Files.deleteIfExists(Paths.get(UPLOAD_DIR + fileName));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                reporteRepository.deleteById(id);
                body = new APIResponse("Operación exitosa", HttpStatus.OK);
            } catch (Exception ex) {
                ex.printStackTrace();
                body = new APIResponse("No se pudo eliminar el reporte", true, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            body = new APIResponse("El reporte no existe", true, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(body, body.getStatus());
    }

    public byte[] getImage(String fileName) throws IOException {
        Path filePath = Paths.get(UPLOAD_DIR + fileName);
        return Files.readAllBytes(filePath);
    }
}
