# ✈️ Andes Airlines - Check-in API

Sistema de check-in automatizado que simula la asignación de asientos para vuelos comerciales. La API implementa reglas de negocio como menores acompañados y clases de asiento, y permite reasignación manual.

## 🚀 Empezando

### Requisitos Previos

- Java 17 o superior
- Maven 3.8+
- Docker (opcional, para ejecución con contenedores)

### Configuración del Entorno

1. **Clonar el repositorio**
```bash
git clone https://github.com/patriciodunstan/checkin-api.git
cd checkin-api
```

2. **Configurar base de datos**
   - Para producción: MySQL 8.0+, crea una base `checkin` y configura credenciales en `src/main/resources/application-prod.yml`.
   - Para pruebas: H2 en memoria (no requiere configuración adicional).

3. **Perfiles de Ejecución**
   - **Desarrollo**: `mvn spring-boot:run -Dspring-boot.run.profiles=dev`
   - **Pruebas**: `mvn test -Dspring.profiles.active=test`
   - **Producción**: `mvn spring-boot:run -Dspring-boot.run.profiles=prod`

## 🌐 Producción

- **API Base URL**: `https://checkin-api-idfh.onrender.com/api`
- **Swagger UI**: `https://checkin-api-idfh.onrender.com/api/swagger-ui.html`
- **Documentación OpenAPI**: `https://checkin-api-idfh.onrender.com/api/api-docs`

### Uso de Swagger UI
1. Abre [Swagger UI](https://checkin-api-idfh.onrender.com/api/swagger-ui.html)
2. Explora los endpoints:
   - `GET /flights/{flightId}/passengers` → check-in automático de vuelo
   - `PUT /flights/{flightId}/passengers/{passengerId}/seat` → reasignación manual
3. Haz clic en "Try it out", completa los parámetros y presiona "Execute" para probar.

### Usando curl
```bash
# Consultar vuelo 1
curl -X GET "https://checkin-api-idfh.onrender.com/api/flights/1/passengers"

# Reasignar asiento
curl -X PUT "https://checkin-api-idfh.onrender.com/api/flights/1/passengers/144/seat?seatRow=3&seatColumn=B"
```

## 🎯 Funcionalidades

- Check-in automatizado con asignación inteligente de asientos
- Reasignación manual de asientos
- Consulta de vuelos con pasajeros y asientos
- Validaciones de negocio completas
- Manejo centralizado de excepciones
- Tests unitarios (~93 tests, cobertura ~70%)
- Documentación API con Swagger
- Multi-ambiente: dev / prod
- Deployment en Render

## 📋 Endpoints Disponibles

#### 1. Consultar Vuelo con Check-in Automático
```bash
curl -X GET "https://checkin-api-production.up.railway.app/api/flights/1/passengers"
```

**¿Qué hace?**
- Simula el proceso de check-in para el vuelo especificado
- Asigna asientos automáticamente siguiendo las reglas de negocio
- Devuelve la información completa del vuelo con todos los pasajeros

**Respuesta de ejemplo:**
```json
{
  "code": 200,
  "data": {
    "flightId": 1,
    "takeoffDateTime": 1688207580,
    "takeoffAirport": "Aeropuerto Internacional Arturo Merino Benitez, Chile",
    "landingDateTime": 1688221980,
    "landingAirport": "Aeropuerto Internacional Jorge Chávez, Perú",
    "airplaneId": 1,
    "passengers": [
      {
        "passengerId": 144,
        "dni": "372916627",
        "name": "Maximiliana",
        "age": 39,
        "country": "Chile",
        "boardingPassId": 7,
        "purchaseId": 141,
        "seatTypeId": 1,
        "seatId": 105,
        "seatRow": "1",
        "seatColumn": "F"
      }
      // ... más pasajeros
    ]
  },
  "errors": null
}
```

#### 2. Asignar Asiento Manualmente
```bash
curl -X PUT "https://checkin-api-idfh.onrender.com/api/flights/1/passengers/144/seat?seatRow=2&seatColumn=A"
```

**¿Qué hace?**
- Permite reasignar un asiento específico a un pasajero
- Valida que el asiento exista y esté disponible
- Verifica que el tipo de asiento sea compatible con el pasajero

## 🧪 Probar la API Localmente

### Configuración Inicial

1. **Base de Datos en Memoria**
   - El perfil `test` utiliza H2 en memoria
   - No se requiere configuración adicional

2. **Ejecutar Tests**
   ```bash
   # Ejecutar todos los tests
   mvn test
   
   # Ejecutar tests específicos
   mvn test -Dtest=FlightControllerTest
   
   # Con cobertura de código
   mvn clean test jacoco:report
   ```

### Pruebas Manuales

1. **Iniciar la aplicación en modo desarrollo**
   ```bash
   mvn spring-boot:run -Dspring-boot.run.profiles=dev
   ```

2. **Acceder a Swagger UI**
   - Abrir en el navegador: http://localhost:8080/api/swagger-ui.html
   - Explorar y probar los endpoints disponibles

### Usando Swagger UI (Recomendado)
1. **Local**: Ve a [http://localhost:8080/api/swagger-ui.html](http://localhost:8080/api/swagger-ui.html)
2. **Producción**: Ve a [https://checkin-api-idfh.onrender.com/api/swagger-ui.html](https://checkin-api-idfh.onrender.com/api/swagger-ui.html)
3. Expande el endpoint `GET /api/flights/{flightId}/passengers`
4. Haz clic en "Try it out"
5. Ingresa `1` como flightId
6. Haz clic en "Execute"

### Usando curl
```bash
# Consultar vuelo 1 (Local)
curl -X GET "http://localhost:8080/api/flights/1/passengers"

# Consultar vuelo 1 (Producción)
curl -X GET "https://checkin-api-idfh.onrender.com/api/flights/1/passengers"

# Reasignar asiento (Local)
curl -X PUT "http://localhost:8080/api/flights/1/passengers/144/seat?seatRow=3&seatColumn=B"

# Reasignar asiento (Producción)
curl -X PUT "https://checkin-api-idfh.onrender.com/api/flights/1/passengers/144/seat?seatRow=3&seatColumn=B"
```

## 🛠️ Tecnologías

- Java 17, Spring Boot 3.4
- MySQL (producción) / H2 (desarrollo)
- Spring Data JPA + Hibernate
- JUnit 5, Mockito
- Swagger / OpenAPI 3
- Maven
- Deployment: Render / Docker

## 🏗️ Arquitectura

```
src/
├── main/java/com/andesairlines/checkin_api/
│   ├── airplane/    # Gestión de aviones y asientos
│   ├── common/      # Excepciones y respuestas comunes
│   ├── flight/      # Lógica de vuelos y check-in
│   └── passenger/   # Gestión de pasajeros
├── main/resources/
│   ├── application.yml
│   ├── application-dev.yml
│   └── application-prod.yml
└── test/            # Tests unitarios e integración
```

- **Patrón**: Controller → Service → Repository
- **Separación de responsabilidades** clara
- **Error handling** centralizado
- **Tests unitarios** con mocks

## 🎯 Reglas de Negocio

- Menores acompañados por adultos
- Asientos consecutivos para grupos
- Respeto a clases de asiento (Business, Economy)
- Validación de disponibilidad y compatibilidad
- Manejo de errores con códigos HTTP

## 🧪 Testing

```bash
# Ejecutar tests unitarios
mvn test

# Cobertura
mvn clean test jacoco:report
open target/site/jacoco/index.html
```

## 📝 Licencia

MIT License - ver [LICENSE](LICENSE)

## 🤝 Contribución

Lee [CONTRIBUTING.md](CONTRIBUTING.md) para más información.

## 📞 Contacto

dev@andesairlines.com

## 🏠 Ejecución Local

### Requisitos
- **Java 21** o superior
- **Maven 3.8+**

### Pasos
```bash
# 1. Clonar repositorio
git clone <repository-url>
cd checkin-api

# 2. Ejecutar (usa base de datos en memoria H2)
mvn spring-boot:run

# 3. Acceder localmente
# API: http://localhost:8080/api/flights/1/passengers
# Swagger: http://localhost:8080/api/swagger-ui.html
```

**Nota**: La aplicación usa el context path `/api`, por lo que todos los endpoints tienen este prefijo.

## 🧪 Testing

### Ejecutar Tests
```bash
# Tests unitarios
mvn test

# Tests con coverage
mvn clean test jacoco:report

# Ver reporte de coverage
open target/site/jacoco/index.html
```

### Cobertura de Tests
- **Tests Unitarios**: 93 tests ejecutándose
- **Cobertura**: ~70% del código
- **Sin tests de integración** (simplificado para la prueba técnica)

## 🏗️ Arquitectura

```
src/
├── main/java/com/andesairlines/checkin_api/
│   ├── airplane/          # Gestión de aviones y asientos
│   ├── common/           # Excepciones y respuestas comunes
│   ├── flight/           # Lógica de vuelos y check-in
│   └── passenger/        # Gestión de pasajeros
├── main/resources/
│   ├── application.yml   # Configuración principal
│   ├── application-dev.yml
│   └── application-prod.yml
└── test/                 # Tests unitarios e integración
```

## 🔧 Stack Tecnológico

- **Framework**: Spring Boot 3.4
- **Base de Datos**: MySQL (producción) / H2 (desarrollo)
- **ORM**: Spring Data JPA + Hibernate
- **Testing**: JUnit 5, Mockito
- **Documentación**: OpenAPI 3 (Swagger)
- **Build**: Maven
- **Deployment**: Railway

## 🎯 Reglas de Negocio Implementadas

### Asignación Automática de Asientos
1. **Menores acompañados**: Los menores de edad se asientan junto a adultos de su mismo grupo de compra
2. **Asientos consecutivos**: Se priorizan asientos consecutivos para grupos familiares
3. **Tipos de asiento**: Se respetan las clases de servicio (Business, Economy, etc.)
4. **Disponibilidad**: Solo se asignan asientos disponibles

### Validaciones
- Verificación de existencia de vuelo
- Validación de tipos de asiento compatibles
- Control de disponibilidad de asientos
- Manejo de errores con códigos HTTP apropiados

## 📝 Funcionalidades Implementadas

### ✅ Completadas
- [x] Check-in automatizado con asignación inteligente de asientos
- [x] Reasignación manual de asientos específicos
- [x] Consulta de vuelos con información completa de pasajeros
- [x] Validaciones de negocio completas
- [x] Manejo centralizado de excepciones
- [x] Tests unitarios (93 tests ejecutándose)
- [x] Documentación API con Swagger
- [x] Deployment en Railway
- [x] Configuración multi-ambiente (dev/prod)

### 🏗️ Arquitectura
- **Patrón**: Controller → Service → Repository
- **Separación de responsabilidades**: Servicios especializados por funcionalidad
- **Error handling**: GlobalExceptionHandler centralizado
- **Testing**: Estrategia de tests unitarios con mocks
