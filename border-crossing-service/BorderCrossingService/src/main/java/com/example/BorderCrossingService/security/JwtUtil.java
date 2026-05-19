// Utilidad JWT del Border Crossing Service
// Solo VERIFICA y LEE tokens — NO los genera
// Los tokens solo los genera el Auth Service cuando el usuario hace login
package com.example.BorderCrossingService.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    // Para registrar mensajes en la consola de IntelliJ
    private static final Logger log =
            LoggerFactory.getLogger(JwtUtil.class);

    // Lee la clave secreta desde application.properties
    @Value("${jwt.secret}")
    private String secret;

    // Crea la llave de seguridad a partir del texto secret
    // Se usa para verificar que el token no fue manipulado
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    // Extrae el email del usuario desde el token
    public String obtenerEmail(String token) {
        return obtenerClaims(token).getSubject();
    }

    // Extrae el rol del usuario desde el token
    public String obtenerRol(String token) {
        return obtenerClaims(token).get("rol", String.class);
    }

    // Verifica si el token es válido
    public boolean esValido(String token) {
        try {

            // Intenta leer el contenido del token
            // Si el token fue manipulado → lanza excepción → va al catch
            Claims claims = obtenerClaims(token);

            // Verifica que el token no haya expirado
            boolean noExpirado = claims.getExpiration().after(new Date());

            // Registra en la consola que el token fue validado
            log.info("Token validado en Border Crossing para: {}",
                    claims.getSubject());

            // Retorna true si no expiró, false si expiró
            return noExpirado;

        } catch (Exception e) {
            // Si llega aquí es porque el token: fue manipulado o falsificado..
            log.warn("Token inválido en Border Crossing: {}",
                    e.getMessage());
            return false;
        }
    }

    // Método privado que lee y decodifica el contenido del token. Es privado porque solo lo usan los otros métodos de esta clase
    private Claims obtenerClaims(String token) {
        return Jwts.parserBuilder()

                // Le indica la llave para verificar la firma del token
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}