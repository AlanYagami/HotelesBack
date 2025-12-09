package utez.edu.mx.hotelback.security.jwt;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import utez.edu.mx.hotelback.modules.user.User;
import utez.edu.mx.hotelback.modules.user.UserRepository;
import java.util.Collections;

@Service
public class UDService  implements UserDetailsService {
    private final UserRepository userRepository;

    public UDService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User found = userRepository.findByUsername(username).orElse(null);
        if (found == null) throw new UsernameNotFoundException("Usuario no encontrado");

        GrantedAuthority authority= new SimpleGrantedAuthority("ROLE_" + found.getRole().getName());
        return new org.springframework.security.core.userdetails.User(
                found.getUsername(),
                found.getPassword(),
                Collections.singleton(authority)
        );
    }
}
