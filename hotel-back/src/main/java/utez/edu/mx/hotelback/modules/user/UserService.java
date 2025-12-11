package utez.edu.mx.hotelback.modules.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import utez.edu.mx.hotelback.modules.asignacion.AsignacionHabitacionRepository;
import utez.edu.mx.hotelback.modules.user.dto.UserCreateDTO;
import utez.edu.mx.hotelback.modules.user.dto.UserDTO;
import utez.edu.mx.hotelback.modules.user.dto.UserUpdateDTO;
import utez.edu.mx.hotelback.utils.APIResponse;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final AsignacionHabitacionRepository asignacionRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       AsignacionHabitacionRepository asignacionRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.asignacionRepository = asignacionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    private UserDTO convertEntityToDTO(User u) {
        return new UserDTO(
                u.getId(),
                u.getUsername(),
                u.getEmail(),
                u.getRole()
        );
    }

    private List<UserDTO> convertEntitiesToDTO(List<User> users) {
        List<UserDTO> list = new ArrayList<>();
        for (User u : users) {
            list.add(convertEntityToDTO(u));
        }
        return list;
    }

    @Transactional(readOnly = true)
    public ResponseEntity<APIResponse> findAll() {
        APIResponse body = new APIResponse(
                "Operación exitosa",
                convertEntitiesToDTO(userRepository.findAll()),
                HttpStatus.OK
        );
        return new ResponseEntity<>(body, body.getStatus());
    }

    @Transactional(readOnly = true)
    public ResponseEntity<APIResponse> findById(UUID id) {
        APIResponse body;
        User found = userRepository.findById(id).orElse(null);
        if (found != null) {
            body = new APIResponse("Operación exitosa", convertEntityToDTO(found), HttpStatus.OK);
        } else {
            body = new APIResponse("El usuario no existe", true, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(body, body.getStatus());
    }

    @Transactional(readOnly = true)
    public ResponseEntity<APIResponse> findByUsername(String username) {
        APIResponse body;
        User found = userRepository.findByUsername(username).orElse(null);
        if (found != null) {
            body = new APIResponse("Operación exitosa", convertEntityToDTO(found), HttpStatus.OK);
        } else {
            body = new APIResponse("El usuario no existe", true, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(body, body.getStatus());
    }

    @Transactional(rollbackFor = {SQLException.class, Exception.class})
    public ResponseEntity<APIResponse> saveUser(UserCreateDTO dto) {
        APIResponse body;
        try {
            if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
                body = new APIResponse("Ya existe un usuario con ese nombre de usuario", true, HttpStatus.BAD_REQUEST);
                return new ResponseEntity<>(body, body.getStatus());
            }

            if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
                body = new APIResponse("Ya existe un usuario con ese email", true, HttpStatus.BAD_REQUEST);
                return new ResponseEntity<>(body, body.getStatus());
            }

            User u = new User();
            u.setUsername(dto.getUsername());
            u.setEmail(dto.getEmail());
            u.setPassword(passwordEncoder.encode(dto.getPassword())); // Aquí deberías encriptar la contraseña
            u.setRole(dto.getRole());
            userRepository.saveAndFlush(u);

            body = new APIResponse("Operación exitosa", convertEntityToDTO(u), HttpStatus.CREATED);
        } catch (Exception ex) {
            ex.printStackTrace();
            body = new APIResponse("No se pudo registrar al usuario", true, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(body, body.getStatus());
    }

    @Transactional(rollbackFor = {SQLException.class, Exception.class})
    public ResponseEntity<APIResponse> updateUser(UserUpdateDTO dto) {
        APIResponse body;
        User found = userRepository.findById(dto.getId()).orElse(null);
        if (found != null) {
            try {
                if (dto.getUsername() != null) {
                    if (userRepository.findByUsername(dto.getUsername()).isPresent() &&
                            !found.getUsername().equals(dto.getUsername())) {
                        body = new APIResponse("Ya existe un usuario con ese nombre de usuario", true, HttpStatus.BAD_REQUEST);
                        return new ResponseEntity<>(body, body.getStatus());
                    }
                    found.setUsername(dto.getUsername());
                }

                if (dto.getEmail() != null) {
                    if (userRepository.findByEmail(dto.getEmail()).isPresent() &&
                            !found.getEmail().equals(dto.getEmail())) {
                        body = new APIResponse("Ya existe un usuario con ese email", true, HttpStatus.BAD_REQUEST);
                        return new ResponseEntity<>(body, body.getStatus());
                    }
                    found.setEmail(dto.getEmail());
                }

                userRepository.saveAndFlush(found);
                body = new APIResponse("Operación exitosa", convertEntityToDTO(found), HttpStatus.OK);
            } catch (Exception ex) {
                ex.printStackTrace();
                body = new APIResponse("No se pudo actualizar al usuario", true, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            body = new APIResponse("El usuario no existe", true, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(body, body.getStatus());
    }

    @Transactional(rollbackFor = {SQLException.class, Exception.class})
    public ResponseEntity<APIResponse> deleteUser(UUID id) {
        APIResponse body;
        User found = userRepository.findById(id).orElse(null);
        if (found != null) {
            try {
                // PRIMERO: Eliminar todas las asignaciones activas de este usuario
                asignacionRepository.findByUsuarioIdAndActivaTrue(id).forEach(asignacion -> {
                    asignacionRepository.delete(asignacion);
                });

                // También eliminar las inactivas si las hay
                asignacionRepository.deleteAll(
                        asignacionRepository.findAll().stream()
                                .filter(a -> a.getUsuario().getId().equals(id))
                                .toList()
                );

                // SEGUNDO: Ahora sí eliminar el usuario
                userRepository.deleteById(id);

                body = new APIResponse("Operación exitosa", HttpStatus.OK);
            } catch (Exception ex) {
                ex.printStackTrace();
                body = new APIResponse("No se pudo eliminar al usuario: " + ex.getMessage(), true, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            body = new APIResponse("El usuario no existe", true, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(body, body.getStatus());
    }
}