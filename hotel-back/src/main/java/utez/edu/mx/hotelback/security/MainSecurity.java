package utez.edu.mx.hotelback.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class MainSecurity {
    String RECEPCION ="ROLE_RECEPCION";
    String CAMARERA="ROLE_CAMARERA";

    private final  String[] RECEPCION_LIST={};
    private final String[] CAMARERA_LIST={};
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .cors(c->c.configurationSource(corsRegistry()))
                .authorizeHttpRequests(auth -> {
                    auth
                            .requestMatchers("/api/auth/**").permitAll();
                    for (String url : RECEPCION_LIST) {
                        auth.requestMatchers(url).hasAuthority(RECEPCION);
                    }
                    for (String url : CAMARERA_LIST) {
                        auth.requestMatchers(url).hasAuthority(CAMARERA);
                    }
                      auth .anyRequest().authenticated();
                });

        return http.build();
    }

    private CorsConfigurationSource corsRegistry() {
        CorsConfiguration conf =  new CorsConfiguration();
        conf.setAllowedOrigins(List.of("*"));
        conf.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        conf.setAllowedHeaders(List.of("Accept", "Content-Type", "Authorization"));
        conf.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource src = new UrlBasedCorsConfigurationSource();
        src.registerCorsConfiguration("/**", conf);

        return src;
    }
}
