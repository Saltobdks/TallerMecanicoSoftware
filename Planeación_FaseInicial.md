# Planeación — Fase inicial

**Sistema:** MotorFlow, gestión de órdenes de reparación para taller mecánico  
**Fecha de revisión:** 24 de septiembre de 2026  
**Alcance revisado:** código disponible en este repositorio local.

## 1. Estado objetivo de la Fase 1

### Módulos concluidos al 100%

No existe todavía un módulo funcional de extremo a extremo concluido al 100%. El flujo de autenticación está iniciado y operativo en sus rutas de registro e inicio de sesión, pero la recuperación no puede finalizarse y la configuración de despliegue aún requiere ajustes. A continuación se registran los componentes desarrollados, sin presentarlos como módulos cerrados.

### Componente desarrollado: interfaz de autenticación

**Entidades/datos:** nombre, correo electrónico y contraseña; respuesta de sesión con token, nombre, correo y rol.

**Funcionalidad terminada:** interfaz React/Vite para iniciar sesión, registro público y solicitud de recuperación; validación HTML básica; estados de carga y mensajes; diseño adaptable para escritorio y móvil. El registro está limitado a crear cuentas `CLIENTE`.

### Componente desarrollado: acceso y autenticación inicial del API

**Entidades/datos:** entidad `User`/tabla `usuarios` (`id`, `email`, `password`, `nombre`, `role`, `activo`, `resetToken`, `resetExpiresAt`), enum `Role` y JWT firmado.

**Funcionalidad terminada:**

- Alta pública de usuarios cliente con correo único y contraseña hasheada con BCrypt (coste 12).
- Inicio de sesión con JWT de una hora.
- Inicialización idempotente de un `SUPERADMIN` configurable.
- Cadena de seguridad stateless, filtro JWT, CORS configurable y restricciones de acceso para rutas futuras.
- Solicitud de recuperación que crea un token temporal y no revela si el correo existe.

### Componente desarrollado: infraestructura local de datos

**Entidades/datos:** base `taller`, usuario de aplicación `taller_app` y volumen persistente Docker declarados.

**Funcionalidad terminada:** definición de servicio compatible MySQL en `docker-compose.yml` y configuración de datasource Spring. En la ejecución local actual se utilizó MariaDB 10.6 —compatible con el driver MySQL— en el puerto **3307**, porque el puerto 3306 ya estaba ocupado.

## 2. Módulos en proceso o pendientes

| Componente | Estado real | Trabajo siguiente |
|---|---|---|
| Confirmación de recuperación | Iniciado | Agregar `POST /api/auth/password/reset`, validar token/vencimiento, invalidarlo y actualizar hash; integrar SMTP. |
| Gestión de usuarios/roles | Pendiente | Listado, cambio de rol, activación/desactivación, protección por jerarquía y auditoría. |
| Cifrado de datos en reposo | Pendiente | `DATA_ENCRYPTION_KEY` existe, pero no hay `AttributeConverter` AES-GCM aplicado a campos. Implementarlo y rotar claves mediante un gestor de secretos. |
| Transporte seguro | Pendiente para producción | Configurar HTTPS/TLS en proxy o proveedor; no publicar HTTP. |
| Órdenes de reparación | Pendiente | Entidades orden, vehículo, cliente, diagnóstico, estados, mano de obra, refacciones y evidencias. |
| Citas, inventario y facturación | Pendiente | Diseñar APIs, permisos, transacciones y trazabilidad. |
| Calidad | Pendiente | OpenAPI, pruebas unitarias/integración, limitación de tasa, revocación/renovación JWT, verificación de correo y auditoría. |

## 3. Arquitectura actual

```text
React + Vite (frontend:5173)
        │ HTTP local / HTTPS en producción
        ▼
Spring Boot REST + Spring Security (backend:8080)
        │ JPA / JDBC
        ▼
MySQL 8.4 (Docker) o MariaDB 10.6 compatible (local:3307)
```

Los contratos, endpoints y responsabilidades por clase están documentados en [DOCUMENTACION_CODIGO.md](DOCUMENTACION_CODIGO.md).

## 4. Configuración y credenciales

### Rutas exactas

| Archivo | Uso | ¿Subir a Git? |
|---|---|---|
| `/home/tina/Documentos/taller_v1/.env.example` | Plantilla de secretos requeridos. | Sí, sin secretos reales. |
| `/home/tina/Documentos/taller_v1/.env` | Debe contener secretos reales para Docker/despliegue. Actualmente no existe en el repositorio. | **No**; está ignorado por Git. |
| `/home/tina/Documentos/taller_v1/backend/src/main/resources/application.yml` | Lee `DB_URL`, `DB_USER`, `MYSQL_PASSWORD`, `JWT_SECRET`, `DATA_ENCRYPTION_KEY`, `FRONTEND_URL`, `SUPERADMIN_EMAIL` y `SUPERADMIN_PASSWORD`. Incluye valores de desarrollo que deben reemplazarse. | Sí, sin secretos reales. |
| `/home/tina/Documentos/taller_v1/docker-compose.yml` | Define usuario, base, contraseña y puerto de Docker mediante variables. | Sí, sin secretos reales. |

Para desarrollo, crear `.env` a partir de `.env.example` y definir secretos únicos. En producción, definir las mismas variables en el panel del proveedor o un vault; nunca en Git. El frontend no debe recibir `JWT_SECRET`, `MYSQL_PASSWORD`, `DATA_ENCRYPTION_KEY` ni `SUPERADMIN_PASSWORD`.

## 5. Guía de despliegue recomendada

### Frontend: Vercel

Se recomienda **Vercel** para este SPA Vite: se integra directamente con GitHub, crea vistas previas por cambio y soporta proyectos Vite. Antes de desplegar, cambiar el frontend para consumir `import.meta.env.VITE_API_URL` en vez de la URL local actual.

1. Confirmar que `npm run build` funciona dentro de `frontend`.
2. Crear en Vercel un proyecto e importar el repositorio GitHub.
3. Establecer **Root Directory**: `frontend`.
4. Definir **Build Command**: `npm run build`; **Output Directory**: `dist`.
5. Definir `VITE_API_URL=https://api.tu-dominio.com/api` como variable de entorno de producción. Sólo variables `VITE_*` pueden exponerse al navegador, por lo que debe ser una URL pública, no un secreto.
6. Publicar desde la rama principal y verificar login, registro y CORS. Configurar dominio y HTTPS del proveedor.

Vercel admite despliegues de Vite y despliegue desde Git/CLI. [Documentación de Vite en Vercel](https://vercel.com/docs/frameworks/frontend/vite), [guía de despliegue](https://vercel.com/docs/deployments/overview).

### Backend: Render Web Service + MySQL administrado

Se recomienda un **Render Web Service** para la API inicialmente; es más directo para Spring Boot y conecta repositorios Git. Para producción se debe usar una base MySQL administrada compatible o una VPS/servicio de base de datos que ofrezca MySQL. No se recomienda publicar el contenedor de base de datos de desarrollo directamente en Internet.

1. Subir el repositorio a GitHub y conectar el proveedor Git a Render.
2. Crear **Web Service** desde el repositorio, con **Root Directory** `backend`.
3. Build command: `mvn clean package -DskipTests`.
4. Start command: `java -jar target/taller-api-0.0.1.jar`.
5. Configurar variables: `DB_URL`, `DB_USER`, `MYSQL_PASSWORD`, `JWT_SECRET`, `DATA_ENCRYPTION_KEY`, `FRONTEND_URL`, `SUPERADMIN_EMAIL`, `SUPERADMIN_PASSWORD`. Usar secretos robustos y diferentes por entorno.
6. Hacer que `DB_URL` apunte a la base administrada y habilitar TLS de conexión si el proveedor lo exige.
7. Configurar health check, por ejemplo `/actuator/health`, sólo después de agregar Spring Boot Actuator al proyecto.
8. Configurar `FRONTEND_URL` con el dominio Vercel para que CORS permita únicamente esa interfaz y probar el flujo completo mediante HTTPS.

Render ofrece servicios web conectados a Git y redespliegue automático al enviar cambios a la rama configurada. [Guía oficial inicial de Render](https://render.com/docs/your-first-deploy), [despliegues automáticos](https://render.com/docs/deploys).

### Alternativa con mayor control: VPS + Docker Compose

Para operación continua o requisitos de red privada, usar una VPS con Docker Compose, Nginx/Caddy como proxy TLS, backend en contenedor y MySQL en red privada con volúmenes y respaldos. En ese caso, **no** exponer el puerto 3306/3307 al exterior, usar certificados TLS y secret manager/archivo `.env` con permisos restringidos.

## 6. Publicación en GitHub

El directorio no tiene remoto Git configurado durante esta revisión. Desde la raíz, después de validar que `.env` no esté incluido:

```bash
cd /home/tina/Documentos/taller_v1
git init
git add README.md DOCUMENTACION_CODIGO.md Planeación_FaseInicial.md backend frontend docker-compose.yml .env.example .gitignore
git commit -m "docs: agregar planeacion y documentacion de fase inicial"
git branch -M main
git remote add origin https://github.com/<organizacion-o-usuario>/<repositorio>.git
git push -u origin main
```

Si ya existe un repositorio remoto, omitir `git init` y `git remote add`; revisar primero con `git remote -v`.

## 7. Criterio de salida de la siguiente fase

La Fase 2 debe cerrar recuperación completa, administración de usuarios y roles, cifrado en reposo implementado, documentación OpenAPI, pruebas de autenticación, configuración de producción sin valores predeterminados inseguros y el primer módulo de órdenes de reparación.
