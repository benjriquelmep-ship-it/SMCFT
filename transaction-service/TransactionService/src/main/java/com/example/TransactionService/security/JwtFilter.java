// Filtro de seguridad que se ejecuta en cada petición HTTP entrante para validar tokens
package com.example.TransactionService.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private static final Logger log =
            LoggerFactory.getLogger(JwtFilter.class);

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // Extrae el encabezado 'Authorization' de la petición HTTP
        String authHeader = request.getHeader("Authorization");

        // Verifica si viene el token con el prefijo estándar 'Bearer '
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // Remueve "Bearer " para aislar el JWT

            // Valida la firma, expiración y estructura del token
            if (jwtUtil.esValido(token)) {
                String email = jwtUtil.obtenerEmail(token);
                String rol   = jwtUtil.obtenerRol(token);

                log.info("Token válido en Transaction para: {} rol: {}",
                        email, rol);

                // Autentica al usuario en el contexto de Spring Security con su rol correspondiente
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                email,
                                null,
                                List.of(new SimpleGrantedAuthority("ROLE_" + rol))
                        );
                SecurityContextHolder.getContext()
                        .setAuthentication(authentication);
            } else {
                log.warn("Token inválido en Transaction para: {}",
                        request.getRequestURI());
            }
        }

        // Continúa con el siguiente filtro en la cadena de seguridad de Spring
        filterChain.doFilter(request, response);
    }
}