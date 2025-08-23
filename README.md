# ✈️ Andes Airlines - Check-in API

Sistema de check-in automatizado que simula el proceso de asignación de asientos para vuelos comerciales. La API implementa lógica de negocio para asignar asientos de manera inteligente, respetando restricciones como menores acompañados y tipos de asiento.

## 🎯 ¿Qué hace esta API?

Esta API simula un sistema de check-in automático para aerolíneas que:

1. **Asigna asientos automáticamente** cuando consultas un vuelo
2. **Respeta reglas de negocio**:
   - Menores de edad deben estar junto a un adulto de su grupo de compra
   - Asientos consecutivos para grupos cuando sea posible
   - Respeta clases de asiento (Business, Economy, etc.)
3. **Permite reasignación manual** de asientos específicos
4. **Devuelve información completa** del vuelo con todos los pasajeros y sus asientos

## 🚀 API en Producción

### 🌐 Acceso Directo
- **API Base URL**: `https://checkin-api-production.up.railway.app/api`
- **Swagger UI**: `https://checkin-api-production.up.railway.app/api/swagger-ui.html`
- **Documentación OpenAPI**: `https://checkin-api-production.up.railway.app/api/api-docs`

### 📋 Endpoints Disponibles

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
curl -X PUT "https://checkin-api-production.up.railway.app/api/flights/1/passengers/144/seat?seatRow=2&seatColumn=A"
```

**¿Qué hace?**
- Permite reasignar un asiento específico a un pasajero
- Valida que el asiento exista y esté disponible
- Verifica que el tipo de asiento sea compatible con el pasajero

## 🧪 Probar la API

### Usando Swagger UI (Recomendado)
1. Ve a: `https://checkin-api-production.up.railway.app/api/swagger-ui.html`
2. Expande el endpoint `GET /flights/{flightId}/passengers`
3. Haz clic en "Try it out"
4. Ingresa `1` como flightId
5. Haz clic en "Execute"

### Usando curl
```bash
# Consultar vuelo 1
curl -X GET "https://checkin-api-production.up.railway.app/api/flights/1/passengers"

# Reasignar asiento
curl -X PUT "https://checkin-api-production.up.railway.app/api/flights/1/passengers/144/seat?seatRow=3&seatColumn=B"
```

### Casos de Prueba
- **Vuelo existente**: `flightId=1` → Devuelve 200 con datos
- **Vuelo inexistente**: `flightId=999` → Devuelve 404
- **Asiento inválido**: seatRow fuera de rango → Devuelve 400

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
