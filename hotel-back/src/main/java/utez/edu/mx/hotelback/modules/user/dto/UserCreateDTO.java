package utez.edu.mx.hotelback.modules.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import utez.edu.mx.hotelback.modules.rol.Role;

import java.util.UUID;

// DTO para crear usuario
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserCreateDTO {
    private String username;
    private String email;
    private String password;
    private Role role;
}