# 👤 Perfil del Ingeniero — Docente de Desarrollo Backend

## Información General

| Campo            | Detalle                                                  |
|------------------|----------------------------------------------------------|
| **Rol**          | Ingeniero de Software Backend · Docente Universitario    |
| **Institución**  | Universidad de San Buenaventura Cali (USB Cali)          |
| **Área**         | Ingeniería de Sistemas / Desarrollo de Software          |
| **Especialidad** | Desarrollo Backend con Java y Spring Boot                |
| **Nivel**        | Senior                                                   |

---

## 🧠 Competencias Técnicas

### Lenguajes y Plataformas
- **Java 25** — Lenguaje principal de desarrollo backend
- **SQL** — Consultas avanzadas, procedimientos almacenados e índices en PostgreSQL

### Frameworks y Librerías
- **Spring Boot 4.x** — Framework principal para creación de APIs RESTful y servicios empresariales
- **Spring Data JPA / Hibernate** — ORM para mapeo objeto-relacional y gestión de persistencia
- **Spring Web MVC** — Arquitectura MVC para exposición de endpoints REST
- **Lombok** — Reducción de código boilerplate: `@Data`, `@Builder`, `@AllArgsConstructor`, etc.
- **Maven** — Gestión de dependencias y ciclo de vida del proyecto

### Bases de Datos
- **PostgreSQL** — Motor principal de base de datos relacional
  - Diseño de esquemas con constraints, índices, claves foráneas y checks
  - Configuración de conexión JDBC: `jdbc:postgresql://localhost:5432/ecommerce_usb_db`
- Modelado de datos con relaciones `@ManyToOne`, `@OneToMany`, `@ManyToMany`
- Uso de tipos avanzados: `numeric(12,2)`, `text`, `OffsetDateTime`, `LocalDate`

### Arquitectura y Patrones de Diseño
- **Arquitectura en capas** (Controller → Service → Repository → Model)
- **Patrón Repository** con Spring Data JPA
- **DTO Pattern** — Separación entre modelos de dominio y objetos de transferencia
- **Mapper Pattern** — Conversión entre entidades y DTOs
- **Builder Pattern** — Construcción de objetos con Lombok `@Builder`
- **Enumeraciones de estado** — Control de flujos con `Enum` (ej: `OrderStatus`, `CartStatus`, `PaymentStatus`)

### Herramientas de Desarrollo
- **Spring DevTools** — Recarga en caliente para agilizar el desarrollo
- **IntelliJ IDEA** — IDE principal
- **Git** — Control de versiones
- **Postman** — Pruebas de APIs REST

---

## 🎓 Rol Docente — Materias que Enseña

Como docente universitario, este ingeniero dicta materias orientadas al desarrollo backend moderno, usando el presente proyecto **ecommerceusb** como caso de estudio práctico y real en el aula.

### Materias Principales

| Materia                              | Contenidos Clave                                                                                     |
|--------------------------------------|------------------------------------------------------------------------------------------------------|
| **Programación Orientada a Objetos** | Clases, herencia, encapsulamiento, polimorfismo en Java. Uso de Lombok como herramienta moderna.    |
| **Bases de Datos I**                 | Modelo relacional, SQL, diseño de esquemas, normalización, constraints e índices en PostgreSQL.      |
| **Bases de Datos II**                | JPA/Hibernate, ORM, relaciones entre entidades, JPQL, transacciones.                                |
| **Desarrollo de Software Backend**   | APIs REST con Spring Boot, arquitectura en capas, DTOs, manejo de excepciones, validaciones.        |
| **Arquitectura de Software**         | Patrones de diseño (Repository, Builder, Mapper), principios SOLID, separación de concerns.         |
| **Ingeniería de Software**           | Ciclo de vida del software, gestión con Maven, pruebas unitarias y de integración con Spring Test.   |

### Enfoque Pedagógico
- Aprendizaje basado en proyectos reales (Project-Based Learning)
- El proyecto **ecommerceusb** sirve como caso de estudio de principio a fin
- Se instruye sobre buenas prácticas: nomenclatura, estructura de paquetes, control de versiones
- Uso de IntelliJ IDEA y herramientas profesionales del ecosistema Java

---

## 📦 Proyecto Actual: `ecommerceusb`

### Descripción
Backend RESTful de una plataforma de comercio electrónico, construido con Spring Boot y PostgreSQL. Sirve como proyecto pedagógico para los estudiantes de la USB Cali, exponiendo una arquitectura empresarial completa desde el modelo de datos hasta los endpoints de la API.

### Coordenadas del Proyecto

| Campo              | Valor                              |
|--------------------|------------------------------------|
| **GroupId**        | `co.edu.usbcali`                   |
| **ArtifactId**     | `ecommerceusb`                     |
| **Versión**        | `0.0.1-SNAPSHOT`                   |
| **Spring Boot**    | `4.0.3-SNAPSHOT`                   |
| **Java**           | `25`                               |
| **Base de Datos**  | PostgreSQL — `ecommerce_usb_db`    |
| **Puerto BD**      | `5432`                             |

### Arquitectura de Paquetes

```
co.edu.usbcali.ecommerceusb
├── model/               # Entidades JPA (tablas de la base de datos)
│   ├── enums/           # Enumeraciones de estado del dominio
│   └── ...
├── dto/                 # Objetos de Transferencia de Datos (Request / Response)
├── mapper/              # Conversores entre entidades y DTOs
├── repository/          # Interfaces de acceso a datos (Spring Data JPA)
├── service/             # Interfaces de lógica de negocio
│   └── impl/            # Implementaciones de los servicios
└── (controller/)        # Capa de exposición REST (en desarrollo)
```

### Dominio del Negocio — Entidades Principales

| Entidad               | Descripción                                                                 |
|-----------------------|-----------------------------------------------------------------------------|
| `User`                | Usuarios registrados con tipo de documento, país, email único y auditoría  |
| `DocumentType`        | Catálogo de tipos de documento (CC, Pasaporte, NIT, etc.)                  |
| `Product`             | Catálogo de productos disponibles en la tienda                              |
| `Category`            | Categorías para clasificar productos                                        |
| `ProductCategory`     | Relación muchos a muchos entre `Product` y `Category`                      |
| `Inventory`           | Stock actual de cada producto                                               |
| `InventoryMovement`   | Registro histórico de entradas y salidas del inventario                    |
| `Cart`                | Carrito de compras asociado a un usuario                                   |
| `CartItem`            | Ítem individual dentro de un carrito                                       |
| `Order`               | Pedido generado por un usuario (estados: `CREATED`, `PAID`, `CANCELLED`)  |
| `OrderItem`           | Ítem individual dentro de una orden                                        |
| `Payment`             | Registro del pago asociado a una orden                                     |

### Estados del Dominio (Enumeraciones)

| Enum                     | Valores                          | Uso                                          |
|--------------------------|----------------------------------|----------------------------------------------|
| `CartStatus`             | *(por definir)*                  | Estado del carrito de compras                |
| `OrderStatusCheck`       | `CREATED`, `PAID`, `CANCELLED`  | Ciclo de vida de un pedido                   |
| `PaymentStatusCheck`     | *(por definir)*                  | Estado del proceso de pago                   |
| `InventoryMovementTypes` | *(por definir)*                  | Tipo de movimiento: entrada o salida         |

### Dependencias del `pom.xml`

| Dependencia                         | Propósito                                              |
|-------------------------------------|--------------------------------------------------------|
| `spring-boot-starter-webmvc`        | Exposición de APIs REST con Spring MVC                 |
| `spring-boot-starter-data-jpa`      | Persistencia con JPA / Hibernate                       |
| `postgresql` (runtime)              | Driver JDBC para conectarse a PostgreSQL               |
| `lombok`                            | Reducción de boilerplate con anotaciones               |
| `spring-boot-devtools` (runtime)    | Recarga automática en desarrollo                       |
| `spring-boot-starter-data-jpa-test` | Pruebas de la capa de persistencia                     |
| `spring-boot-starter-webmvc-test`   | Pruebas de controladores REST                          |

### Configuración de Conexión (`application.properties`)

```properties
spring.application.name=ecommerceusb
spring.datasource.url=jdbc:postgresql://localhost:5432/ecommerce_usb_db
spring.datasource.driver-class-name=org.postgresql.Driver
```

---

## 🚀 Cómo Levantar el Proyecto

### Prerrequisitos
1. JDK 25 instalado y configurado en `JAVA_HOME`
2. Maven 3.9+ disponible en el `PATH`
3. PostgreSQL corriendo localmente en el puerto `5432`
4. Base de datos `ecommerce_usb_db` creada en PostgreSQL

### Comandos

```bash
# Clonar el repositorio y entrar al proyecto
cd ecommerceusb

# Compilar y ejecutar con Maven Wrapper
./mvnw spring-boot:run

# O con Maven instalado
mvn spring-boot:run

# Ejecutar pruebas
./mvnw test
```

---

## 📚 Valor Educativo del Proyecto

Este proyecto está diseñado para que los estudiantes aprendan, de manera progresiva:

1. **Modelado de datos** — Diseño de entidades con JPA, relaciones, constraints e índices
2. **Capa de repositorio** — Uso de `JpaRepository` y consultas personalizadas
3. **Lógica de negocio** — Separación con interfaces `Service` e implementaciones `ServiceImpl`
4. **DTOs y Mappers** — Cómo aislar el modelo de dominio del contrato de la API
5. **API REST** — Construcción de endpoints con Spring Web MVC
6. **Buenas prácticas** — Arquitectura limpia, nomenclatura, y estructura de paquetes profesionales
