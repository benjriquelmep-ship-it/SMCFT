# Dependencias del Proyecto SMCFT

## Versiones Base

- **Spring Boot**: 4.0.6 (servicios de negocio), 3.4.2 (eureka-server, gateway)
- **Spring Cloud**: 2025.1.2 (servicios), 2024.0.0 (eureka-server, gateway)
- **Java**: 21

---

## 1. Dependencias Compartidas (todos los servicios de negocio)

| Dependencia | Versión | ¿Qué hace? |
|-------------|---------|------------|
| `spring-boot-starter-data-jpa` | *managed* | JPA + Hibernate para BD |
| `spring-boot-starter-webmvc` | *managed* | REST controllers |
| `spring-boot-starter-validation` | *managed* | Jakarta Validation (@NotBlank, @Email, etc.) |
| `spring-boot-starter-security` | *managed* | Seguridad Spring |
| `spring-boot-starter-hateoas` | *managed* | HATEOAS (links REST) |
| `spring-boot-starter-flyway` | *managed* | Migraciones de BD |
| `flyway-mysql` | *managed* | Driver Flyway para MySQL |
| `mysql-connector-j` | *managed* | Driver MySQL |
| `spring-cloud-starter-netflix-eureka-client` | *managed* | Service Discovery |
| `springdoc-openapi-starter-webmvc-ui` | 2.8.13 | Swagger / OpenAPI |
| `net.logstash.logback` | 7.4 | Logs en JSON |
| `jjwt-api` | 0.11.5 | JWT (firmar/verificar tokens) |
| `jjwt-impl` | 0.11.5 | Implementación JWT (runtime) |
| `jjwt-jackson` | 0.11.5 | Serialización JWT (runtime) |
| `lombok` | *managed* | `@Getter`, `@Setter`, `@Slf4j`, etc. |

---

## 2. Dependencias de Test (todos los servicios de negocio)

| Dependencia | Scope | ¿Qué hace? |
|-------------|-------|------------|
| `spring-boot-starter-data-jpa-test` | test | Testing de JPA |
| `spring-boot-starter-flyway-test` | test | Testing de Flyway |
| `spring-boot-starter-validation-test` | test | Testing de validación |
| `spring-boot-starter-webmvc-test` | test | Testing de WebMVC |
| `com.h2database:h2` | test | BD en memoria H2 (no requiere MySQL) |

**Adicionales** solo en auth-service, audit-service, deadline-service, sanitary-service, transaction-service, report-service:

| spring-boot-starter-security-test | test | Testing de seguridad |
|-----------------------------------|-------|---------------------|

**Adicionales** solo en auth-service, notification-service, vehicle-service, item-category-service, border-crossing-service, entry-service:

| spring-boot-starter-webflux-test | test | Testing de WebClient |
|----------------------------------|-------|---------------------|

---

## 3. Dependencias por Servicio

### eureka-server
| Dependencia | Versión |
|-------------|---------|
| `spring-cloud-starter-netflix-eureka-server` | *managed* |
| `spring-boot-starter-test` | *managed* |

### gateway
| Dependencia | Versión |
|-------------|---------|
| `spring-cloud-starter-gateway` | *managed* |
| `spring-boot-starter-webflux` | *managed* |
| `springdoc-openapi-starter-webflux-ui` | 2.8.13 |
| `spring-cloud-starter-netflix-eureka-client` | *managed* |
| `lombok` | *managed* |
| `spring-boot-starter-test` | *managed* |

### auth-service (auth-service/AuthService)
| Dependencia | Versión |
|-------------|---------|
| `spring-boot-starter-data-jpa` | *managed* |
| `spring-boot-starter-flyway` | *managed* |
| `spring-boot-starter-security` | *managed* |
| `spring-boot-starter-hateoas` | *managed* |
| `spring-boot-starter-validation` | *managed* |
| `spring-boot-starter-webflux` | *managed* |
| `spring-boot-starter-webmvc` | *managed* |
| `flyway-mysql` | *managed* |
| `mysql-connector-j` | *managed* |
| `spring-cloud-starter-netflix-eureka-client` | *managed* |
| `springdoc-openapi-starter-webmvc-ui` | 2.8.13 |
| `net.logstash.logback` | 7.4 |
| `jjwt-api` | 0.11.5 |
| `jjwt-impl` | 0.11.5 (runtime) |
| `jjwt-jackson` | 0.11.5 (runtime) |
| `lombok` | *managed* |
| `spring-boot-starter-data-jpa-test` | test |
| `spring-boot-starter-flyway-test` | test |
| `spring-boot-starter-security-test` | test |
| `spring-boot-starter-validation-test` | test |
| `spring-boot-starter-webflux-test` | test |
| `spring-boot-starter-webmvc-test` | test |
| `com.h2database:h2` | test |

### user-service (user-service/user-service) — IGUAL que auth-service pero sin webflux

### vehicle-service, notification-service, item-category-service, audit-service, border-crossing-service, deadline-service, sanitary-service, transaction-service, entry-service, report-service

Todos siguen la misma estructura que auth-service.

---

## 4. Diferencia clave: auth-service vs user-service

| Dependencia | auth-service | user-service |
|-------------|:---:|:---:|
| `spring-boot-starter-webflux` | ✅ | ❌ |
| `spring-boot-starter-webflux-test` | ✅ | ❌ |
| `spring-boot-starter-security-test` | ✅ | ❌ |
| JWT (jjwt) | ✅ | ✅ |

---

## 5. ¿Qué hace cada dependencia?

| Dependencia | Explicación simple |
|-------------|-------------------|
| `spring-boot-starter-data-jpa` | Conexión a BD y repositorios |
| `spring-boot-starter-webflux` | WebClient para llamadas REST entre servicios |
| `spring-boot-starter-webmvc` | Controladores REST (@RestController) |
| `spring-boot-starter-security` | Login, roles, JWT |
| `spring-boot-starter-hateoas` | Links HATEOAS en respuestas REST |
| `spring-boot-starter-validation` | @NotBlank, @Email, @Size en DTOs |
| `spring-boot-starter-flyway` | Migraciones automáticas de BD |
| `flyway-mysql` | Soporte MySQL para Flyway |
| `mysql-connector-j` | Conector MySQL |
| `spring-cloud-starter-netflix-eureka-client` | Registrar servicio en Eureka |
| `springdoc-openapi-starter-webmvc-ui` | Swagger UI automático |
| `net.logstash.logback` | Logs en formato JSON |
| `jjwt-api` | Crear y validar tokens JWT |
| `jjwt-impl` | Implementación interna de JWT |
| `jjwt-jackson` | Convertir JWT a JSON |
| `lombok` | Menos código boilerplate |
| `spring-cloud-starter-gateway` | API Gateway con rutas |
| `spring-cloud-starter-netflix-eureka-server` | Servidor Eureka (service discovery) |
| `com.h2database:h2` | BD en memoria para tests |
| `spring-boot-starter-test` | JUnit 5 + Mockito + AssertJ |
