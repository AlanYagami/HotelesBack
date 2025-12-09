package utez.edu.mx.hotelback.modules.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LoginResponseDTO {
    private String token;
    private UUID uid;
    private String username;
    private String role;
}
