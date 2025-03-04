
# Flight Booking API

A RESTful API for managing flight bookings, user authentication, and profile management. Built with Spring Boot, PostgreSQL, Redis, and JWT for secure authentication. Includes features like two-factor authentication (2FA), flight management, seat reservations, and user profile customization.

## Technologies Used
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

#### `#AUTH`
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
**Success Response** (201 Created):
```json
{
  "message": "User registered successfully",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```
**Error Response** (409 Conflict):
```json
{
  "error": "User already exists"
}
```

##### **Login**
`POST /api/v1/auth/login`  
**Request Body**:
```json
{
  "email": "john@example.com",
  "contraseña": "securePassword123"
}
```
**Success Response** (200 OK):
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```
**Error Response** (401 Unauthorized):
```json
{
  "error": "Invalid credentials"
}
```

---

#### `#PROFILE`
*(Requires Authentication)*

##### **Upload Profile Image**
`POST /api/v1/profile/upload-image`  
**Request Body**:
- Multipart file (JPEG/PNG only).

**Success Response** (200 OK):
```json
{
  "message": "Image uploaded successfully"
}
```
**Error Response** (400 Bad Request):
```json
{
  "error": "Invalid file format"
}
```

---

#### `#VUELOS`
*(Requires Authentication. Admin-only endpoints marked with 🔒)*

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
**Success Response** (201 Created):
```json
{
  "message": "Flight created successfully"
}
```

---

#### `#RESERVAS`
*(Requires Authentication)*

##### **Book a Seat**
`POST /api/v1/reservas/reservar/1`  
**Request Body**:
```json
{
  "id_usuario": 1,
  "clase": "bussiness",
  "numero_asiento": "A1"
}
```
**Success Response** (200 OK):
```json
{
  "message": "Seat reserved successfully"
}
```
**Error Response** (400 Bad Request):
```json
{
  "error": "Seat already reserved"
}
```

---

## Results & Examples
### Successful Request
![Postman Success Example](https://via.placeholder.com/600x400?text=Postman+Success+Response)

### Error Request
![Postman Error Example](https://via.placeholder.com/600x400?text=Postman+Error+Response)

---

## Contributing
Pull requests are welcome. For major changes, open an issue first to discuss proposed changes.

## License
[MIT](https://choosealicense.com/licenses/mit/)
```
