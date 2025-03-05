# Flight Booking API 🛫

A RESTful API for managing flight bookings, user authentication, and profile management. Built with Spring Boot, PostgreSQL, Redis, and JWT for secure authentication. Includes features like two-factor authentication (2FA), flight management, seat reservations, and user profile customization.

## 🚀 Technologies Used

- **Backend**: Spring Boot, Spring Data JPA
- **Database**: PostgreSQL
- **Caching**: Redis
- **Authentication**: JWT (JSON Web Tokens), Spring Security
- **Testing**: JUnit 5, Mockito
- **Containerization**: Docker

---

## Deployment with Docker

### Prerequisites

- Install [Docker](https://docs.docker.com/get-docker/) and [Docker Compose](https://docs.docker.com/compose/install/).

### Step-by-Step Guide

1. **Clone the repository**:

   ```bash
   git clone https://github.com/wubbzy25/api-reserva-vuelo.git
   cd your-repo-directory
   ```

2. **Build the Docker image**:

   ```bash
   docker-compose build
   ```

3. **Run the application**:

   ```bash
   docker-compose up -d
   ```

4. **Verify containers are running**:
   ```bash
   docker ps
   ```
   ![Docker Containers Running](https://via.placeholder.com/600x400?text=Docker+Containers+Running)

The API will be accessible at `http://localhost:8080`.

---

## API Documentation

### Authentication

All endpoints under `#AUTH` require no authentication unless specified.

---

## `AUTH`

##### **Register User**

`POST /api/v1/auth/register`  
**Request Body**:

```json
{
  "primer-nombre": "John",
  "segundo-nombre": "",
  "primer-apellido": "Doe",
  "segundo-apellido": "",
  "email": "john@example.com",
  "telefono": "1234567890",
  "fecha_nacimiento": "1990-01-01",
  "genero": "MALE",
  "contraseña": "securePassword123"
}
```

**Success Response** (201 Created)

```json
{
  "timeStamp": "2025-03-04 17:27:39",
  "code": "P-201",
  "idUsuario": 2,
  "message": "Usuario creado correctamente",
  "url": "/api/v1/auth/register"
}
```

**Error Response** (409 Conflict)

```json
{
  "timeStamp": "2025-03-04 17:27:57",
  "code": "p-500",
  "message": "El usuario ya se encuentra registrado en la aplicacion",
  "url": "/api/v1/auth/register"
}
```

##### **Forgot-Password**

`POST /api/v1/auth/forgot-password`
**Request Body**

```json
{
  "email": "john@gmail.com"
}
```

**Success Response** (200 OK)

```json
{
  "timeStamp": "2025-03-04 17:31:30",
  "code": "P-200",
  "message": "Se ha enviado un codigo de verificacion al correo electronico registrado",
  "url": "/api/v1/auth/forgot-password"
}
```

**Error Response** (404 Not Found)

```json
{
  "timeStamp": "2025-03-04 17:34:43",
  "code": "P-404",
  "message": "El usuario no se encuentra registrado en la aplicacion",
  "url": "/api/v1/auth/forgot-password"
}
```

##### **Login**

`POST /api/v1/auth/login`  
**Request Body**:

```json
{
  "email": "john@gmail.com",
  "contraseña": "securePassword123"
}
```

**Success Response** (200 OK)

```json
{
  "timeStamp": "2025-03-04 17:37:24",
  "code": "P-200",
  "idUsuario": 1,
  "message": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ3dXViemlAZ21haWwuY29tIiwiaWF0IjoxNzQxMTA5ODQ0LCJleHAiOjE3NDE3MTQ2NDR9.mArKT5tUAhC3nwYKKkFuVusp0ih-pafrVaD-d3f-H0JsOInamdf7PF4TETyKAW-X_ZHy-EzvtoLHzT0aslaQmQ",
  "url": "/api/v1/auth/login"
}
```

**Error Response** (404 Not Found)

```json
{
  "timeStamp": "2025-03-04 17:37:44",
  "code": "P-404",
  "message": "El usuario no se encuentra registrado en la aplicacion",
  "url": "/api/v1/auth/login"
}
```

**Error Response** (401 Unauthorized)

```json
{
  "timeStamp": "2025-03-04 17:38:54",
  "code": "P-401",
  "message": "Contraseña incorrecta",
  "url": "/api/v1/auth/login"
}
```

## Administradores

**Succcess Response** (200 ok)

```json
{
  "timeStamp": "2025-03-04 14:13:51",
  "code": "P-200",
  "message": "El usuario se ha logeado pero, tiene activado el 2FA, debe introducir el codigo",
  "url": "/api/v1/auth/login"
}
```

**Error Response** (401 Unauthorized)

```json
{
  "timeStamp": "2025-03-04 14:13:51",
  "code": "P-401",
  "message": "Los administradores deben tener el 2FA Activado para poder iniciar sesio",
  "url": "/api/v1/auth/login"
}
```

##### **Verify Code**

`POST /api/v1/auth/verify-code`
**Request Body**

```json
{
  "email": "john@gmail.com",
  "codigo": "123456"
}
```

**Succcess Response** (200 ok)

```json
{
  "timeStamp": "2025-03-04 14:13:51",
  "code": "P-200",
  "message": "El codigo de verificacion fue validado correctamente",
  "url": "/api/v1/auth/verify-code"
}
```

**Error Response** (404 Not Found)

```json
{
  "timeStamp": "2025-03-04 17:37:44",
  "code": "P-404",
  "message": "No se ha enviado ningun codigo",
  "url": "/api/v1/auth/verify-code"
}
```

**Error Response** (400 Bad Request)

```json
{
  "timeStamp": "2025-03-04 17:37:44",
  "code": "P-400",
  "message": "El codigo no es valido",
  "url": "/api/v1/auth/verify-code"
}
```

#### **Change Password**

`POST /api/v1/auth/change-password`
**Request Body**

```json
{
  "email": "john@gmail.com"
}
```

**Success Response** (200 OK)

```json
{
  "timeStamp": "2025-03-04 17:37:44",
  "code": "P-200",
  "message": "Contraseña cambiada correctamente",
  "url": "/api/v1/auth/change-password"
}
```

**Error Response** (404 NOT FOUND)

```json
{
  "timeStamp": "2025-03-04 17:37:44",
  "code": "P-404",
  "message": "El usuario no se encuentra registrado en la aplicacion",
  "url": "/api/v1/auth/change-password"
}
```

#### **Setup 2FA**

`GET /api/v1/auth/2FA/setup?id_usuario=1`

**Success Response** (200 OK)

```json
{
  "timeStamp": "2025-03-04 17:37:44",
  "code": "P-200",
  "message": "Se envio el qr para activar el 2AF al email registrado",
  "url": "/api/v1/auth/2FA/setup"
}
```

**Error Response** (400 Bad Request)

```json
{
  "timeStamp": "2025-03-04 17:37:44",
  "code": "P-400",
  "message": "Ya tienes seteado el 2FA",
  "url": "/api/v1/auth/2FA/setup"
}
```

**Error Response** (404 Not Found)

```json
{
  "timeStamp": "2025-03-04 17:37:44",
  "code": "P-404",
  "message": "El usuario no se encuentra registrado en la aplicacion",
  "url": "/api/v1/auth/2FA/setup"
}
```

#### **Verify 2FA**

`GET /api/v1/auth/2FA/verify?id_usuario=1`

**Success Response**

```json
{
  "timeStamp": "2025-03-04 17:37:24",
  "code": "P-200",
  "idUsuario": 1,
  "message": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ3dXViemlAZ21haWwuY29tIiwiaWF0IjoxNzQxMTA5ODQ0LCJleHAiOjE3NDE3MTQ2NDR9.mArKT5tUAhC3nwYKKkFuVusp0ih-pafrVaD-d3f-H0JsOInamdf7PF4TETyKAW-X_ZHy-EzvtoLHzT0aslaQmQ",
  "url": "/api/v1/auth/2FA/verify"
}
```

**Error Response** (404 Not Found)

```json
{
  "timeStamp": "2025-03-04 17:37:44",
  "code": "P-404",
  "message": "El usuario no se encuentra registrado en la aplicacion",
  "url": "/api/v1/auth/2FA/verify"
}
```

**Error Response** (400 Bad Request)

```json
{
  "timeStamp": "2025-03-04 17:37:44",
  "code": "P-400",
  "message": "No tienes activado el 2FA",
  "url": "/api/v1/auth/2FA/verify"
}
```

**Error Response** (401 Unauthorized)

```json
{
  "timeStamp": "2025-03-04 17:37:44",
  "code": "P-401",
  "message": "No estas autorizado para usar esto",
  "url": "/api/v1/auth/2FA/verify"
}
```

**Error Response** (400 Bad Request)

```json
{
  "timeStamp": "2025-03-04 17:37:44",
  "code": "P-400",
  "message": "El codigo de verificacion no es valido",
  "url": "/api/v1/auth/2FA/verify"
}
```

---

#### `#PROFILE`

_(Requires Authentication)_

##### **Upload Profile Image**

`POST /api/v1/profile/upload-image`
**Request Body**:

- Multipart file (JPEG/PNG only).

**Success Response** (200 OK)

```json
{
  "timeStamp": "2025-03-04 17:37:44",
  "code": "P-200",
  "message": "La imagen fue subida correctamente",
  "url": "/api/v1/profile/upload-image"
}
```

**Error Response** (400 Bad Request)

```json
{
  "timeStamp": "2025-03-04 17:37:44",
  "code": "P-400",
  "message": "El tipo de archivo no es válido. Sólo se permiten imágenes en formato JPEG o PNG.",
  "url": "/api/v1/profile/upload-image"
}
```

**Error Response** (400 Bad Request)

```json
{
  "timeStamp": "2025-03-04 17:37:44",
  "code": "P-400",
  "message": "Este archivo puede contener malware",
  "url": "/api/v1/profile/upload-image"
}
```

**Error Response** (400 Bad Request)

```json
{
  "timeStamp": "2025-03-04 17:37:44",
  "code": "P-400",
  "message": "Profile image not found",
  "url": "/api/v1/profile/upload-image"
}
```

##### **Get Profile**

`GET /api/v1/profile/1`
**Success Response** (200 OK)

```json
{
  "timeStamp": "2025-03-04T22:21:00Z",
  "code": "200",
  "id_usuario": 123456789,
  "url_imagen": "https://example.com/images/profile.jpg",
  "primer_nombre": "Carlos",
  "segundo_nombre": "Alberto",
  "primer_apellido": "Gomez",
  "segundo_apellido": "Hernandez",
  "nombre_completo": "Carlos Alberto Gomez Hernandez",
  "email": "carlos.gomez@gmail.com",
  "telefono": "3243456572",
  "fecha_nacimiento": "1990-01-15",
  "genero": "Masculino",
  "url": "/api/v1/profile"
}
```

**Error Response** (404 Not Found)

```json
{
  "timeStamp": "2025-03-04 17:37:44",
  "code": "P-404",
  "message": "El usuario no se encuentra registrado en la aplicacion",
  "url": "/api/v1/profile"
}
```

##### **Edit Profile**

`PUT /api/v1/profile/edit-profile/1`

**Success Response** (200 OK)

```json
{
  "timeStamp": "2025-03-04T22:21:00Z",
  "code": "200",
  "id_usuario": 123456789,
  "url_imagen": "https://example.com/images/profile.jpg",
  "primer_nombre": "Carlos",
  "segundo_nombre": "Alberto",
  "primer_apellido": "Gomez",
  "segundo_apellido": "Hernandez",
  "nombre_completo": "Carlos Alberto Gomez Hernandez",
  "email": "carlos.gomez@gmail.com",
  "telefono": "3243456572",
  "fecha_nacimiento": "1990-01-15",
  "genero": "Masculino",
  "url": "/api/v1/profile/edit-profile"
}
```

**Error Response** (404 Not Found)

```json
{
  "timeStamp": "2025-03-04 17:37:44",
  "code": "P-404",
  "message": "El usuario no se encuentra registrado en la aplicacion",
  "url": "/api/v1/profile/edit-profile"
}
```

---

#### `#VUELOS`

_(Requires Authentication. Admin-only endpoints marked with 🔒)_

##### **Create Flight (Admin Only)**

`POST /api/v1/vuelos/vuelo/crear` 🔒  
**Request Body**:

```json
{
  "aerolínea": "Delta",
  "numeroVuelo": "DL123",
  "tipoAvion": "Boeing 737",
  "origen": "JFK",
  "destino": "LAX",
  "fechaIda": "2023-12-25",
  "horaSalida": "14:30:00",
  "fechaVuelta": "2023-12-30",
  "horaVuelta": "18:00:00",
  "duracion": "5h 30m",
  "bussinessClass": 20,
  "economyClass": 150,
  "precioBussiness": 800,
  "precioEconomy": 300
}
```

**Success Response** (201 Created)

```json
{
  "timeStamp": "2025-03-04 17:37:44",
  "code": "P-201",
  "message": "Vuelo creado correctamente",
  "url": "/api/v1/vuelos/vuelo/crear"
}
```

**Error Response** (404 Bad Request)

```json
{
  "timeStamp": "2025-03-04 17:37:44",
  "code": "P-400",
  "message": "El vuelo ya existe",
  "url": "/api/v1/profile/edit-profile"
}
```

##### **Get Available Flights**

`GET /api/v1/vuelos?page=0&size=1`

**Success Response** (200 OK)

```json
[
  {
    "aerolínea": "Delta",
    "numeroVuelo": "DL123",
    "tipoAvion": "Boeing 737",
    "origen": "JFK",
    "destino": "LAX",
    "fechaIda": "2023-12-25",
    "horaSalida": "14:30:00",
    "fechaVuelta": "2023-12-30",
    "horaVuelta": "18:00:00",
    "duracion": "5h 30m",
    "bussinessClass": 20,
    "economyClass": 150,
    "precioBussiness": 800,
    "precioEconomy": 300
  },
  {
    "aerolínea": "American Airlines",
    "numeroVuelo": "AA456",
    "tipoAvion": "Airbus A320",
    "origen": "MIA",
    "destino": "ORD",
    "fechaIda": "2023-12-26",
    "horaSalida": "10:00:00",
    "fechaVuelta": "2023-12-31",
    "horaVuelta": "15:30:00",
    "duracion": "3h 15m",
    "bussinessClass": 15,
    "economyClass": 120,
    "precioBussiness": 750,
    "precioEconomy": 250
  },
  {
    "aerolínea": "United Airlines",
    "numeroVuelo": "UA789",
    "tipoAvion": "Boeing 777",
    "origen": "SFO",
    "destino": "HNL",
    "fechaIda": "2023-12-27",
    "horaSalida": "08:45:00",
    "fechaVuelta": "2024-01-01",
    "horaVuelta": "12:00:00",
    "duracion": "5h 45m",
    "bussinessClass": 30,
    "economyClass": 180,
    "precioBussiness": 900,
    "precioEconomy": 320
  },
  {
    "aerolínea": "Southwest",
    "numeroVuelo": "SW345",
    "tipoAvion": "Boeing 737",
    "origen": "DAL",
    "destino": "LAS",
    "fechaIda": "2023-12-28",
    "horaSalida": "09:30:00",
    "fechaVuelta": "2024-01-02",
    "horaVuelta": "11:45:00",
    "duracion": "2h 45m",
    "bussinessClass": 25,
    "economyClass": 140,
    "precioBussiness": 700,
    "precioEconomy": 280
  }
]
```

##### **Get Available Flight**

`GET /api/v1/vuelos/vuelo/1`

**Success Response** (200 OK)

```json
{
  "aerolínea": "Southwest",
  "numeroVuelo": "SW345",
  "tipoAvion": "Boeing 737",
  "origen": "DAL",
  "destino": "LAS",
  "fechaIda": "2023-12-28",
  "horaSalida": "09:30:00",
  "fechaVuelta": "2024-01-02",
  "horaVuelta": "11:45:00",
  "duracion": "2h 45m",
  "bussinessClass": 25,
  "economyClass": 140,
  "precioBussiness": 700,
  "precioEconomy": 280
}
```

**Error Response** (404 Not Found)

```json
{
  "timeStamp": "2025-03-04 17:37:44",
  "code": "P-404",
  "message": "El vuelo no existe",
  "url": "/api/v1/vuelos/vuelo"
}
```

##### **Edit State Flight (Admin Only)**

`PUT /api/v1/vuelos/vuelo/actualizar-estado/1` 🔒
**Request Body**:

```json
{
  "estado": "PROGRAMADO"
}
```

**States**:

- PROGRAMADO
- EN_ABORDAJE
- ABORDAJE_COMPLETO
- RETRASADO
- EN_VUELO
- ATERRIZADO
- CANCELADO
- DESVIADO
- EN_ESPERA
- FINALIZADO

**Success Response** (200 OK)

```json
{
  "timeStamp": "2025-03-04 17:37:44",
  "code": "P-200",
  "message": "El vuelo no existe",
  "url": "/api/v1/vuelos/vuelo/actualizar-estado"
}
```

**Error Response** (404 Not Found)

```json
{
  "timeStamp": "2025-03-04 17:37:44",
  "code": "P-404",
  "message": "El vuelo no existe",
  "url": "/api/v1/vuelos/vuelo/actualizar-estado"
}
```

##### **Edit Information Flight (Admin Only)**

`PUT /api/v1/vuelos/vuelo/editar/1` 🔒
**Request Body**:

```json
{
  "aerolínea": "American Airlines",
  "numeroVuelo": "AA456",
  "tipoAvion": "Airbus A320",
  "origen": "MIA",
  "destino": "ORD",
  "fechaIda": "2023-12-26",
  "horaSalida": "10:00:00",
  "fechaVuelta": "2023-12-31",
  "horaVuelta": "15:30:00",
  "duracion": "3h 15m",
  "bussinessClass": 15,
  "economyClass": 120,
  "precioBussiness": 750,
  "precioEconomy": 250
}
```

**Success Response** (200 OK)

```json
{
  "timeStamp": "2025-03-04 17:37:44",
  "code": "P-200",
  "message": "La informacion del vuelo fue actualizada correctamente",
  "url": "/api/v1/vuelos/vuelo/editar"
}
```

**Error Response** (404 Not Found)

```json
{
  "timeStamp": "2025-03-04 17:37:44",
  "code": "P-404",
  "message": "El vuelo no existe",
  "url": "/api/v1/vuelos/vuelo/editar"
}
```

##### **Delete Flight (Admin Only)**

`DELETE /api/v1/vuelos/vuelo/eliminar/4` 🔒

**Success Response** (200 OK)

```json
{
  "timeStamp": "2025-03-04 17:37:44",
  "code": "P-200",
  "message": "El vuelo fue eliminado correctamente",
  "url": "/api/v1/vuelos/vuelo/eliminar"
}
```

**Error Response** (404 Not Found)

```json
{
  "timeStamp": "2025-03-04 17:37:44",
  "code": "P-404",
  "message": "El vuelo no existe",
  "url": "/api/v1/vuelos/vuelo/eliminar"
}
```

##### **Get Seats**

`GET /api/v1/vuelos/vuelo/asientos/1`

**Success Response** (200 OK)

```json
[
  {
    "numeroAsiento": 1,
    "clase": "Bussiness",
    "estado": "Ocupado"
  },
  {
    "numeroAsiento": 2,
    "clase": "Bussiness",
    "estado": "Disponible"
  },
  {
    "numeroAsiento": 3,
    "clase": "Bussiness",
    "estado": "Ocupado"
  },
  {
    "numeroAsiento": 4,
    "clase": "Bussiness",
    "estado": "Disponible"
  },
  {
    "numeroAsiento": 5,
    "clase": "Economy",
    "estado": "Disponible"
  },
  {
    "numeroAsiento": 6,
    "clase": "Economy",
    "estado": "Ocupado"
  },
  {
    "numeroAsiento": 7,
    "clase": "Economy",
    "estado": "Disponible"
  },
  {
    "numeroAsiento": 8,
    "clase": "Economy",
    "estado": "Ocupado"
  }
]
```

**Error Response** (404 Not Found)

```json
{
  "timeStamp": "2025-03-04 17:37:44",
  "code": "P-404",
  "message": "El vuelo no existe",
  "url": "/api/v1/vuelos/vuelo/asientos"
}
```

---

#### `#RESERVAS`

_(Requires Authentication)_

##### **Book a Seat**

`POST /api/v1/reservas/reservar/1`  
**Request Body**:

```json
{
  "id_usuario": 1,
  "clase": "bussiness",
  "numero_asiento": "2"
}
```

**Success Response** (201 Created):

```json
{
  "timeStamp": "2025-03-04 17:37:44",
  "code": "P-201",
  "message": "Se ha reservado el vuelo correctamente",
  "url": "/api/v1/reservas/reservar"
}
```

**Error Response** (404 Not Found)

```json
{
  "timeStamp": "2025-03-04 17:37:44",
  "code": "P-404",
  "message": "El vuelo no existe",
  "url": "/api/v1/reservas/reservar"
}
```

**Error Response** (400 Bad Request)

```json
{
  "timeStamp": "2025-03-04 17:37:44",
  "code": "P-400",
  "message": "El asiento está reservado",
  "url": "/api/v1/reservas/reservar"
}
```

**Error Response** (400 Bad Request)

```json
{
  "timeStamp": "2025-03-04 17:37:44",
  "code": "P-400",
  "message": "El vuelo está completamente ocupado",
  "url": "/api/v1/reservas/reservar"
}
```

**Error Response** (400 Bad Request)

```json
{
  "timeStamp": "2025-03-04 17:37:44",
  "code": "P-400",
  "message": "Los asientos de bussines class ya estan completamente llenos",
  "url": "/api/v1/reservas/reservar"
}
```

**Error Response** (400 Bad Request)

```json
{
  "timeStamp": "2025-03-04 17:37:44",
  "code": "P-400",
  "message": "Los asientos economy class ya estan completamente llenos",
  "url": "/api/v1/reservas/reservar"
}
```

**Error Response** (400 Bad Request)

```json
{
  "timeStamp": "2025-03-04 17:37:44",
  "code": "P-400",
  "message": "Este asiento no existe o no pertenece a la clase economy",
  "url": "/api/v1/reservas/reservar"
}
```

**Error Response** (400 Bad Request)

```json
{
  "timeStamp": "2025-03-04 17:37:44",
  "code": "P-400",
  "message": "Este asiento no existe o no pertenece a la clase bussiness",
  "url": "/api/v1/reservas/reservar"
}
```

##### **Cancel Seat**

`DELETE /api/v1/reservas/cancelar/3`

**Success Response** (200 OK)

```json
{
  "timeStamp": "2025-03-04 17:37:44",
  "code": "P-200",
  "message": "Se ha cancelado la reserva",
  "url": "/api/v1/reservas/cancelar"
}
```

**Error Response** (404 Not Found)

```json
{
  "timeStamp": "2025-03-04 17:37:44",
  "code": "P-404",
  "message": "La reserva no existe",
  "url": "/api/v1/reservas/reservar"
}
```

---
