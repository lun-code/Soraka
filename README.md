# 🏥 Soraka — Gestión de Citas Hospitalarias

> 🚧 **Proyecto en desarrollo** — Algunas funcionalidades están aún en construcción.

Aplicación web fullstack para la reserva y gestión de citas médicas en un hospital. Los pacientes pueden consultar médicos disponibles, ver citas disponibles y reservarlas. Los médicos pueden crear y gestionar citas. Los administradores tienen control total sobre el sistema.

---

## ✨ Funcionalidades

- [x] Registro de usuarios (gestionado por el administrador o médico)
- [x] Inicio de sesión con JWT
- [x] Validación de cuenta por email
- [x] Gestión de médicos y especialidades
- [x] Gestión de usuarios y pacientes
- [x] Listado de citas disponibles
- [x] Reserva y cancelación de citas
- [x] Panel de administración completo
- [x] Dashboard por rol (Paciente, Médico, Admin)
- [x] Página pública de especialistas

---

## 👥 Roles del sistema

El sistema cuenta con tres roles con una jerarquía definida:

```
ADMIN > MEDICO > PACIENTE
```

Esto significa que un **ADMIN** hereda todos los permisos de MEDICO y PACIENTE, y un **MEDICO** hereda los permisos de PACIENTE.

| Rol | Permisos principales |
|---|---|
| `ADMIN` | Control total: gestión de usuarios, médicos, especialidades y citas |
| `MEDICO` | Consultar sus propias citas, cancelar citas y registrar nuevos usuarios |
| `PACIENTE` | Ver citas disponibles, reservar y cancelar sus propias citas |

---

## 🔐 Seguridad y autenticación

La autenticación está basada en **JWT (JSON Web Token)**:

1. El administrador (o médico) registra al usuario, cuya cuenta queda **inactiva** inicialmente.
2. Se envía un **email de confirmación** con un token único al usuario.
3. El usuario activa su cuenta haciendo clic en el enlace del email.
4. Una vez activa la cuenta, el usuario puede iniciar sesión y recibe un **token JWT** válido por **1 hora**.
5. Todas las peticiones protegidas deben incluir el token en el header `Authorization: Bearer <token>`.

El token JWT incluye el nombre, email y rol del usuario como claims adicionales.

---

## 📡 Endpoints de la API

### Autenticación `/auth`

| Método | Endpoint | Acceso | Descripción |
|---|---|---|---|
| `POST` | `/auth/login` | Público | Iniciar sesión y obtener token JWT |
| `POST` | `/auth/register` | MEDICO, ADMIN | Registrar un nuevo usuario |
| `GET` | `/auth/confirmar?token=...` | Público | Activar cuenta mediante token de email |

### Usuarios `/api/usuarios`

| Método | Endpoint | Acceso | Descripción |
|---|---|---|---|
| `GET` | `/api/usuarios` | ADMIN | Listar todos los usuarios |
| `GET` | `/api/usuarios/publico` | Público | Listar pacientes (datos limitados) |
| `GET` | `/api/usuarios/{id}` | ADMIN o el propio usuario | Obtener usuario por ID |
| `PATCH` | `/api/usuarios/{id}` | ADMIN | Modificar usuario |
| `DELETE` | `/api/usuarios/{id}` | ADMIN | Eliminar usuario |

### Médicos `/api/medicos`

| Método | Endpoint | Acceso | Descripción |
|---|---|---|---|
| `GET` | `/api/medicos` | ADMIN | Listar todos los médicos (datos completos) |
| `GET` | `/api/medicos/publicos` | Público | Listar médicos (datos públicos) |
| `GET` | `/api/medicos/{id}` | Autenticado | Obtener médico por ID |
| `POST` | `/api/medicos` | ADMIN | Crear médico |
| `PATCH` | `/api/medicos/{id}` | ADMIN | Modificar médico |
| `DELETE` | `/api/medicos/{id}` | ADMIN | Eliminar médico |

### Especialidades `/api/especialidades`

| Método | Endpoint | Acceso | Descripción |
|---|---|---|---|
| `GET` | `/api/especialidades` | Público | Listar todas las especialidades |
| `GET` | `/api/especialidades/{id}` | Público | Obtener especialidad por ID |
| `POST` | `/api/especialidades` | ADMIN | Crear especialidad |
| `PATCH` | `/api/especialidades/{id}` | ADMIN | Modificar especialidad |
| `DELETE` | `/api/especialidades/{id}` | ADMIN | Eliminar especialidad |

### Citas `/api/citas`

| Método | Endpoint | Acceso | Descripción |
|---|---|---|---|
| `GET` | `/api/citas` | ADMIN | Listar todas las citas |
| `GET` | `/api/citas/disponibles` | PACIENTE, MEDICO, ADMIN | Listar citas disponibles con fecha futura |
| `GET` | `/api/citas/mis-citas` | PACIENTE | Ver citas propias del paciente |
| `GET` | `/api/citas/mis-citas-medico` | MEDICO | Ver citas propias del médico autenticado |
| `GET` | `/api/citas/{id}` | MEDICO, ADMIN | Obtener cita por ID |
| `POST` | `/api/citas` | ADMIN | Crear cita |
| `PATCH` | `/api/citas/{id}` | ADMIN | Modificar cita |
| `DELETE` | `/api/citas/{id}` | ADMIN | Eliminar cita |
| `POST` | `/api/citas/{id}/reservar` | PACIENTE | Reservar una cita disponible |
| `POST` | `/api/citas/{id}/cancelar` | PACIENTE, MEDICO, ADMIN | Cancelar una cita |

---

## 🖥️ Rutas del Frontend

### Públicas

| Ruta | Descripción |
|---|---|
| `/` | Página de inicio (Hero con médicos y especialidades) |
| `/login` | Inicio de sesión |
| `/especialistas` | Listado público de especialistas |

### Paciente (requiere rol `PACIENTE`)

| Ruta | Descripción |
|---|---|
| `/dashboard` | Panel principal del paciente |
| `/mis-citas` | Ver, reservar y cancelar citas propias |

### Médico (requiere rol `MEDICO`)

| Ruta | Descripción |
|---|---|
| `/medico` | Dashboard del médico |

### Administrador (requiere rol `ADMIN`)

| Ruta | Descripción |
|---|---|
| `/admin` | Panel principal de administración |
| `/admin/usuarios` | Gestión de usuarios |
| `/admin/medicos` | Gestión de médicos |
| `/admin/especialidades` | Gestión de especialidades |
| `/admin/citas` | Gestión de citas |

---

## 🛠️ Tecnologías

### Backend
- **Java + Spring Boot**
- **Spring Security + JWT** — autenticación stateless
- **JPA / Hibernate** — acceso a base de datos
- **MySQL 8.0** — base de datos relacional

### Frontend
- **React 18** con **Vite**
- **React Router DOM v7** — enrutado por rol con rutas protegidas
- **Tailwind CSS v3** + **Material Tailwind** — estilos y componentes UI
- **Lucide React** + **Heroicons** — iconografía
- **jwt-decode** — decodificación del token en cliente

### Infraestructura
- **Docker + Docker Compose** — contenedores para backend, base de datos y phpMyAdmin

---

## 🚀 Instalación y ejecución

### Requisitos previos
- [Docker](https://www.docker.com/) y Docker Compose instalados

### 1. Clonar el repositorio

```bash
git clone https://github.com/lun-code/Soraka.git
cd Soraka
```

### 2. Configurar variables de entorno

Crea un archivo `.env` en la raíz del proyecto con el siguiente contenido:

```env
MYSQL_ROOT_PASSWORD=tu_password_root
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=tu_password_root
JWT_SECRET=tu_clave_secreta_jwt
```

### 3. Levantar los servicios

```bash
docker-compose up --build
```

Una vez iniciado, los servicios estarán disponibles en:

| Servicio | URL |
|---|---|
| API Backend | http://localhost:8080 |
| Frontend | http://localhost:5173 |
| phpMyAdmin | http://localhost:8081 |

> **Nota:** El frontend (`localhost:5173`) debe ejecutarse por separado en desarrollo con `npm run dev` dentro de la carpeta `frontend/`. El Docker Compose gestiona únicamente el backend, la base de datos y phpMyAdmin.

---

## 📁 Estructura del proyecto

```
Soraka/
├── backend/
│   └── src/main/java/com/hospital/Soraka/
│       ├── config/         # Configuración general y CORS
│       ├── controller/     # Controladores REST
│       ├── dto/            # Objetos de transferencia de datos
│       ├── entity/         # Entidades JPA
│       ├── enums/          # Enumeraciones (Rol)
│       ├── exception/      # Excepciones personalizadas
│       ├── repository/     # Repositorios Spring Data
│       ├── security/       # Configuración JWT y Spring Security
│       └── service/        # Lógica de negocio
├── frontend/
│   └── src/
│       ├── assets/         # Imágenes y recursos estáticos
│       ├── components/     # Componentes reutilizables (auth, home, admin, user...)
│       ├── contexts/       # AuthContext (estado global de autenticación)
│       ├── pages/          # Páginas por rol (home, user, medico, admin)
│       └── services/       # Llamadas a la API REST
├── docker-compose.yml
└── dockerfile
```

---

## 📄 Licencia

© 2026 lun-code. Todos los derechos reservados. Este proyecto es de carácter personal y se comparte únicamente con fines de visualización.
