package com.example.VehicleService.service;

import com.example.VehicleService.dto.VehicleDocumentDTO;
import com.example.VehicleService.model.Vehicle;
import com.example.VehicleService.model.VehicleDocument;
import com.example.VehicleService.repository.VehicleDocumentRepository;
import com.example.VehicleService.service.VehicleService;
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
class VehicleDocumentServiceTest {
    // Crea el servicio real e inyecta los mocks automáticamente
    @InjectMocks
    private VehicleDocumentService documentService;
    // Crea objetos simulados (mocks) para aislar el servicio de sus dependencias
    @Mock
    private VehicleDocumentRepository documentRepository;
    @Mock
    private VehicleService vehicleService;
    // Marca el método como prueba unitaria ejecutable por JUnit
    @Test

    void agregar_DatosValidos_AgregaDocumento() {
        // Probar que cuando datos validos → agrega documento

        // Crear el DTO con los datos necesarios para la prueba
        VehicleDocumentDTO dto = new VehicleDocumentDTO();
        dto.setVehicleId(1L);
        dto.setTipo("PERMISO_CIRCULACION");
        dto.setNumero("DOC-001");
        dto.setFechaVencimiento(LocalDate.now().plusDays(60));
        Vehicle vehicle = new Vehicle();
        vehicle.setId(1L);
        when(vehicleService.obtenerPorId(1L)).thenReturn(vehicle);
        when(documentRepository.existsByNumero("DOC-001")).thenReturn(false);
        when(documentRepository.save(any(VehicleDocument.class))).thenAnswer(i -> i.getArgument(0));
        VehicleDocument result = documentService.agregar(dto);
        assertNotNull(result);
        assertTrue(result.getVigente());
        assertEquals("DOC-001", result.getNumero());
    }
    @Test

    void agregar_NumeroDuplicado_LanzaExcepcion() {
        // Probar que cuando numero duplicado → lanza excepcion

        // Crear el DTO con los datos necesarios para la prueba
        VehicleDocumentDTO dto = new VehicleDocumentDTO();
        dto.setVehicleId(1L);
        dto.setNumero("DOC-001");
        Vehicle vehicle = new Vehicle();
        vehicle.setId(1L);
        when(vehicleService.obtenerPorId(1L)).thenReturn(vehicle);
        when(documentRepository.existsByNumero("DOC-001")).thenReturn(true);
        // Llamar al servicio y capturar la excepción lanzada
        RuntimeException ex = assertThrows(RuntimeException.class, () -> documentService.agregar(dto));
        assertTrue(ex.getMessage().contains("número"));
    }
    @Test

    void invalidar_DocumentoExistente_MarcaNoVigente() {
        // Probar que cuando documento existente → marca no vigente

        VehicleDocument doc = new VehicleDocument();
        doc.setId(1L);
        doc.setVigente(true);

        // --- Preparar los datos de entrada y configurar los mocks ---
        when(documentRepository.findById(1L)).thenReturn(Optional.of(doc));
        when(documentRepository.save(any(VehicleDocument.class))).thenAnswer(i -> i.getArgument(0));
        VehicleDocument result = documentService.invalidar(1L);
        assertFalse(result.getVigente());
    }
}