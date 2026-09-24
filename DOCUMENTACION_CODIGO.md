# Referencia de código — Fase 1

Este documento es la referencia técnica de los componentes generados en la Fase 1. Describe el comportamiento implementado; no sustituye pruebas automatizadas ni una especificación OpenAPI.

## Backend (`backend/src/main/java/mx/taller`)

| Componente | Responsabilidad |
|---|---|
| `TallerApplication` | Punto de entrada de Spring Boot. |
| `Bootstrap` | Crea la cuenta `SUPERADMIN` una sola vez al iniciar, tomando correo y contraseña de variables de entorno. |
| `user/User` | Entidad JPA persistida como `usuarios`: `id`, `email`, hash `password`, `nombre`, `role`, `activo`, token y vencimiento de recuperación. Normaliza correo y nombre antes de persistir. |
| `user/Role` | Catálogo de los siete niveles: `SUPERADMIN`, `ADMINISTRADOR`, `DUENO`, `GERENTE`, `SECRETARIA`, `MECANICO`, `CLIENTE`. |
| `user/UserRepository` | Consultas JPA tipadas por correo y token de recuperación; evita construir SQL manual. |
| `auth/AuthDtos` | Contratos de entrada/salida y validación Bean Validation para registro, acceso y recuperación. |
| `auth/AuthController` | Endpoints REST públicos de autenticación. Hashea contraseñas mediante el `PasswordEncoder`, no devuelve hashes, y responde igual al solicitar recuperación para no enumerar cuentas. |
| `security/JwtService` | Firma tokens JWT HMAC y extrae el sujeto. Su vigencia actual es de una hora. |
| `security/JwtFilter` | Lee el encabezado `Authorization: Bearer <JWT>`, valida el token, carga el usuario activo y publica su autoridad Spring Security. |
| `security/SecurityConfig` | Declara BCrypt (coste 12), API sin sesión, CORS restringido y reglas de autorización. |

### Endpoints implementados

| Método y ruta | Datos de entrada | Resultado | Notas |
|---|---|---|---|
| `POST /api/auth/register` | `nombre`, `email`, `password` (mín. 10) | `201` y JWT | Sólo crea `CLIENTE`; correo único. |
| `POST /api/auth/login` | `email`, `password` | `200` y JWT | Devuelve `401` para credenciales inválidas o cuenta inactiva. |
| `POST /api/auth/password/forgot` | `email` | `200` genérico | Genera token aleatorio con vigencia de 15 min. No existe aún envío por correo ni endpoint de confirmación. |

### Reglas de seguridad codificadas

- `POST /api/auth/**` es público.
- Toda otra ruta requiere autenticación JWT, excepto `/actuator/health`.
- Contraseñas se almacenan con BCrypt; no son recuperables.
- `SecurityConfig` permite la URL configurada en `FRONTEND_URL`.

## Frontend (`frontend/src`)

| Archivo | Responsabilidad |
|---|---|
| `main.jsx` | Monta el componente `App`, conserva estado de vista/formulario, invoca los tres endpoints y guarda el JWT en `localStorage` después de un acceso correcto. |
| `styles.css` | Diseño responsive de dos paneles, identidad visual MotorFlow, estados de formulario y adaptación móvil. |
| `tailwind.config.js` | Paleta de color preparada para Tailwind; actualmente el diseño se implementa con CSS propio, no mediante utilidades Tailwind. |

La constante `API` de `main.jsx` usa actualmente `http://localhost:8080/api`. Debe sustituirse por una variable `VITE_API_URL` antes de desplegar el frontend.

## Convenciones para continuar

1. Documentar cada controlador con propósito, autorización, entrada, salida y errores HTTP.
2. Mantener DTOs separados de entidades JPA.
3. No registrar contraseñas, JWT, tokens de recuperación ni valores de `.env`.
4. Añadir pruebas de integración para cada endpoint y pruebas de autorización por rol.
