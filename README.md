# Taller Seguro

Sistema inicial de gestión de órdenes de reparación: acceso por roles, registro, recuperación de contraseña y API REST.

## Arranque

1. Copia `.env.example` a `.env` y reemplaza **todos** los secretos. Genera claves seguras: `openssl rand -base64 32`.
2. Inicia MySQL: `docker compose --env-file .env up -d`.
3. Backend: `cd backend && mvn spring-boot:run`.
4. Frontend: `cd frontend && npm install && npm run dev`.

La interfaz queda en `http://localhost:5173` y la API en `http://localhost:8080/api`.

## Cuenta inicial

Se crea al iniciar la API con `SUPERADMIN_EMAIL` y `SUPERADMIN_PASSWORD`; cámbiala inmediatamente. La contraseña se persiste con BCrypt, nunca reversible.

## Seguridad aplicada

- Contraseñas con BCrypt y tokens JWT firmados.
- Control de acceso por rol en servidor; el frontend sólo mejora la experiencia, no autoriza.
- Campos personales se cifran en reposo con AES-256-GCM mediante `DATA_ENCRYPTION_KEY`.
- JPA parametriza las consultas y DTOs validan entradas.
- CORS restringido al frontend configurable, respuesta de errores sin detalles internos.
- Para producción, sirve API y frontend únicamente por HTTPS (TLS), guarda secretos en un vault y configura un proveedor SMTP real para recuperación. El endpoint de recuperación siempre devuelve una respuesta genérica para no revelar cuentas.

## Roles

`SUPERADMIN > ADMINISTRADOR > DUENO > GERENTE > SECRETARIA > MECANICO > CLIENTE`.

Los registros públicos crean cuentas `CLIENTE`. Los roles elevados se asignan desde el módulo de usuarios por alguien con permisos suficientes.

## Siguientes módulos REST sugeridos

Órdenes y estados, vehículos, citas, inventario, cotizaciones/facturas, auditoría, notificaciones y permisos granulares por recurso. Añade rate limiting, rotación/revocación de JWT, verificación de correo y respaldos cifrados antes de producción.
