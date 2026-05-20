// Componente encargado de desencriptar, leer y verificar la validez de los tokens JWT
package com.example.TransactionService.security;

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

    private static final Logger log =
            LoggerFactory.getLogger(JwtUtil.class);

    // Firma secreta inyectada desde los archivos de propiedades
    @Value("${jwt.secret}")
    private String secret;

    // Genera la llave secreta criptográfica HMAC basada en bytes
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    // Extrae el correo electrónico (Subject) del cuerpo del token
    public String obtenerEmail(String token) {
        return obtenerClaims(token).getSubject();
    }

    // Extrae el atributo o claim personalizado con el rol del usuario
    public String obtenerRol(String token) {
        return obtenerClaims(token).get("rol", String.class);
    }

    // Comprueba la firma del JWT y verifica que no esté vencido
    public boolean esValido(String token) {
        try {
            Claims claims = obtenerClaims(token);
            boolean noExpirado = claims.getExpiration().after(new Date());
            log.info("Token validado en Transaction Service para: {}",
                    claims.getSubject());
            return noExpirado;
        } catch (Exception e) {
            log.warn("Token inválido en Transaction Service: {}",
                    e.getMessage());
            return false;
        }
    }

    // Parsea y extrae el conjunto de Claims usando la firma configurada
    private Claims obtenerClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}