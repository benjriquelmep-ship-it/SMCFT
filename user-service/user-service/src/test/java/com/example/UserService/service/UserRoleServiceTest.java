package com.example.UserService.service;

import com.example.UserService.dto.UserRoleDTO;
import com.example.UserService.model.User;
import com.example.UserService.model.UserRole;
import com.example.UserService.repository.UserRoleRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

// Activa Mockito para que JUnit maneje los mocks automáticamente
@ExtendWith(MockitoExtension.class)
class UserRoleServiceTest {
    // Crea el servicio real e inyecta los mocks automáticamente
    @InjectMocks
    private UserRoleService userRoleService;
    // Crea objetos simulados (mocks) para aislar el servicio de sus dependencias
    @Mock
    private UserRoleRepository userRoleRepository;
    @Mock
    private UserService userService;
    // Marca el método como prueba unitaria ejecutable por JUnit
    @Test

    void asignar_UsuarioExiste_DesactivaRolAnteriorYAsignaNuevo() {
        // Probar que cuando usuario existe → desactiva rol anterior y asigna nuevo

        // Crear el DTO con los datos necesarios para la prueba
        UserRoleDTO dto = new UserRoleDTO();
        dto.setUserId(1L);
        dto.setRol("ADMIN");
        User usuario = new User();
        usuario.setId(1L);
        UserRole rolActivo = new UserRole();
        rolActivo.setRol("FISCALIZADOR");
        rolActivo.setActivo(true);
        when(userService.obtenerPorId(1L)).thenReturn(usuario);
        when(userRoleRepository.findByUserIdAndActivoTrue(1L)).thenReturn(List.of(rolActivo));
        when(userRoleRepository.save(any(UserRole.class))).thenAnswer(i -> i.getArgument(0));

        // --- Ejecutar el método del servicio que se está probando ---
        UserRole result = userRoleService.asignar(dto);

        // --- Verificar que el resultado sea el correcto ---
        assertNotNull(result);
        assertEquals("ADMIN", result.getRol());
        assertTrue(result.getActivo());
        assertFalse(rolActivo.getActivo());

        // Verificar que el repositorio haya sido invocado la cantidad esperada de veces
        verify(userRoleRepository, times(2)).save(any(UserRole.class));
    }
    @Test

    void asignar_UsuarioNoExiste_LanzaExcepcion() {
        // Probar que cuando usuario no existe → lanza excepcion

        // Crear el DTO con los datos necesarios para la prueba
        UserRoleDTO dto = new UserRoleDTO();
        dto.setUserId(99L);
        dto.setRol("ADMIN");
        when(userService.obtenerPorId(99L)).thenThrow(new RuntimeException("Usuario no encontrado con id: 99"));

        // --- Ejecutar el método del servicio que se está probando ---
        RuntimeException ex = assertThrows(RuntimeException.class, () -> userRoleService.asignar(dto));

        // --- Verificar que el resultado sea el correcto ---
        assertTrue(ex.getMessage().contains("no encontrado"));
    }
}