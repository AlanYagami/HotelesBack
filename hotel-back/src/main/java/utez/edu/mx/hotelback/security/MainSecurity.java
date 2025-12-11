package utez.edu.mx.hotelback.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import utez.edu.mx.hotelback.security.filters.JWTFilter;

import java.util.List;

@Configuration
@EnableWebSecurity
public class MainSecurity {

    private final JWTFilter jwtFilter;

    public MainSecurity(JWTFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    String RECEPCION = "ROLE_RECEPCION";
    String CAMARERA = "ROLE_CAMARERA";

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .cors(c -> c.configurationSource(corsRegistry()))
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> {
                    // Endpoints públicos
                    auth.requestMatchers("/api/auth/**").permitAll();
                    auth.requestMatchers("/api/reportes/imagen/**").permitAll();

                    // Endpoints protegidos por rol
                    auth.requestMatchers("/api/users/**").hasAnyAuthority(RECEPCION, "ROLE_GERENTE");
                    auth.requestMatchers("/api/habitaciones/**").hasAnyAuthority(RECEPCION, CAMARERA);
                    auth.requestMatchers("/api/asignaciones/**").hasAnyAuthority(RECEPCION, CAMARERA);
                    auth.requestMatchers("/api/reportes/**").hasAnyAuthority(RECEPCION, CAMARERA);

                    // Cualquier otra petición requiere autenticación
                    auth.anyRequest().authenticated();
                })
                // Agregar el filtro JWT
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private CorsConfigurationSource corsRegistry() {
        CorsConfiguration conf = new CorsConfiguration();

        conf.setAllowedOriginPatterns(List.of("*"));
        conf.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        conf.setAllowedHeaders(List.of("*"));
        conf.setAllowCredentials(true);
        conf.setExposedHeaders(List.of("Authorization"));

        UrlBasedCorsConfigurationSource src = new UrlBasedCorsConfigurationSource();
        src.registerCorsConfiguration("/**", conf);
        return src;
    }
}