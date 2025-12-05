package utez.edu.mx.hotelback.security.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import utez.edu.mx.hotelback.security.jwt.JWTUtils;
import utez.edu.mx.hotelback.security.jwt.UDService;

import java.io.IOException;

@Component
public class JWTFilter extends OncePerRequestFilter {
    private final UDService udService;
    private final JWTUtils jwtUtils;

    public JWTFilter(UDService udService, JWTUtils jwtUtils) {
        this.udService = udService;
        this.jwtUtils = jwtUtils;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String AUTHORIZATION_HEADER = request.getHeader("Authorization");
        String username = null;
        String token = null;

        if (AUTHORIZATION_HEADER != null&& AUTHORIZATION_HEADER.startsWith("Bearer ")) {
            token = AUTHORIZATION_HEADER.substring(7);
            username = jwtUtils.extractUsername(token);

        }
        if (username != null && SecurityContextHolder.getContext().getAuthentication() ==null){

            UserDetails userDetails = udService.loadUserByUsername(username);
            if (jwtUtils.validateToken(token,userDetails)){
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null,userDetails.getAuthorities()
                );

                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}
