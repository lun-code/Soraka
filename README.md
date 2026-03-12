# 🏥 Soraka — Gestión de Citas Médicas

![Estado](https://img.shields.io/badge/estado-desplegado-brightgreen)
![Version](https://img.shields.io/badge/versión-1.0.0-blue)
![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.9-brightgreen)
![React](https://img.shields.io/badge/React-18-61DAFB)
![Docker](https://img.shields.io/badge/Docker-Compose-2496ED)
![Licencia](https://img.shields.io/badge/licencia-privada-lightgrey)

**Soraka** es una aplicación web fullstack para la gestión de citas médicas hospitalarias. Los pacientes pueden consultar especialistas y reservar citas online. Los médicos gestionan su agenda. Los administradores tienen control total del sistema.

🌐 **Demo en vivo:** [https://soraka-hospital.vercel.app](https://soraka-hospital.vercel.app)

---

## 📋 Tabla de contenidos

- [Demo](#-entorno-demo)
- [Características](#-características)
- [Tecnologías](#️-tecnologías)
- [Arquitectura](#-arquitectura)
- [Instalación local](#-instalación-local)
- [Despliegue en producción](#-despliegue-en-producción)
- [API REST](#-api-rest)
- [Rutas del frontend](#️-rutas-del-frontend)
- [Estructura del proyecto](#-estructura-del-proyecto)
- [Autor](#-autor)

---

## 🎭 Entorno Demo

La aplicación incluye cuentas preconfiguradas para explorar todas las funcionalidades:

| Email | Rol | Contraseña |
|---|---|---|
| admin.demo@soraka.com | ADMIN | demo1234 |
| medico.demo@soraka.com | MEDICO | demo1234 |
| paciente.demo@soraka.com | PACIENTE | demo1234 |

> ⚠️ El sistema se resetea automáticamente cada 30 minutos, restaurando los datos demo y eliminando los cambios realizados por visitantes.

---

## ✨ Características

- **Autenticación JWT** stateless con roles diferenciados (ADMIN, MEDICO, PACIENTE)
- **Confirmación de cuenta** por email al registrar un nuevo usuario
- **Rutas protegidas** por rol en el frontend
- **Panel de administración** completo: usuarios, médicos, especialidades y citas
- **Dashboard del médico** con agenda de citas propias
- **Portal del paciente** para consultar especialistas, reservar y cancelar citas
- **Listado público** de especialistas sin necesidad de autenticación
- **Reset automático** del entorno demo cada 30 minutos
- **Fotos de médicos** gestionadas por URL
- **CORS configurable** por variable de entorno

---

## 🛠️ Tecnologías

### Backend
- **Java 21 + Spring Boot 3.5.9**
- **Spring Security + JWT (jjwt 0.11.5)** — autenticación stateless
- **JPA / Hibernate** — acceso a base de datos
- **MySQL 8.0** — base de datos relacional
- **JavaMailSender** — envío de emails de confirmación
- **Maven** — gestión de dependencias

### Frontend
- **React 18** con **Vite**
- **React Router DOM v7** — enrutado por rol con rutas protegidas
- **Tailwind CSS v3** + **Material Tailwind** — estilos y componentes UI
- **Lucide React** — iconografía
- **jwt-decode** — decodificación del token en cliente

### Infraestructura
- **Docker + Docker Compose** — contenedores para backend, base de datos y phpMyAdmin
- **Vercel** — despliegue del frontend en producción

---

## 🏗️ Arquitectura

```
Cliente (Vercel)          Servidor VPS / Cloud
┌─────────────┐           ┌────────────────────────────┐
│   React +   │  HTTPS    │  Docker Compose            │
│   Vite      │ ────────► │  ┌──────────┐ ┌─────────┐  │
│  (Vercel)   │           │  │ Spring   │ │  MySQL  │  │
└─────────────┘           │  │  Boot    │ │   8.0   │  │
                          │  │ :8080    │ │  :3306  │  │
                          │  └──────────┘ └─────────┘  │
                          │  ┌──────────┐              │
                          │  │phpMyAdmin│              │
                          │  │  :8081   │              │
                          │  └──────────┘              │
                          └────────────────────────────┘
```

El frontend está desplegado en **Vercel** y consume la API del backend mediante la variable de entorno `VITE_API_URL`. El backend, la base de datos y phpMyAdmin corren en Docker Compose.

---

## 🚀 Instalación local

### Requisitos previos
- [Docker](https://www.docker.com/) y Docker Compose instalados
- Node.js >= 20 (para el frontend en desarrollo)

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
JWT_SECRET=tu_clave_secreta_jwt_minimo_32_caracteres
MAIL_USERNAME=tu_email@gmail.com
MAIL_PASSWORD=tu_app_password_gmail
CORS_ALLOWED_ORIGIN=http://localhost:5173
```

### 3. Levantar el backend con Docker

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

> **Nota:** El Docker Compose gestiona el backend, la base de datos y phpMyAdmin. El frontend debe ejecutarse por separado con `npm run dev`.

---

## ☁️ Despliegue en producción

### Frontend — Vercel

1. Importa el repositorio en [Vercel](https://vercel.com)
2. Configura el directorio raíz como `frontend`
3. Añade la variable de entorno en el panel de Vercel:

```env
VITE_API_URL=https://tu-dominio-backend.com
```

4. Vercel detecta automáticamente Vite y despliega el build.

### Backend — Docker en VPS

1. Copia los archivos al servidor:

```bash
scp -r . usuario@tu-servidor:/opt/soraka
```

2. En el servidor, crea el `.env` con las variables de producción (incluye `CORS_ALLOWED_ORIGIN` apuntando al dominio de Vercel):

```env
MYSQL_ROOT_PASSWORD=password_seguro
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=password_seguro
JWT_SECRET=clave_secreta_larga_y_segura
MAIL_USERNAME=tu_email@gmail.com
MAIL_PASSWORD=tu_app_password_gmail
CORS_ALLOWED_ORIGIN=https://soraka-hospital.vercel.app
```

3. Levanta los contenedores:

```bash
docker-compose up -d --build
```

4. (Opcional) Configura un proxy inverso como **Nginx** con SSL para exponer el backend en HTTPS.

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
| `GET` | `/api/especialidades/{id}` | Público | Obtener especialidad por ID |
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
| `GET` | `/api/citas/{id}` | MEDICO, ADMIN | Obtener cita por ID |
| `POST` | `/api/citas` | ADMIN | Crear cita |
| `PATCH` | `/api/citas/{id}` | ADMIN | Modificar cita |
| `DELETE` | `/api/citas/{id}` | ADMIN | Eliminar cita |
| `POST` | `/api/citas/{id}/reservar` | PACIENTE | Reservar una cita disponible |
| `POST` | `/api/citas/{id}/cancelar` | PACIENTE, MEDICO, ADMIN | Cancelar una cita |

### Seguridad

El token JWT se genera al hacer login e incluye `nombre`, `email` y `rol` como claims. Debe enviarse en cada petición protegida mediante el header:

```
Authorization: Bearer <token>
```

---

## 🖥️ Rutas del frontend

### Públicas
| Ruta | Descripción |
|---|---|
| `/` | Página de inicio |
| `/login` | Inicio de sesión |
| `/especialistas` | Listado público de especialistas |
| `/contacto` | Información del desarrollador |

### Paciente (rol `PACIENTE`)
| Ruta | Descripción |
|---|---|
| `/dashboard` | Panel principal |
| `/mis-citas` | Ver, reservar y cancelar citas |
| `/mi-perfil` | Información personal |

### Médico (rol `MEDICO`)
| Ruta | Descripción |
|---|---|
| `/medico` | Dashboard con agenda de citas |

### Administrador (rol `ADMIN`)
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
├── dockerfile
└── README.md
```

---

## 👨‍💻 Autor

**Manuel José Esteban Valdivia**

- 🌐 GitHub: [lun-code](https://github.com/lun-code)
- 💼 LinkedIn: [Manuel José Esteban Valdivia](https://www.linkedin.com/in/manuel-jos%C3%A9-esteban-valdivia-91a3803b1/)
- 📧 Email: lun.code01@gmail.com

---

## 📄 Licencia

© 2026 lun-code. Todos los derechos reservados. Este proyecto es de carácter personal y se comparte únicamente con fines de visualización.
