// JWT = JSON Web Token — estándar para transmitir información segura entre sistemas
package com.example.AuthService.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {
    private static final Logger log = LoggerFactory.getLogger(JwtUtil.class);

    // Clave secreta para firmar y verificar tokens
    // Viene de application.properties: jwt.secret=smcft_clave_...
    @Value("${jwt.secret}")
    private String secret;

    // Tiempo de vida del token en milisegundos
    // Viene de application.properties: jwt.expiration=86400000 (24 horas)
    @Value("${jwt.expiration}")
    private Long expiration;

    // Convierte el secreto en texto a una clave criptográfica real
    // HMAC-SHA256 necesita una clave de al menos 256 bits
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    // Genera un nuevo token JWT con los datos del usuario
    // Se llama después de verificar que las credenciales son correctas
    public String generarToken(String email, String rol) {
        log.info("Generando token JWT para: {}", email);
        return Jwts.builder()

                // setSubject define el identificador del usuario en el token
                .setSubject(email)

                // claim agrega el rol dentro del token
                // Otros microservicios leen el rol sin consultar la BD
                .claim("rol", rol)

                // Fecha de creación del token
                .setIssuedAt(new Date())

                // Fecha de expiración: ahora + 24 horas
                .setExpiration(
                        new Date(System.currentTimeMillis() + expiration))

                // Firma el token con el secreto usando algoritmo HS256
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)

                // Construye y retorna el string final del token
                .compact();
    }

    // Extrae el email del token (guardado como "subject")
    public String obtenerEmail(String token) {
        return obtenerClaims(token).getSubject();
    }

    // Extrae el rol del token
    // Permite conocer permisos del usuario sin consultar la BD
    public String obtenerRol(String token) {
        return obtenerClaims(token).get("rol", String.class);
    }

    // Verifica si el token tiene firma válida y no ha expirado
    // NO verifica la blacklist — eso lo hace AuthService
    public boolean esValido(String token) {
        try {
            Claims claims = obtenerClaims(token);
            boolean noExpirado = claims.getExpiration().after(new Date());
            log.info("Token verificado para: {}", claims.getSubject());
            return noExpirado;
        } catch (Exception e) {
            // Si la firma no coincide o el token está malformado
            log.warn("Token con firma inválida: {}", e.getMessage());
            return false;
        }
    }

    // Decodifica el token y retorna todos sus datos internos
    // Privado porque solo lo usan los métodos de esta clase
    private Claims obtenerClaims(String token) {
        return Jwts.parserBuilder()

                // Indicamos con qué clave fue firmado para verificarlo
                .setSigningKey(getSigningKey())
                .build()

                // Verifica la firma y decodifica el token
                .parseClaimsJws(token)

                // Retorna el payload con todos los datos
                .getBody();
    }
}
