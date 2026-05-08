# SMCFT
# SMCFT - Sistema de Modernización de Control Fronterizo Terrestre

Este sistema está diseñado para la digitalización y agilización de los procesos aduaneros en pasos fronterizos terrestres. Permite gestionar el ingreso y salida de vehículos, declaraciones de equipaje y control sanitario, utilizando una arquitectura de microservicios desacoplada.

## 🚀 Arquitectura del Proyecto (12 Microservicios)

El sistema se divide en servicios independientes para garantizar escalabilidad y mantenimiento:

1.  **Auth Service**: Gestión de seguridad y tokens JWT.
2.  **User Service**: Administración de perfiles (Administradores, Fiscalizadores y Viajeros).
3.  **Vehicle Service**: Control de patentes, propiedad y estado de vehículos.
4.  **Border Crossing Service**: Registro y validación de salidas temporales.
5.  **Entry Service**: Gestión de ingresos al territorio nacional.
6.  **Item Category Service**: Categorización de equipaje y mercancías declaradas.
7.  **Deadline Service**: Control de plazos legales (regla de los 180 días).
8.  **Report Service**: Generación de estadísticas y consultas avanzadas.
9.  **Notification Service**: Sistema de alertas automáticas (Email/Push).
10. **Transaction Service**: Historial completo de movimientos por usuario/patente.
11. **Sanitary Service**: Control del SAG para mascotas y declaraciones juradas.
12. **Audit Service**: Registro de logs de operaciones para transparencia.

## 👥 Roles de Usuario
* **Administrador (SNA)**: Gestión de normativas, parámetros y usuarios.
* **Fiscalizador (Aduana / SAG)**: Validación de documentos, equipaje y estado sanitario.
* **Usuario Particular (Viajero)**: Autogestión de formularios y consulta de estados.

## 🛠️ Tecnologías Utilizadas
* **Lenguaje:** Java 
* **Framework:** Spring Boot 3.x
* **Base de Datos:** MySQL (Micro-bases de datos independientes)
* **Gestor de Dependencias:** Maven
* **Control de Versiones:** Git & GitHub

## 💻 Configuración para Desarrolladores
1. Clonar el repositorio:
   ```bash
   git clone [https://github.com/benjriquelmep-ship-it/SMCFT.git](https://github.com/benjriquelmep-ship-it/SMCFT.git)
