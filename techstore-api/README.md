# TechStore API

Microservicio RESTful desarrollado en Java con Spring Boot para la tienda ficticia **TechStore Chile**.

El proyecto permite gestionar productos mediante una API protegida con JWT, usando arquitectura por capas, PostgreSQL en Docker, Maven, Docker Compose y pruebas con Postman.

Este proyecto fue desarrollado como parte de la **Formativa-Sumativa 2** del curso de Java / Spring Boot.


## Índice

- [Creadoras del proyecto](#creadoras-del-proyecto)
- [Objetivo del proyecto](#objetivo-del-proyecto)
- [Funcionalidades principales](#funcionalidades-principales)
- [Arquitectura del proyecto](#arquitectura-del-proyecto)
- [Tecnologías utilizadas](#tecnologías-utilizadas)
- [Base de datos](#base-de-datos)
- [Usuarios de prueba para login](#usuarios-de-prueba-para-login)
- [Generar el archivo .jar](#generar-el-archivo-jar)
- [Ejecutar con Docker Compose](#ejecutar-con-docker-compose)
- [Endpoints principales](#endpoints-principales)
- [Uso del token en Postman](#uso-del-token-en-postman)
- [Evidencias de prueba](#evidencias-de-prueba)
- [Comandos útiles](#comandos-útiles)
- [Flujo recomendado para ejecutar el proyecto](#flujo-recomendado-para-ejecutar-el-proyecto)
- [Presentación final del proyecto](#presentación-final-del-proyecto)
- [Estado final del proyecto](#estado-final-del-proyecto)

## Creadoras del proyecto

- Victoria Bustos
- Jael Yapur

---

## Objetivo del proyecto

Construir un microservicio funcional que permita administrar productos de una tienda ficticia, aplicando los contenidos trabajados en clases:

- Spring Boot.
- API REST.
- Arquitectura por capas.
- Seguridad con JWT.
- Base de datos relacional.
- PostgreSQL con Docker.
- Maven.
- Docker Compose.
- Git y GitHub.
- Pruebas con Postman.

---

## Funcionalidades principales

- Login con JWT.
- Listar productos activos.
- Crear productos.
- Modificar productos.
- Eliminar productos mediante borrado lógico.
- Persistencia en PostgreSQL.
- Ejecución mediante Docker Compose.

---

## Arquitectura del proyecto

El proyecto sigue una arquitectura por capas:

```text
Controller → Service → Repository → Base de datos
        ↓
      DTO / Model
        ↓
     Security JWT
```

### Capas principales

- `controller`: recibe las peticiones HTTP.
- `service`: contiene la lógica de negocio.
- `repository`: conecta con la base de datos mediante JPA.
- `model`: contiene la entidad `Producto`.
- `dto`: contiene los objetos de transferencia de datos.
- `security`: contiene la configuración de JWT, filtros y seguridad.

---

## Tecnologías utilizadas

- Java 21
- Spring Boot
- Spring Web
- Spring Data JPA
- Spring Security
- JWT
- PostgreSQL
- Docker
- Docker Compose
- Maven
- Postman
- Git / GitHub

---

## Base de datos

El proyecto utiliza PostgreSQL mediante Docker.

### Datos de conexión

```text
Base de datos: techstore
Usuario: admin
Contraseña: admin123
Puerto local: 5433
Puerto interno Docker: 5432
```

> Importante: la contraseña `admin123` corresponde a PostgreSQL, no al login de la API.

---

## Usuarios de prueba para login

```text
Usuario: admin
Contraseña: admin
Rol: ADMIN
```

```text
Usuario: user
Contraseña: password
Rol: USER
```

---

## Generar el archivo `.jar`

Desde la raíz del proyecto ejecutar:

```bash
./mvnw package -DskipTests
```

En Windows CMD o PowerShell:

```bash
mvnw.cmd package -DskipTests
```

El archivo `.jar` se genera dentro de la carpeta:

```text
target/
```

---

## Ejecutar con Docker Compose

Desde la raíz del proyecto ejecutar:

```bash
docker compose down
docker compose up --build
```

Esto levanta:

- Contenedor de PostgreSQL.
- Contenedor del microservicio Spring Boot.

Para verificar los contenedores activos:

```bash
docker ps
```

Deben aparecer:

```text
techstore_api
techstore_db_compose
```

---

## Endpoints principales

### Login

```http
POST http://localhost:8080/api/auth/login
```

Body:

```json
{
  "username": "admin",
  "password": "admin"
}
```

Respuesta esperada:

```json
{
  "token": "TOKEN_GENERADO",
  "tipo": "Bearer",
  "username": "admin"
}
```

---

### Listar productos

```http
GET http://localhost:8080/api/productos
```

Requiere token JWT.

---

### Crear producto

```http
POST http://localhost:8080/api/productos
```

Requiere token JWT.

Body:

```json
{
  "nombre": "Mouse Logitech",
  "descripcion": "Mouse inalámbrico",
  "precio": 12990,
  "stock": 20,
  "categoria": "Computación",
  "activo": true
}
```

Respuesta esperada:

```text
201 Created
```

---

### Modificar producto

```http
PUT http://localhost:8080/api/productos/1
```

Requiere token JWT.

Body:

```json
{
  "nombre": "Mouse Logitech Actualizado",
  "descripcion": "Mouse inalámbrico USB",
  "precio": 14990,
  "stock": 15,
  "categoria": "Computación",
  "activo": true
}
```

Respuesta esperada:

```text
200 OK
```

---

### Eliminar producto

```http
DELETE http://localhost:8080/api/productos/1
```

Requiere token JWT.

Respuesta esperada:

```text
204 No Content
```

El sistema no elimina físicamente el producto de la base de datos. Solo cambia el campo:

```text
activo = false
```

Esto corresponde a un **borrado lógico**.

---

## Uso del token en Postman

Después de hacer login:

1. Copiar el valor del campo `token`.
2. Ir al endpoint protegido.
3. Abrir la pestaña `Auth`.
4. Seleccionar `Bearer Token`.
5. Pegar el token.
6. Enviar la petición.

---

## Comandos útiles

Ver contenedores activos:

```bash
docker ps
```

Ver todos los contenedores:

```bash
docker ps -a
```

Detener Docker Compose:

```bash
docker compose down
```

Levantar nuevamente el proyecto:

```bash
docker compose up --build
```

Entrar a PostgreSQL:

```bash
docker exec -it techstore_db_compose psql -U admin -d techstore
```

Ver tablas:

```sql
\dt
```

Salir de PostgreSQL:

```sql
\q
```

---

## Flujo recomendado para ejecutar el proyecto

```bash
git pull origin dev
./mvnw package -DskipTests
docker compose down
docker compose up --build
```

Luego probar en Postman:

```http
POST http://localhost:8080/api/auth/login
```

---

## Presentación final del proyecto

El proyecto fue cerrado con una presentación final, donde se explicó:

- El contexto de TechStore Chile.
- El objetivo del microservicio.
- La arquitectura por capas.
- El funcionamiento de JWT.
- Los endpoints principales.
- El borrado lógico.
- La dockerización del proyecto.
- Las pruebas realizadas con Postman.
- Los resultados obtenidos.

La presentación permitió mostrar que la API se encuentra funcional, protegida con JWT, conectada a PostgreSQL y ejecutándose correctamente mediante Docker Compose.

---

## Estado final del proyecto

- API REST funcional.
- JWT funcionando.
- CRUD de productos funcionando.
- Borrado lógico implementado.
- PostgreSQL corriendo con Docker.
- Proyecto dockerizado.
- Docker Compose funcionando.
- Pruebas realizadas con Postman.
- Proyecto subido a GitHub.
- Proyecto cerrado con presentación final.