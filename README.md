# SMCFT - Sistema de Monitoreo y Control Fronterizo Terrestre

**Estudiantes:** Benjamin Riquelme, Aileen Oyaneder

## Microservicios

| # | Servicio | Puerto | Descripción |
|---|----------|--------|-------------|
| 1 | AuthService | 2022 | Autenticación y control de sesiones |
| 2 | UserService | 2031 | Gestión de operadores e identidades |
| 3 | VehicleService | 2032 | Parque vehicular y patentes |
| 4 | BorderCrossingService | 2033 | Controles de tránsito y equipajes |
| 5 | EntryService | 2025 | Declaraciones y registros de ingreso |
| 6 | SanitaryService | 2029 | Inspecciones y visados sanitarios |
| 7 | DeadlineService | 2024 | Control de alertas y tiempos límite |
| 8 | ItemCategoryService | 2026 | Catálogo de categorías arancelarias |
| 9 | TransactionService | 2040 | Conciliación y pagos aduaneros |
| 10 | ReportService | 2028 | Motor de reportería estadística |
| 11 | NotificationService | 2027 | Mensajería y despacho de alertas |
| 12 | AuditService | 2021 | Bitácora e historial forense |
| - | Eureka Server | 8760 | Service Discovery |
| - | API Gateway | 2020 | Punto de entrada único |

## Rutas del Gateway

Todas las rutas parten de `http://localhost:2020/api/v1/`:

- `auth/**` → AuthService
- `users/**` → UserService
- `vehicles/**` → VehicleService
- `border-crossings/**` → BorderCrossingService
- `entries/**` → EntryService
- `sanitary/**` → SanitaryService
- `deadline-alerts/**` → DeadlineService
- `item-categories/**` → ItemCategoryService
- `transactions/**` → TransactionService
- `reports/**` → ReportService
- `notifications/**` → NotificationService
- `audits/**` → AuditService

## Documentación

- **Swagger Gateway:** http://localhost:2020/swagger-ui.html
- **Eureka Dashboard:** http://localhost:8760
