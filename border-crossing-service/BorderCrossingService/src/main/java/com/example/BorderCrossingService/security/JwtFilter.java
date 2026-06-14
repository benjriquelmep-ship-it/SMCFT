// Filtro que intercepta TODAS las peticiones del Border Crossing Service
// Verifica si la petición trae un token JWT válido
// Si es válido → registra al usuario en el contexto de seguridad
// Si no hay token o es inválido → continúa sin autenticación
package com.example.BorderCrossingService.security;

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

    // Para registrar mensajes en la consola de IntelliJ
    private static final Logger log =
            LoggerFactory.getLogger(JwtFilter.class);

    // Verifica la firma y expiración del token
    private final JwtUtil jwtUtil;

    // Excluye las rutas de Swagger UI y OpenAPI para que no requieran validación de JWT
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return path.startsWith("/swagger-ui") || path.startsWith("/v3/api-docs");
    }

    // Se ejecuta automáticamente en CADA petición antes de llegar al Controller
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // Lee el header Authorization de la petición
        String authHeader = request.getHeader("Authorization");

        // Verifica que el header no sea null y empiece con "Bearer "
        // Si no cumple estas condiciones → no hay token → salta el if
        if (authHeader != null && authHeader.startsWith("Bearer ")) {

            // Elimina "Bearer " y se queda solo con el token
            String token = authHeader.substring(7);

            // Le pregunta a JwtUtil si el token es válido
            // Verifica que no esté expirado y que la firma sea correcta
            if (jwtUtil.esValido(token)) {

                // Extrae el email del usuario desde el token
                String email = jwtUtil.obtenerEmail(token);

                // Extrae el rol del usuario desde el token
                String rol = jwtUtil.obtenerRol(token);

                // Registra en la consola que el token es válido
                log.info("Token válido en Border Crossing para: {} rol: {}",
                        email, rol);

                // Crea el objeto de autenticación con los datos del usuario
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                email,
                                null,
                                // Spring requiere el prefijo ROLE_ antes del nombre
                                // Ej: "FISCALIZADOR" → "ROLE_FISCALIZADOR"
                                List.of(new SimpleGrantedAuthority("ROLE_" + rol))
                        );

                // Guarda la autenticación en el SecurityContext
                // Spring usa esto para verificar permisos en cada ruta
                // SecurityConfig decide qué rutas necesitan autenticación
                SecurityContextHolder.getContext()
                        .setAuthentication(authentication);

            } else {
                // El token llegó pero no es válido. Puede ser porque expiró o fue manipulado
                log.warn("Token inválido en Border Crossing para: {}",
                        request.getRequestURI());
            }
        }

        // Permite que la petición continúe hacia el Controller
        // Se llama SIEMPRE — tanto si hay token como si no hay
        // Sin esta línea la petición quedaría bloqueada aquí
        filterChain.doFilter(request, response);
    }
}