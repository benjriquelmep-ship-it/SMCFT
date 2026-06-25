package com.example.UserService.service;

import com.example.UserService.dto.UserDTO;
import com.example.UserService.model.User;
import com.example.UserService.repository.UserRepository;
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
class UserServiceTest {
    // Crea el servicio real e inyecta los mocks automáticamente
    @InjectMocks
    private UserService userService;
    // Crea objetos simulados (mocks) para aislar el servicio de sus dependencias
    @Mock
    private UserRepository userRepository;
    // Marca el método como prueba unitaria ejecutable por JUnit
    @Test

    void crear_RutUnicoYEmailUnico_CreaUsuario() {
        // Probar que cuando rut unico y email unico → crea usuario

        // Crear el DTO con los datos necesarios para la prueba
        UserDTO dto = new UserDTO();
        dto.setRut("12345678-9");
        dto.setNombre("Juan Perez");
        dto.setEmail("juan@test.com");
        dto.setPassword("pass123");
        dto.setRol("FISCALIZADOR");
        when(userRepository.existsByRut("12345678-9")).thenReturn(false);
        when(userRepository.existsByEmail("juan@test.com")).thenReturn(false);
        User userToSave = new User();
        userToSave.setId(1L);
        when(userRepository.save(any(User.class))).thenReturn(userToSave);

        // --- Ejecutar el método del servicio que se está probando ---
        User result = userService.crear(dto);

        // --- Verificar que el resultado sea el correcto ---
        assertNotNull(result);
        assertEquals(1L, result.getId());

        // Verificar que el repositorio haya sido invocado la cantidad esperada de veces
        verify(userRepository, times(1)).save(any(User.class));
    }
    @Test

    void crear_RutDuplicado_LanzaExcepcion() {
        // Probar que cuando rut duplicado → lanza excepcion

        // Crear el DTO con los datos necesarios para la prueba
        UserDTO dto = new UserDTO();
        dto.setRut("12345678-9");
        dto.setEmail("juan@test.com");
        when(userRepository.existsByRut("12345678-9")).thenReturn(true);

        // --- Ejecutar el método del servicio que se está probando ---
        RuntimeException ex = assertThrows(RuntimeException.class, () -> userService.crear(dto));

        // --- Verificar que el resultado sea el correcto ---
        assertTrue(ex.getMessage().contains("RUT"));

        // Verificar que el repositorio NO haya sido invocado
        verify(userRepository, never()).save(any());
    }
    @Test

    void crear_EmailDuplicado_LanzaExcepcion() {
        // Probar que cuando email duplicado → lanza excepcion

        // Crear el DTO con los datos necesarios para la prueba
        UserDTO dto = new UserDTO();
        dto.setRut("12345678-9");
        dto.setEmail("juan@test.com");
        when(userRepository.existsByRut("12345678-9")).thenReturn(false);
        when(userRepository.existsByEmail("juan@test.com")).thenReturn(true);

        // --- Ejecutar el método del servicio que se está probando ---
        RuntimeException ex = assertThrows(RuntimeException.class, () -> userService.crear(dto));

        // --- Verificar que el resultado sea el correcto ---
        assertTrue(ex.getMessage().contains("email"));

        // Verificar que el repositorio NO haya sido invocado
        verify(userRepository, never()).save(any());
    }
    @Test

    void obtenerPorId_Existente_RetornaUsuario() {
        // Probar que cuando existente → retorna usuario

        User user = new User();
        user.setId(1L);

        // --- Preparar los datos de entrada y configurar los mocks ---
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // --- Ejecutar el método del servicio que se está probando ---
        User result = userService.obtenerPorId(1L);

        // --- Verificar que el resultado sea el correcto ---
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }
    @Test

    void obtenerPorId_NoExistente_LanzaExcepcion() {
        // Probar que cuando no existente → lanza excepcion

        // --- Preparar los datos de entrada y configurar los mocks ---
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        // --- Ejecutar el método del servicio que se está probando ---
        RuntimeException ex = assertThrows(RuntimeException.class, () -> userService.obtenerPorId(99L));

        // --- Verificar que el resultado sea el correcto ---
        assertTrue(ex.getMessage().contains("no encontrado"));
    }
    @Test

    void actualizar_DatosValidos_ActualizaCampos() {
        // PASO 1 - GIVEN: preparar datos y configurar mocks
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setNombre("Viejo Nombre");
        existingUser.setEmail("viejo@test.com");
        existingUser.setRol("VIAJERO");

        UserDTO dto = new UserDTO();
        dto.setNombre("Nuevo Nombre");
        dto.setEmail("nuevo@test.com");
        dto.setRol("FISCALIZADOR");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        // PASO 2 - WHEN: ejecutar el método del servicio
        User result = userService.actualizar(1L, dto);

        // PASO 3 - THEN: verificar resultados
        assertEquals("Nuevo Nombre", result.getNombre());
        assertEquals("nuevo@test.com", result.getEmail());
        assertEquals("FISCALIZADOR", result.getRol());
        verify(userRepository, times(1)).save(existingUser);
    }

    @Test

    void eliminar_SoftDelete_DesactivaUsuario() {
        // Probar que cuando soft delete → desactiva usuario

        User user = new User();
        user.setId(1L);
        user.setActivo(true);

        // --- Preparar los datos de entrada y configurar los mocks ---
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // --- Ejecutar el método del servicio que se está probando ---
        userService.eliminar(1L);

        // --- Verificar que el resultado sea el correcto ---
        assertFalse(user.getActivo());

        // Verificar que el repositorio haya sido invocado la cantidad esperada de veces
        verify(userRepository).save(user);
    }

    @Test

    void obtenerPorEmail_Existente_RetornaUsuario() {
        User user = new User();
        user.setId(1L);
        user.setEmail("juan@test.com");
        when(userRepository.findByEmail("juan@test.com")).thenReturn(Optional.of(user));

        User result = userService.obtenerPorEmail("juan@test.com");

        assertNotNull(result);
        assertEquals("juan@test.com", result.getEmail());
    }

    @Test

    void obtenerPorEmail_NoExistente_LanzaExcepcion() {
        when(userRepository.findByEmail("no@existe.com")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> userService.obtenerPorEmail("no@existe.com"));
        assertTrue(ex.getMessage().contains("no encontrado"));
    }

    @Test

    void actualizar_UsuarioNoExistente_LanzaExcepcion() {
        UserDTO dto = new UserDTO();
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.actualizar(99L, dto));
        verify(userRepository, never()).save(any());
    }

    @Test

    void obtenerActivos_RetornaSoloActivos() {
        List<User> activos = List.of(new User(), new User());
        when(userRepository.findByActivoTrue()).thenReturn(activos);

        List<User> result = userService.obtenerActivos();

        assertEquals(2, result.size());
        verify(userRepository).findByActivoTrue();
    }

    @Test

    void buscarPorNombre_TextoCoincide_RetornaUsuarios() {
        User user = new User();
        user.setNombre("Juan Perez");
        when(userRepository.findByNombreContaining("Juan")).thenReturn(List.of(user));

        List<User> result = userService.buscarPorNombre("Juan");

        assertEquals(1, result.size());
        assertEquals("Juan Perez", result.get(0).getNombre());
    }
}