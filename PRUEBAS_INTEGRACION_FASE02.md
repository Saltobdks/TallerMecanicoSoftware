# Pruebas de integración — Fase 02

Fecha: 28 de septiembre de 2026.

## Entorno comprobado

| Componente | Resultado | Evidencia |
|---|---|---|
| MariaDB compatible con MySQL | Correcto | Contenedores `taller-mysql` y `taller-mysql-v1` activos; datasource JDBC iniciado por Hikari. |
| API Spring Boot | Correcto | Tomcat iniciado en puerto 8080; JPA detectó tres repositorios y conectó a la base. |
| CORS desde Vite 5174 | Correcto | `OPTIONS /api/auth/customers` devolvió `200`, `Access-Control-Allow-Origin: http://localhost:5174`, métodos POST y cabeceras `content-type, authorization`. |
| Inicio de sesión | Correcto | `POST /api/auth/login` devolvió JWT para Superadmin. |
| Registro de cliente | Correcto | `POST /api/auth/customers` desde origen Vite devolvió `201` y `customerId: 1`. |
| Persistencia | Correcto | Cliente `integracion.fase02@motorflow.local` y relación `customer_id=1, workshop_id=1` comprobados con consulta SQL. |

## Registro de integración conservado

El siguiente registro se insertó deliberadamente para validar el flujo:

```text
Cliente: Prueba Integracion Fase 02
Correo: integracion.fase02@motorflow.local
Teléfono: 5512345678
Sucursal: 1
```

## Hallazgo pendiente

Las solicitudes posteriores al primer registro devolvieron `403`, incluso con un JWT recién emitido. El preflight CORS y el alta inicial fueron exitosos, así que el problema se concentra en la cadena de autenticación/autorización para solicitudes repetidas. Antes de producción se debe:

1. Reemplazar el manejo silencioso de excepciones de `JwtFilter` por registro seguro y respuestas controladas.
2. Agregar pruebas automatizadas de `401`, `403`, `409` y `400`.
3. Confirmar que el mismo JWT funciona de forma repetida para login, alta y consulta protegida.

No se insertaron registros adicionales durante los intentos negativos.
