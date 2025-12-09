package utez.edu.mx.hotelback.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JWTUtils {
    @Value("${jwt.secret.key}")
    private String SECRET_KEY;

    private Key getSignKey() {
        try {
            // 1. Convertimos tu texto a bytes puros (sin Base64, para que lea el texto tal cual)
            byte[] keyBytes = SECRET_KEY.getBytes(StandardCharsets.UTF_8);

            // 2. Usamos SHA-256 para convertir tu texto corto en una firma de 256 bits válida
            MessageDigest sha = MessageDigest.getInstance("SHA-256");
            keyBytes = sha.digest(keyBytes);

            // 3. Retornamos la llave generada que AHORA SÍ cumple con la seguridad
            return Keys.hmacShaKeyFor(keyBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al generar la llave segura", e);
        }
    }
    public Claims extractAllClaims(String token){
        return Jwts.parser().
                setSigningKey(getSignKey()).
                parseClaimsJws(token).
                getBody();
    }

    public <T> T extractClaim(String token, Function<Claims,T> ClaimsResolver){
        final Claims CLAIMS = extractAllClaims(token);
        return ClaimsResolver.apply(CLAIMS);

    }

    public String extractUsername(String token){
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token){
        return extractClaim(token, Claims::getExpiration);
    }


    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Boolean validateToken(String token, UserDetails userDetails){
        final String USERNAME = extractUsername(token);
        return (USERNAME.equals(userDetails.getUsername())&& !isTokenExpired(token));
    }
    private String createToken(Map<String, Object> claims, String subject){
        return Jwts.builder()
                .setClaims(claims).setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
                .signWith(SignatureAlgorithm.HS256, getSignKey())
                .compact();
    }


    public String generateToken(UserDetails userDetails) {
        Map<String,Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername());
    }
}
