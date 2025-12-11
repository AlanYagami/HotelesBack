package utez.edu.mx.hotelback.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import utez.edu.mx.hotelback.modules.rol.Role;
import utez.edu.mx.hotelback.modules.user.User;
import utez.edu.mx.hotelback.modules.user.UserRepository;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Verificar si ya existe el usuario
            if (userRepository.findByUsername("LeyAVE").isEmpty()) {
                User recepcionista = new User();
                recepcionista.setUsername("LeyAVE");
                recepcionista.setEmail("20223tn036@utez.edu.mx");
                // Encriptar la contraseña
                recepcionista.setPassword(passwordEncoder.encode("password123"));
                recepcionista.setRole(Role.RECEPCION);

                userRepository.save(recepcionista);

            } else {
                System.out.println("El usuario ya existe en la base de datos");
            }
        };
    }
}