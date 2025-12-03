package utez.edu.mx.hotelback.modules.asignacion;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import utez.edu.mx.hotelback.modules.asignacion.dto.AsignacionCreateDTO;
import utez.edu.mx.hotelback.modules.asignacion.dto.AsignacionDTO;
import utez.edu.mx.hotelback.modules.asignacion.dto.AsignacionDesactivarDTO;
import utez.edu.mx.hotelback.modules.habitacion.Habitacion;
import utez.edu.mx.hotelback.modules.habitacion.HabitacionRepository;
import utez.edu.mx.hotelback.modules.user.User;
import utez.edu.mx.hotelback.modules.user.UserRepository;
import utez.edu.mx.hotelback.utils.APIResponse;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class AsignacionService {

    private final AsignacionHabitacionRepository asignacionRepository;
    private final UserRepository userRepository;
    private final HabitacionRepository habitacionRepository;

    public AsignacionService(AsignacionHabitacionRepository asignacionRepository,
                             UserRepository userRepository,
                             HabitacionRepository habitacionRepository) {
        this.asignacionRepository = asignacionRepository;
        this.userRepository = userRepository;
        this.habitacionRepository = habitacionRepository;
    }

    private AsignacionDTO convertEntityToDTO(AsignacionHabitacion a) {
        return new AsignacionDTO(
                a.getId(),
                a.getUsuario().getId(),
                a.getUsuario().getUsername(),
                a.getHabitacion().getId(),
                a.getHabitacion().getNumero(),
                a.getHabitacion().getEstado(),
                a.isActiva()
        );
    }

    private List<AsignacionDTO> convertEntitiesToDTO(List<AsignacionHabitacion> asignaciones) {
        List<AsignacionDTO> list = new ArrayList<>();
        for (AsignacionHabitacion a : asignaciones) {
            list.add(convertEntityToDTO(a));
        }
        return list;
    }

    @Transactional(readOnly = true)
    public ResponseEntity<APIResponse> findAll() {
        APIResponse body = new APIResponse(
                "Operación exitosa",
                convertEntitiesToDTO(asignacionRepository.findAll()),
                HttpStatus.OK
        );
        return new ResponseEntity<>(body, body.getStatus());
    }

    @Transactional(readOnly = true)
    public ResponseEntity<APIResponse> findActivas() {
        APIResponse body = new APIResponse(
                "Operación exitosa",
                convertEntitiesToDTO(asignacionRepository.findByActivaTrue()),
                HttpStatus.OK
        );
        return new ResponseEntity<>(body, body.getStatus());
    }

    @Transactional(readOnly = true)
    public ResponseEntity<APIResponse> findByUsuarioId(UUID usuarioId) {
        APIResponse body = new APIResponse(
                "Operación exitosa",
                convertEntitiesToDTO(asignacionRepository.findByUsuarioIdAndActivaTrue(usuarioId)),
                HttpStatus.OK
        );
        return new ResponseEntity<>(body, body.getStatus());
    }

    @Transactional(readOnly = true)
    public ResponseEntity<APIResponse> findByHabitacionId(UUID habitacionId) {
        APIResponse body = new APIResponse(
                "Operación exitosa",
                convertEntitiesToDTO(asignacionRepository.findByHabitacionIdAndActivaTrue(habitacionId)),
                HttpStatus.OK
        );
        return new ResponseEntity<>(body, body.getStatus());
    }

    @Transactional(rollbackFor = {SQLException.class, Exception.class})
    public ResponseEntity<APIResponse> saveAsignacion(AsignacionCreateDTO dto) {
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

            // Verificar si ya existe una asignación activa para esta habitación
            List<AsignacionHabitacion> asignacionesActivas =
                    asignacionRepository.findByHabitacionIdAndActivaTrue(dto.getHabitacionId());

            if (!asignacionesActivas.isEmpty()) {
                body = new APIResponse("La habitación ya tiene una asignación activa", true, HttpStatus.BAD_REQUEST);
                return new ResponseEntity<>(body, body.getStatus());
            }

            AsignacionHabitacion asignacion = new AsignacionHabitacion();
            asignacion.setUsuario(user);
            asignacion.setHabitacion(habitacion);
            asignacion.setActiva(true);
            asignacionRepository.saveAndFlush(asignacion);

            body = new APIResponse("Operación exitosa", convertEntityToDTO(asignacion), HttpStatus.CREATED);
        } catch (Exception ex) {
            ex.printStackTrace();
            body = new APIResponse("No se pudo crear la asignación", true, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(body, body.getStatus());
    }

    @Transactional(rollbackFor = {SQLException.class, Exception.class})
    public ResponseEntity<APIResponse> desactivarAsignacion(AsignacionDesactivarDTO dto) {
        APIResponse body;
        AsignacionHabitacion found = asignacionRepository.findById(dto.getId()).orElse(null);
        if (found != null) {
            try {
                found.setActiva(false);
                asignacionRepository.saveAndFlush(found);
                body = new APIResponse("Operación exitosa", convertEntityToDTO(found), HttpStatus.OK);
            } catch (Exception ex) {
                ex.printStackTrace();
                body = new APIResponse("No se pudo desactivar la asignación", true, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            body = new APIResponse("La asignación no existe", true, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(body, body.getStatus());
    }

    @Transactional(rollbackFor = {SQLException.class, Exception.class})
    public ResponseEntity<APIResponse> deleteAsignacion(UUID id) {
        APIResponse body;
        AsignacionHabitacion found = asignacionRepository.findById(id).orElse(null);
        if (found != null) {
            try {
                asignacionRepository.deleteById(id);
                body = new APIResponse("Operación exitosa", HttpStatus.OK);
            } catch (Exception ex) {
                ex.printStackTrace();
                body = new APIResponse("No se pudo eliminar la asignación", true, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            body = new APIResponse("La asignación no existe", true, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(body, body.getStatus());
    }
}