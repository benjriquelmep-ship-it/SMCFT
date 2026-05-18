// Este archivo solo VERIFICA que el token sea válido
// y EXTRAE la información que contiene
package com.example.AuditService.security;

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

    // Logger para registrar mensajes en la consola de IntelliJ
    // Muestra si el token fue validado correctamente o no
    private static final Logger log =
            LoggerFactory.getLogger(JwtUtil.class);

    // Lee la clave secreta desde application.properties
    @Value("${jwt.secret}")
    private String secret;

    // Crea la llave de seguridad a partir del texto secret
    // Se usa para verificar la firma del token
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    // Extrae el email del usuario desde el token
    // El email fue guardado en el token por el Auth Service al hacer login
    public String obtenerEmail(String token) {
        return obtenerClaims(token).getSubject();
    }

    // Extrae el rol del usuario desde el token
    // El rol fue guardado en el token por el Auth Service al hacer login
    public String obtenerRol(String token) {
        return obtenerClaims(token).get("rol", String.class);
    }

    // Verifica si el token es válido
    // Devuelve true si el token es válido
    // Devuelve false si el token expiró o fue manipulado
    public boolean esValido(String token) {
        try {

            // Intenta leer el contenido del token
            Claims claims = obtenerClaims(token);

            // Verifica que el token no haya expirado
            boolean noExpirado = claims.getExpiration().after(new Date());

            // Registra en la consola que el token fue validado
            log.info("Token validado en Audit Service para: {}",
                    claims.getSubject());

            // Retorna true si no expiró, false si expiró
            return noExpirado;

            // Si llega aquí es porque el token: fue manipulado o falsificado...
        } catch (Exception e) {
            log.warn("Token inválido en Audit Service: {}",
                    e.getMessage());
            return false;
        }
    }

    // Método privado que lee y decodifica el contenido del token
    // Es privado porque solo lo usan los otros métodos de esta clase
    private Claims obtenerClaims(String token) {
        return Jwts.parserBuilder()

                // Le indica la llave para verificar la firma del token
                .setSigningKey(getSigningKey())

                // Construye el parser con la configuración anterior
                .build()

                // Parsea el token y verifica su firma al mismo tiempo
                .parseClaimsJws(token)

                // Extrae el body del token que contiene
                .getBody();
    }
}