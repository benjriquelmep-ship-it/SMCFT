// security/JwtFilter.java
// Se ejecuta en CADA petición HTTP que llega al servidor
// Verifica si la petición trae un token JWT válido
// Si es válido → registra al usuario en el contexto de seguridad
// Si no hay token o es inválido → continúa sin autenticación

package com.example.AuthService.security;

import com.example.AuthService.repository.TokenBlacklistRepository;
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
// OncePerRequestFilter garantiza que el filtro se ejecute una vez por petición
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtFilter.class);

    // Para validar firma y expiración del token
    private final JwtUtil jwtUtil;

    // Para verificar si el token fue invalidado por logout
    private final TokenBlacklistRepository tokenBlacklistRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // Leer el header Authorization
        // Formato esperado: Authorization: Bearer eyJhbGci...
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {

            // Extraer solo el token quitando "Bearer " (7 caracteres)
            String token = authHeader.substring(7);

            // VERIFICACIÓN 1 — firma válida y no expirado
            boolean firmaValida = jwtUtil.esValido(token);

            // VERIFICACIÓN 2 — no está en la blacklist (no hizo logout)
            boolean noEnBlacklist =
                    !tokenBlacklistRepository.existsByToken(token);

            // Solo autenticar si pasa AMBAS verificaciones
            if (firmaValida && noEnBlacklist) {
                String email = jwtUtil.obtenerEmail(token);
                String rol   = jwtUtil.obtenerRol(token);

                log.info("Token válido para: {} con rol: {}", email, rol);

                // Registrar al usuario en el contexto de seguridad de Spring
                // Spring Security usa esto para verificar permisos en cada ruta
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                email,
                                null,
                                // Spring Security requiere el prefijo "ROLE_"
                                List.of(new SimpleGrantedAuthority("ROLE_" + rol))
                        );
                SecurityContextHolder.getContext()
                        .setAuthentication(authentication);

            } else {
                if (!firmaValida) {
                    log.warn("Token inválido en: {}",
                            request.getRequestURI());
                }
                if (!noEnBlacklist) {
                    log.warn("Token en blacklist en: {}",
                            request.getRequestURI());
                }
            }
        }

        // SIEMPRE llamar a doFilter para continuar la cadena
        // Sin esto la petición queda bloqueada indefinidamente
        filterChain.doFilter(request, response);
    }
}