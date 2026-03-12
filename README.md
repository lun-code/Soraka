# 🏥 Soraka — Gestión de Citas Hospitalarias

Aplicación web fullstack para la reserva y gestión de citas médicas hospitalarias. Los pacientes pueden consultar especialistas disponibles y reservar citas. Los médicos gestionan su agenda. Los administradores tienen control total del sistema.

---

## ✨ Funcionalidades

- [x] Registro de usuarios (gestionado por administrador o médico)
- [x] Confirmación de cuenta por email
- [x] Inicio de sesión con JWT
- [x] Rutas protegidas por rol en frontend y backend
- [x] Gestión completa de usuarios, médicos y especialidades
- [x] Listado público de especialistas
- [x] Reserva y cancelación de citas
- [x] Dashboard por rol (Paciente, Médico, Admin)
- [x] Panel de administración con filtros y paginación
- [x] Diseño responsive (móvil y escritorio)
- [x] Entorno demo público con reset automático cada 30 minutos

---

## 👥 Roles del sistema

| Rol | Permisos principales |
|---|---|
| `ADMIN` | Control total: gestión de usuarios, médicos, especialidades y citas |
| `MEDICO` | Consultar sus propias citas, registrar nuevos usuarios |
| `PACIENTE` | Ver citas disponibles, reservar y cancelar sus propias citas |

---

## 🔐 Seguridad y autenticación

La autenticación está basada en **JWT (JSON Web Token)**:

1. El administrador (o médico) registra al usuario — la cuenta queda **inactiva** inicialmente.
2. Se envía un **email de confirmación** con un token único al usuario.
3. El usuario activa su cuenta haciendo clic en el enlace del email.
4. Una vez activa, el usuario inicia sesión y recibe un **token JWT** válido por **1 hora**.
5. Todas las peticiones protegidas deben incluir el token en el header `Authorization: Bearer <token>`.

El token JWT incluye nombre, email y rol del usuario como claims.

---

## 🎭 Entorno Demo

La aplicación incluye un entorno de demostración pública con cuentas preconfiguradas:

| Email | Rol | Contraseña |
|---|---|---|
| admin.demo@soraka.com | ADMIN | demo1234 |
| medico.demo@soraka.com | MEDICO | demo1234 |
| paciente.demo@soraka.com | PACIENTE | demo1234 |

> El sistema se resetea automáticamente cada 30 minutos, restaurando los datos demo y eliminando los cambios realizados por visitantes.

---

## 🛠️ Tecnologías

### Backend
- **Java + Spring Boot**
- **Spring Security + JWT** — autenticación stateless
- **JPA / Hibernate** — acceso a base de datos
- **MySQL 8.0** — base de datos relacional
- **JavaMailSender** — envío de emails de confirmación

### Frontend
- **React 18** con **Vite**
- **React Router DOM v7** — enrutado por rol con rutas protegidas
- **Tailwind CSS v3** + **Material Tailwind** — estilos y componentes UI
- **Lucide React** — iconografía
- **jwt-decode** — decodificación del token en cliente

### Infraestructura
- **Docker + Docker Compose** — contenedores para backend, base de datos y phpMyAdmin

---

## 🚀 Instalación y ejecución

### Requisitos previos
- [Docker](https://www.docker.com/) y Docker Compose instalados
- Node.js (para el frontend en desarrollo)

### 1. Clonar el repositorio

```bash
git clone https://github.com/lun-code/Soraka.git
cd Soraka
```

### 2. Configurar variables de entorno

Crea un archivo `.env` en la raíz del proyecto:

```env
MYSQL_ROOT_PASSWORD=tu_password_root
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=tu_password_root
JWT_SECRET=tu_clave_secreta_jwt
MAIL_USERNAME=tu_email@gmail.com
MAIL_PASSWORD=tu_app_password_gmail
```

### 3. Levantar los servicios con Docker

```bash
docker-compose up --build
```

### 4. Ejecutar el frontend en desarrollo

```bash
cd frontend
npm install
npm run dev
```

Una vez iniciado, los servicios estarán disponibles en:

| Servicio | URL |
|---|---|
| Frontend | http://localhost:5173 |
| API Backend | http://localhost:8080 |
| phpMyAdmin | http://localhost:8081 |

> **Nota:** El Docker Compose gestiona únicamente el backend, la base de datos y phpMyAdmin. El frontend debe ejecutarse por separado con `npm run dev`.

---

## 🌐 API REST

### Autenticación `/auth`

| Método | Endpoint | Acceso | Descripción |
|---|---|---|---|
| `POST` | `/auth/login` | Público | Iniciar sesión |
| `POST` | `/auth/register` | ADMIN | Registrar nuevo usuario |
| `GET` | `/auth/confirmar?token=...` | Público | Confirmar cuenta por email |

### Usuarios `/api/usuarios`

| Método | Endpoint | Acceso | Descripción |
|---|---|---|---|
| `GET` | `/api/usuarios` | ADMIN | Listar todos los usuarios |
| `GET` | `/api/usuarios/{id}` | ADMIN, propio usuario | Obtener usuario por ID |
| `PATCH` | `/api/usuarios/{id}` | ADMIN | Modificar usuario |
| `DELETE` | `/api/usuarios/{id}` | ADMIN | Eliminar usuario |

### Médicos `/api/medicos`

| Método | Endpoint | Acceso | Descripción |
|---|---|---|---|
| `GET` | `/api/medicos` | Público | Listar todos los médicos |
| `GET` | `/api/medicos/{id}` | Público | Obtener médico por ID |
| `POST` | `/api/medicos` | ADMIN | Crear médico |
| `PATCH` | `/api/medicos/{id}` | ADMIN | Modificar médico |
| `DELETE` | `/api/medicos/{id}` | ADMIN | Eliminar médico |

### Especialidades `/api/especialidades`

| Método | Endpoint | Acceso | Descripción |
|---|---|---|---|
| `GET` | `/api/especialidades` | Público | Listar especialidades |
| `POST` | `/api/especialidades` | ADMIN | Crear especialidad |
| `PATCH` | `/api/especialidades/{id}` | ADMIN | Modificar especialidad |
| `DELETE` | `/api/especialidades/{id}` | ADMIN | Eliminar especialidad |

### Citas `/api/citas`

| Método | Endpoint | Acceso | Descripción |
|---|---|---|---|
| `GET` | `/api/citas` | ADMIN | Listar todas las citas |
| `GET` | `/api/citas/disponibles` | Autenticado | Citas disponibles con fecha futura |
| `GET` | `/api/citas/mis-citas` | PACIENTE | Citas propias del paciente |
| `GET` | `/api/citas/mis-citas-medico` | MEDICO | Citas propias del médico |
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
| `/` | Página de inicio |
| `/login` | Inicio de sesión |
| `/especialistas` | Listado público de especialistas |
| `/contacto` | Información del desarrollador |

### Paciente
| Ruta | Descripción |
|---|---|
| `/dashboard` | Panel principal |
| `/mis-citas` | Ver, reservar y cancelar citas |
| `/mi-perfil` | Información personal |

### Médico
| Ruta | Descripción |
|---|---|
| `/medico` | Dashboard con agenda de citas |

### Administrador
| Ruta | Descripción |
|---|---|
| `/admin` | Panel con estadísticas del sistema |
| `/admin/usuarios` | Gestión de usuarios |
| `/admin/medicos` | Gestión de médicos |
| `/admin/especialidades` | Gestión de especialidades |
| `/admin/citas` | Gestión de citas |

---

## 📁 Estructura del proyecto

```
Soraka/
├── backend/
│   └── src/main/java/com/hospital/Soraka/
│       ├── config/         # Configuración, CORS, datos demo y reset scheduler
│       ├── controller/     # Controladores REST
│       ├── dto/            # Objetos de transferencia de datos
│       ├── entity/         # Entidades JPA
│       ├── enums/          # Enumeraciones (Rol, EstadoCita)
│       ├── exception/      # Excepciones personalizadas
│       ├── repository/     # Repositorios Spring Data
│       ├── security/       # JWT y Spring Security
│       └── service/        # Lógica de negocio
├── frontend/
│   └── src/
│       ├── assets/         # Imágenes y recursos estáticos
│       ├── components/     # Componentes reutilizables por rol
│       ├── contexts/       # AuthContext (estado global de autenticación)
│       ├── hooks/          # Hooks personalizados por rol
│       ├── pages/          # Páginas por rol (home, user, medico, admin)
│       └── services/       # Llamadas a la API REST
├── docker-compose.yml
└── dockerfile
```

---

## 👨‍💻 Autor

**Manuel José Esteban Valdivia**

- GitHub: [lun-code](https://github.com/lun-code)
- LinkedIn: [Manuel José Esteban Valdivia](https://www.linkedin.com/in/manuel-jos%C3%A9-esteban-valdivia-91a3803b1/)
- Email: lun.code01@gmail.com

---

## 📄 Licencia

© 2026 lun-code. Todos los derechos reservados. Este proyecto es de carácter personal y se comparte únicamente con fines de visualización.
