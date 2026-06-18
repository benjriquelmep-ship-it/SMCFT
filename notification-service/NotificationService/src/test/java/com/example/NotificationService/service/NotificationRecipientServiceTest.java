package com.example.NotificationService.service;

import com.example.NotificationService.model.NotificationRecipient;
import com.example.NotificationService.repository.NotificationRecipientRepository;
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
class NotificationRecipientServiceTest {
    // Crea el servicio real e inyecta los mocks automáticamente
    @InjectMocks
    private NotificationRecipientService recipientService;
    // Crea objetos simulados (mocks) para aislar el servicio de sus dependencias
    @Mock
    private NotificationRecipientRepository recipientRepository;
    // Marca el método como prueba unitaria ejecutable por JUnit
    @Test

    void obtenerPorNotificacion_Existente_RetornaDestinatarios() {
        // Probar que cuando existente → retorna destinatarios

        NotificationRecipient recipient = new NotificationRecipient();

        // --- Preparar los datos de entrada y configurar los mocks ---
        when(recipientRepository.findByNotificationId(1L)).thenReturn(List.of(recipient));
        List<NotificationRecipient> result = recipientService.obtenerPorNotificacion(1L);
        assertFalse(result.isEmpty());
    }
}