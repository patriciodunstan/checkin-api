# ✈️ Andes Airlines - Check-in API

Sistema de check-in automatizado para aerolínea con asignación inteligente de asientos y gestión de vuelos.

## 🚀 Características Principales

- **Check-in Automatizado**: Proceso completo de check-in con asignación automática de asientos
- **Asignación Manual**: Capacidad de reasignar asientos manualmente
- **Gestión de Vuelos**: Consulta de vuelos con información completa de pasajeros
- **API REST**: Endpoints documentados con Swagger/OpenAPI
- **Base de Datos**: Soporte para MySQL en producción y H2 para desarrollo/testing
- **Containerización**: Docker ready para deployment
- **CI/CD**: Pipeline automatizado con GitHub Actions
- **Testing**: Cobertura completa con tests unitarios e integración

## 📋 Requisitos

- **Java 21** o superior
- **Maven 3.8+**
- **MySQL 8.0+** (para producción)
- **Docker** (opcional, para containerización)

## 🛠️ Instalación y Configuración

### 1. Clonar el Repositorio
```bash
git clone <repository-url>
cd checkin-api
```

### 2. Configurar Base de Datos

#### Desarrollo (H2 - automático)
```bash
mvn spring-boot:run
```

#### Producción (MySQL)
```bash
# Crear base de datos
mysql -u root -p
CREATE DATABASE checkin_db;

# Configurar variables de entorno
export DB_HOST=localhost
export DB_PORT=3306
export DB_NAME=checkin_db
export DB_USERNAME=your_username
export DB_PASSWORD=your_password
```

### 3. Ejecutar la Aplicación
```bash
# Desarrollo
mvn spring-boot:run

# Producción
mvn clean package
java -jar target/checkin-api-0.0.1-SNAPSHOT.jar
```

## 🌐 Endpoints API

### Gestión de Vuelos

#### Obtener Vuelo con Pasajeros
```http
GET /flights/{flightId}/passengers
```
**Respuesta:**
```json
{
  "code": 200,
  "data": {
    "flightId": 1,
    "airplaneId": 100,
    "takeoffDateTime": 1234567890,
    "landingDateTime": 1234567890,
    "takeoffAirport": "SCL",
    "landingAirport": "LIM",
    "passengers": [...]
  }
}
```

#### Asignar Asiento Manualmente
```http
PUT /flights/{flightId}/passengers/{passengerId}/seat?seatRow=10&seatColumn=A
```
**Respuesta:**
```json
{
  "code": 200,
  "data": {
    "passengerId": 1,
    "name": "John Doe",
    "dni": "12345678",
    "age": 30,
    "country": "Chile",
    "boardingPass": "BP001",
    "seatTypeId": 1,
    "seatId": 15
  }
}
```

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
- **Tests Unitarios**: 16 tests
- **Tests de Integración**: 2 tests  
- **Cobertura Mínima**: 70%

## 🐳 Docker

### Construir Imagen
```bash
docker build -t checkin-api .
```

### Ejecutar Container
```bash
docker run -p 8080:8080 \
  -e DB_HOST=host.docker.internal \
  -e DB_USERNAME=root \
  -e DB_PASSWORD=password \
  checkin-api
```

## 🚀 Deployment

### Render.com (Automático)
El proyecto está configurado para deployment automático en Render.com mediante GitHub Actions:

1. **Push a main/master** → Ejecuta tests automáticamente
2. **Tests exitosos** → Deploy automático a producción
3. **Tests fallan** → Deployment se cancela

### Variables de Entorno Requeridas
```env
DB_HOST=your-mysql-host
DB_PORT=3306
DB_NAME=checkin_db
DB_USERNAME=your-username
DB_PASSWORD=your-password
RENDER_SERVICE_ID=your-service-id
RENDER_API_KEY=your-api-key
```

## 📚 Documentación API

Una vez ejecutada la aplicación, acceder a:
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

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
- **Base de Datos**: MySQL 8.0 / H2 (testing)
- **ORM**: Spring Data JPA + Hibernate
- **Testing**: JUnit 5, Mockito, Spring Boot Test
- **Documentación**: OpenAPI 3 (Swagger)
- **Build**: Maven
- **Containerización**: Docker
- **CI/CD**: GitHub Actions
- **Deployment**: Render.com

## 👥 Contacto

**Desarrollador**: Patricio Dunstan  
**Email**: [tu-email]  
**LinkedIn**: [tu-linkedin]

---

## 📝 Notas para la Prueba Técnica

### Funcionalidades Implementadas ✅
- [x] Check-in automatizado con asignación de asientos
- [x] Reasignación manual de asientos
- [x] Consulta de vuelos con pasajeros
- [x] Validaciones de negocio (asientos válidos, disponibilidad)
- [x] Manejo de excepciones y errores
- [x] Tests unitarios completos (18 tests)
- [x] Documentación API con Swagger
- [x] Pipeline CI/CD automatizado
- [x] Containerización con Docker
- [x] Configuración multi-ambiente

### Decisiones de Diseño 🎯
1. **Arquitectura en Capas**: Controller → Service → Repository
2. **Separación de Responsabilidades**: Servicios específicos para check-in y asignación manual
3. **Validaciones**: Tanto a nivel de controlador como de servicio
4. **Testing Strategy**: Tests unitarios con mocks + tests de integración
5. **Configuration**: Profiles separados para dev/test/prod
6. **Error Handling**: GlobalExceptionHandler centralizado

### Próximas Mejoras 🚀
- [ ] Autenticación y autorización (JWT)
- [ ] Cache con Redis
- [ ] Métricas con Micrometer
- [ ] Logging estructurado
- [ ] Rate limiting
- [ ] Versionado de API
