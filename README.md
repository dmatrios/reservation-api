# Reservation API 🏨🍽️🏊

Backend REST API para gestión de reservas (Hotel, Restaurante y Piscina).

Proyecto construido con **Spring Boot 3**, **Java 21**, **MySQL**, **Flyway**, **JWT**, y completamente **dockerizado**.

> Este repositorio contiene únicamente el backend.

---

# 🚀 Tecnologías

* Java 21
* Spring Boot 3
* Spring Security + JWT
* Spring Data JPA
* MySQL 8
* Flyway (migraciones)
* Docker & Docker Compose

---

# 🧠 Arquitectura

El proyecto sigue una arquitectura limpia basada en:

```
Controller → Service → Repository → Database
```

Organización por feature:

```
users/
reservations/
security/
shared/
```

Principios aplicados:

* DTOs para entrada y salida
* Manejo global de errores con `@RestControllerAdvice`
* Soft delete
* Validaciones con Bean Validation
* Seguridad stateless con JWT
* Roles: USER / ADMIN

---

# 🔐 Autenticación

La API utiliza JWT.

Header requerido para endpoints protegidos:

```
Authorization: Bearer <TOKEN>
```

Flujo:

1. Usuario se registra (queda INACTIVE).
2. Admin activa usuario.
3. Usuario puede iniciar sesión.
4. Token JWT se usa en cada request protegida.

---

# 🐳 Ejecutar con Docker (Recomendado)

Este proyecto ya está dockerizado.

## 1️⃣ Clonar repositorio

```bash
git clone <URL_DEL_REPO>
cd reservationapi
```

## 2️⃣ Crear archivo .env

En la raíz del proyecto crear un archivo `.env` con:

```
MYSQL_ROOT_PASSWORD=root123
MYSQL_DATABASE=reservationapi
MYSQL_USER=reservationapi
MYSQL_PASSWORD=reservationapi123

JWT_SECRET=TU_SECRET_BASE64

BOOTSTRAP_ADMIN_EMAIL=admin@local.com
BOOTSTRAP_ADMIN_PASSWORD=Admin12345*
BOOTSTRAP_ADMIN_FULL_NAME=Admin
BOOTSTRAP_ADMIN_DNI=00000000
BOOTSTRAP_ADMIN_PHONE=000000000

CORS_ALLOWED_ORIGINS=http://localhost:3000
```

## 3️⃣ Levantar contenedores

```bash
docker compose up -d --build
```

Esto levantará:

* MySQL (puerto 3307 en host)
* API (puerto 8080)

## 4️⃣ Verificar que todo esté corriendo

```bash
docker compose ps
```

La API estará disponible en:

```
http://localhost:8080
```

---

# 🛠 Ejecutar sin Docker (modo desarrollo)

Si prefieres correrlo directamente:

## 1️⃣ Configurar MySQL local

Crear base de datos `reservationapi`.

## 2️⃣ Ejecutar aplicación

```bash
./mvnw spring-boot:run
```

O en Windows:

```powershell
.\mvnw spring-boot:run
```

---

# 📦 Endpoints principales

## Auth

```
POST   /api/v1/auth/register
POST   /api/v1/auth/login
GET    /api/v1/auth/me
```

## Usuario

```
GET    /api/v1/reservations/mine
POST   /api/v1/reservations
PATCH  /api/v1/reservations/{id}/cancel
```

## Admin

```
GET    /api/v1/admin/users
PATCH  /api/v1/admin/users/{id}/status

GET    /api/v1/admin/reservations
PATCH  /api/v1/admin/reservations/{id}/confirm
PATCH  /api/v1/admin/reservations/{id}/delete

GET    /api/v1/admin/rooms
POST   /api/v1/admin/rooms
PUT    /api/v1/admin/rooms/{id}
PATCH  /api/v1/admin/rooms/{id}/status

GET    /api/v1/admin/slot-configs
POST   /api/v1/admin/slot-configs
PUT    /api/v1/admin/slot-configs/{id}
PATCH  /api/v1/admin/slot-configs/{id}/active
```

---

# ⚙️ Variables de Entorno Importantes

| Variable               | Descripción                       |
| ---------------------- | --------------------------------- |
| JWT_SECRET             | Clave para firmar tokens          |
| CORS_ALLOWED_ORIGINS   | Dominio permitido para frontend   |
| SPRING_PROFILES_ACTIVE | Perfil activo (dev, prod, docker) |
| DB_HOST / DB_PORT      | Configuración de base de datos    |

---

# 🌍 Preparado para Producción

El proyecto está listo para:

* Deploy en AWS (ECS / EC2 / Elastic Beanstalk)
* Base de datos RDS
* Frontend en Vercel
* Configuración CORS mediante variable de entorno

No hay valores hardcodeados para producción.

---

# 📌 Buenas Prácticas Implementadas

* Migraciones versionadas con Flyway
* Manejo centralizado de errores
* Validaciones consistentes
* Seguridad desacoplada
* Docker multi-stage build
* Separación clara por módulos

---

# 🧪 Testing Manual

Puedes probar la API con:

* Postman
* Insomnia
* Thunder Client

Primero registrar usuario, luego activarlo como admin y probar flujo completo.

---

# 👨‍💻 Autor

Proyecto desarrollado como parte de un sistema de reservas full-stack preparado para producción y despliegue en la nube.

---

Si este proyecto te sirve como referencia, puedes usarlo como base para:

* Sistemas de reservas
* Gestión hotelera
* APIs seguras con JWT
* Arquitecturas backend limpias

---
