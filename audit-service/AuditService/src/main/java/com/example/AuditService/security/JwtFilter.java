// Este archivo es el filtro JWT del Audit Service
// Su trabajo es interceptar TODAS las peticiones que llegan
// y verificar si traen un token válido antes de procesarlas
package com.example.AuditService.security;

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

    // Logger para registrar mensajes en la consola de IntelliJ
    // Muestra quién se autenticó o qué token llegó inválido
    private static final Logger log =
            LoggerFactory.getLogger(JwtFilter.class);

    // JwtUtil contiene los métodos para validar y leer el token
    // Se inyecta automáticamente por @RequiredArgsConstructor
    private final JwtUtil jwtUtil;

    // Excluye las rutas de Swagger UI y OpenAPI para que no requieran validación de JWT
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return path.startsWith("/swagger-ui") || path.startsWith("/v3/api-docs");
    }

    // Este método se ejecuta automáticamente en CADA petición
    // que llega al Audit Service antes de llegar al Controller
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // Intenta leer el header Authorization de la petición
        String authHeader = request.getHeader("Authorization");

        // Verifica que el header no sea null
        // y que empiece con "Bearer " (con espacio)
        // Si no cumple estas condiciones no hay token → salta el if
        if (authHeader != null && authHeader.startsWith("Bearer ")) {

            // Extrae solo el token sin la palabra "Bearer "
            String token = authHeader.substring(7);

            // Le pregunta a JwtUtil si el token es válido
            // Verifica que no esté expirado y que la firma sea correcta
            if (jwtUtil.esValido(token)) {

                // Extrae el email del usuario desde el token
                String email = jwtUtil.obtenerEmail(token);

                // Extrae el rol del usuario desde el token
                String rol   = jwtUtil.obtenerRol(token);

                // Registra en la consola que el token es válido
                log.info("Token válido en Audit Service para: {} rol: {}",
                        email, rol);

                // Crea el objeto de autenticación de Spring
                // Este objeto representa al usuario autenticado
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                email,
                                null,
                                List.of(new SimpleGrantedAuthority("ROLE_" + rol))
                        );

                // Guarda la autenticación en el SecurityContext
                SecurityContextHolder.getContext()
                        .setAuthentication(authentication);
            } else {

                // Se registra una advertencia en la consola
                log.warn("Token inválido en Audit Service para: {}",
                        request.getRequestURI());
            }
        }

        // permite que la petición continúe
        // hacia el siguiente filtro o hacia el Controller
        filterChain.doFilter(request, response);
    }
}