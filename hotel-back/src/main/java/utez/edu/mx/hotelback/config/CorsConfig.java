package utez.edu.mx.hotelback.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // ✅ Usar allowedOriginPatterns en lugar de allowedOrigins cuando allowCredentials = true
        configuration.setAllowedOriginPatterns(Arrays.asList(
                "*",  // Permite cualquier origen
                "http://localhost:*",
                "http://127.0.0.1:*",
                "https://*.ngrok-free.app",
                "https://*.ngrok.io"
        ));

        // Métodos HTTP permitidos
        configuration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"
        ));

        // Headers permitidos
        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "Accept",
                "Origin",
                "X-Requested-With"
        ));

        // Headers expuestos al cliente
        configuration.setExposedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type"
        ));

        // ✅ Permitir credenciales (cookies, authorization headers)
        configuration.setAllowCredentials(true);

        // Tiempo de caché para preflight requests (OPTIONS)
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}