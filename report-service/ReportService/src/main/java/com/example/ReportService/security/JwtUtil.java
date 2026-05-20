// Componente para parsear, extraer datos y validar tokens JWT en el microservicio
package com.example.ReportService.security;

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

    // Llave secreta de firma (leída desde application.properties)
    @Value("${jwt.secret}")
    private String secret;

    // Genera la clave criptográfica HMAC a partir del string secreto
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    // Extrae el correo (Subject) guardado en el cuerpo del token
    public String obtenerEmail(String token) {
        return obtenerClaims(token).getSubject();
    }

    // Extrae el rol o perfil personalizado guardado en los claims
    public String obtenerRol(String token) {
        return obtenerClaims(token).get("rol", String.class);
    }

    // Verifica que la firma sea correcta y que la fecha de expiración sea válida
    public boolean esValido(String token) {
        try {
            Claims claims = obtenerClaims(token);
            boolean noExpirado = claims.getExpiration().after(new Date());
            log.info("Token validado en Report Service para: {}",
                    claims.getSubject());
            return noExpirado;
        } catch (Exception e) {
            log.warn("Token inválido en Report Service: {}",
                    e.getMessage());
            return false;
        }
    }

    // Desencripta y lee el JSON interno (Claims) usando la clave secreta
    private Claims obtenerClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}