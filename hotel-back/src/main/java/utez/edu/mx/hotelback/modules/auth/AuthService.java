package utez.edu.mx.hotelback.modules.auth;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import utez.edu.mx.hotelback.modules.auth.dto.LoginRequestDTO;
import utez.edu.mx.hotelback.modules.user.User;
import utez.edu.mx.hotelback.modules.user.UserRepository;
import utez.edu.mx.hotelback.security.jwt.JWTUtils;
import utez.edu.mx.hotelback.security.jwt.UDService;
import utez.edu.mx.hotelback.utils.APIResponse;
import utez.edu.mx.hotelback.utils.PasswordEncoder;

import java.sql.SQLException;

@Service
public class AuthService {
private final UserRepository userRepository;
private final UDService udService;
private final JWTUtils jwtUtils;

    public AuthService(UserRepository userRepository, UDService udService, JWTUtils jwtUtils) {
        this.userRepository = userRepository;
        this.udService = udService;
        this.jwtUtils = jwtUtils;
    }
    @Transactional(readOnly = true)
    public APIResponse doLogin(LoginRequestDTO payload){
        try{
            User found = userRepository.findByUsername(payload.getUsername()).orElse(null);
            if (found== null) return  new APIResponse("Usuario no encontrado", true, HttpStatus.NOT_FOUND);


            if(!PasswordEncoder.verifyPassword(payload.getPassword(),found.getPassword()))
                return new APIResponse("Las contraseñas no coinciden", true, HttpStatus.BAD_REQUEST);

            UserDetails ud = udService.loadUserByUsername(found.getUsername());
            String token = jwtUtils.generateToken(ud);
            return new APIResponse("Operación exitosa",token,false,HttpStatus.OK);
        }catch (Exception ex){
            ex.printStackTrace();
            return new APIResponse("Eror al iniciar sesión",
                    true,
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
