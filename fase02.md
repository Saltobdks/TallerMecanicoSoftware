# Fase 02 — Registro de clientes

Implementación adaptada a la arquitectura existente: **React/Vite + Spring Boot + JPA**, no Vue.js. Se emplea `CustomerFacade` como punto de orquestación y `CustomerRepository` para persistencia.

## Flujo implementado

```text
POST /api/auth/customers + JWT
  → CustomerFacade.registerCustomer
  → AuthGuardService (ADMINISTRADOR / SECRETARIA)
  → ValidationService (formatos y dirección)
  → CustomerRepository (duplicados email/teléfono)
  → Customer + CustomerWorkshop (transacción)
```

## Componentes

| Componente | Ubicación | Responsabilidad |
|---|---|---|
| `AuthGuardService` | `backend/src/main/java/mx/taller/customer` | Autoriza exclusivamente roles `ADMINISTRADOR` o `SECRETARIA`; la restricción vive en API. |
| `ValidationService` | Mismo paquete | Valida teléfonos de diez dígitos, CP de cinco, fotografía URL/Base64 y concordancia edad-fecha. |
| `CustomerRepository` | Mismo paquete | Consulta `existsByEmailOrTelefonoPersonal` y persiste clientes. Índices únicos en DB garantizan integridad. |
| `CustomerFacade` | Mismo paquete | Coordina todo el caso de uso con una transacción. |
| `CustomerWorkshop` | Mismo paquete | Relación cliente–sucursal idempotente y preparada para varios talleres. |
| `CustomerController` | Mismo paquete | Expone `POST /api/auth/customers` y devuelve `201` con ID del cliente. |
| `customer.jsx` | `frontend/src` | Formulario React de la operación, disponible en `/customer.html`. |

## Contrato API

`POST /api/auth/customers`

Requiere `Authorization: Bearer <JWT>` de un administrador o secretaria. El cuerpo incluye datos personales, contacto, dirección atómica y `workshopId`. Devuelve `{ "customerId": 1, "message": "Cliente registrado correctamente." }` con estado `201`.

Errores: `400` para formato o consistencia inválida, `401/403` para sesión/rol insuficiente y `409` ante correo o teléfono duplicado.

## Verificación realizada

`mvn -q -DskipTests compile` finalizó correctamente. `npm run build` genera las entradas de autenticación y clientes.
