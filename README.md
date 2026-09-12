# CRM Ferretero

CRM especializado en distribuidoras mayoristas de artículos de ferretería y materiales de
construcción. El usuario del sistema es la distribuidora; los "clientes" que carga son ferreterías
minoristas, corralones, talleres y constructoras (modelo B2B).

> **Estado de esta entrega:** por ahora está implementado **solo el backend**. El frontend
> (React) y la dockerización de todo el proyecto quedan para el siguiente paso, según lo acordado.
> Este README documenta cómo levantar y probar la API tal como está hoy.

## Stack

- **Backend**: Java 21, Spring Boot 3.3.4, Maven.
- **Base de datos**: MongoDB 7.
- **Seguridad**: Spring Security + JWT (jjwt 0.12.6), stateless.
- **Sin Lombok**: por decisión explícita, ningún módulo del proyecto usa Lombok. Los documentos de
  Mongo son clases Java normales (constructor, getters y setters escritos a mano) y los DTOs son
  `record`.
- **Sin tests**: no se agregó JUnit ni ningún framework de testing, según lo pedido.

## Requisitos previos

- JDK 21
- Maven 3.9+ (o usar `./mvnw` si lo agregás)
- Un MongoDB 7 corriendo en `localhost:27017` (podés levantarlo con
  `docker run -d --name mongo-crm -p 27017:27017 mongo:7` si tenés Docker, o instalado local)

## Cómo levantarlo (desarrollo, sin Docker)

```bash
cd backend
mvn spring-boot:run
```

Por defecto se conecta a `mongodb://localhost:27017/crm_ferretero` y expone la API en
`http://localhost:8080`. Al arrancar por primera vez, el `SeedRunner` carga automáticamente
catálogos, usuarios y datos de demostración (ver más abajo). Es idempotente: si volvés a arrancar
con la base ya cargada, no duplica nada.

### Variables de entorno (opcionales)

Si no las definís, se usan los valores por defecto de `application.yml`:

| Variable | Default | Descripción |
|---|---|---|
| `MONGODB_URI` | `mongodb://localhost:27017/crm_ferretero` | Conexión a Mongo |
| `JWT_SECRET` | secreto de ejemplo (32+ caracteres) | **Cambiar en cualquier entorno real** |
| `JWT_VENCIMIENTO_MINUTOS` | `480` | Duración del token |
| `CORS_ALLOWED_ORIGINS` | `http://localhost:5173,http://localhost:3000` | Orígenes permitidos |
| `APP_SEED_ENABLED` | `true` | Poné `false` para arrancar con la base vacía sin que falle |

## Usuarios de prueba

Cargados por el seed, contraseñas con BCrypt:

| Nombre | Email | Contraseña | Rol |
|---|---|---|---|
| Admin del sistema | `admin@crmferretero.com` | `Admin123!` | ADMIN |
| Sergio Rivas | `sergio.rivas@crmferretero.com` | `Vendedor123!` | VENDEDOR |
| Paula Ortiz | `paula.ortiz@crmferretero.com` | `Vendedor123!` | VENDEDOR |
| Marta Coria | `marta.coria@crmferretero.com` | `Responsable123!` | RESPONSABLE_COMERCIAL |

## Guion de demostración (por API, sin frontend todavía)

Los seis pasos que pide el enunciado, probados directamente contra la API:

```bash
# 1. Iniciar sesión
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@crmferretero.com","password":"Admin123!"}'
# copiar el "token" de la respuesta

TOKEN="<pegar el token acá>"

# 2. Registrar un comercio y un contacto
curl -X POST http://localhost:8080/api/empresas \
  -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" \
  -d '{"razonSocial":"Ferretería Demo S.R.L.","tipoComercio":"FERRETERIA_MINORISTA","condicionIva":"RESPONSABLE_INSCRIPTO","zonaReparto":"Morón"}'
# copiar el "id" de la empresa creada

curl -X POST http://localhost:8080/api/contactos \
  -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" \
  -d '{"nombre":"Juan","apellido":"Pérez","empresaId":"<id de la empresa>"}'

# 3. Crear una oportunidad
curl -X POST http://localhost:8080/api/oportunidades \
  -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" \
  -d '{"titulo":"Pedido de prueba","empresaId":"<id de la empresa>","responsableComercialId":"<id de un usuario>","valorEstimado":100000}'
# copiar el "id" de la oportunidad creada

# 4. Verla en el embudo
curl http://localhost:8080/api/embudo -H "Authorization: Bearer $TOKEN"

# 5. Cambiarla de etapa (usar el id de una etapa de /api/etapas)
curl -X PATCH http://localhost:8080/api/oportunidades/<id de la oportunidad>/etapa \
  -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" \
  -d '{"etapaId":"<id de la etapa siguiente>","observacion":"Avanza a la siguiente etapa"}'

# 6. Comprobar que quedó guardado: repetir el GET de la oportunidad o del embudo,
#    o reiniciar la aplicación (con el mismo Mongo) y volver a consultarla.
curl http://localhost:8080/api/oportunidades/<id de la oportunidad> -H "Authorization: Bearer $TOKEN"
```

## Tabla de endpoints

```
POST   /api/auth/login                { email, password } → { token, expiraEnSegundos, usuario }
GET    /api/auth/me

GET    /api/usuarios                  → activos (para selectores de responsable)

GET    /api/empresas                  ?q=&estado=&tipoComercio=&zona=&responsableId=&pagina=&tamanio=
GET    /api/empresas/opciones
GET    /api/empresas/{id}
POST   /api/empresas                  → 201
PUT    /api/empresas/{id}
PATCH  /api/empresas/{id}/estado      { estado }

GET    /api/contactos                 ?q=&empresaId=&estado=&responsableId=&pagina=&tamanio=
GET    /api/contactos/{id}
POST   /api/contactos                 → 201
PUT    /api/contactos/{id}
PATCH  /api/contactos/{id}/estado     { estado }

GET    /api/productos                 ?q=&rubro=&pagina=&tamanio=
GET    /api/productos/opciones

GET    /api/oportunidades             ?q=&etapaId=&responsableId=&estado=&empresaId=&pagina=&tamanio=
GET    /api/oportunidades/{id}
POST   /api/oportunidades             → 201
PUT    /api/oportunidades/{id}
PATCH  /api/oportunidades/{id}/etapa  { etapaId, observacion, motivoPerdidaId }
GET    /api/oportunidades/{id}/historial-etapas

GET    /api/embudo                    ?responsableId=&zona=
GET    /api/etapas
GET    /api/origenes
GET    /api/motivos-perdida
GET    /api/enums                     → opciones de todos los enums, con etiquetas
```

Ninguna requiere autenticación salvo `/api/auth/login`. No hay ningún endpoint `DELETE`: las bajas
de empresas y contactos son lógicas, vía `PATCH /estado`.

## Qué queda fuera de esta entrega

- Frontend (React) y dockerización de todo el proyecto: es el siguiente paso.
- ABM de usuarios por pantalla.
- Permisos efectivos por rol (el rol viaja en el token, pero no restringe nada todavía;
  `@EnableMethodSecurity` está activo pero sin ningún `@PreAuthorize`).
- Actividades e historial comercial visible (el historial de cambios de etapa **sí** se escribe en
  cada alta y cada cambio, en `historial_etapas`, aunque no haya pantalla que lo muestre).
- Pantallas de configuración de catálogos (etapas, orígenes, motivos de pérdida ya viven en
  colecciones de Mongo, listas para que la próxima entrega solo agregue las pantallas).
- Cierre completo de oportunidades con motivo de pérdida obligatorio (hoy es opcional).
- Inteligencia artificial, reportes, notificaciones, exportación, integraciones, facturación, stock.

## Decisiones que preparan la próxima entrega

1. Etapas, orígenes y motivos de pérdida viven en colecciones de Mongo, no como enums.
2. El usuario tiene rol y el token lo transporta desde el día uno; falta solo activar las
   restricciones con `@PreAuthorize`.
3. El historial de cambios de etapa ya se escribe, incluido el alta inicial de cada oportunidad.
4. Auditoría (`creadoEn/Por`, `modificadoEn/Por`) en todos los documentos vía `BaseDocument` +
   `AuditorAware`.
5. Baja lógica desde el inicio: no hay ningún `DELETE` en la API.
6. Estado (`EstadoOportunidad`, fijo) y etapa (`Etapa`, configurable) están separados, con la
   validación de compatibilidad centralizada en `OportunidadService`.
7. Organización por módulos verticales: cada carpeta de dominio (`empresa`, `contacto`,
   `oportunidad`, etc.) tiene su documento, repositorio, servicio, controlador y DTOs. Agregar
   "actividades" en la próxima entrega debería ser crear una carpeta nueva.
8. DTOs separados de los documentos de Mongo, con `*Mapper` explícitos (`@Component`), y sin
   Lombok en ningún lado.
9. Paginación y filtros dinámicos (con `MongoTemplate` + `Criteria`) ya resueltos en todos los
   listados.
10. Formato único de error (`ApiError`) en un solo lugar del backend
    (`GlobalExceptionHandler` + `SecurityConfig` para 401/403).
11. `/api/enums` centraliza las etiquetas de los enums, para que agregar un valor no obligue a
    tocar el frontend cuando se construya.

## Notas de implementación

- El secreto JWT (`JWT_SECRET`) tiene que tener 32 caracteres o más, o la librería jjwt falla al
  arrancar la aplicación.
- El índice único de `cuit` en `empresas` es `sparse`, para que Mongo no rechace el segundo
  comercio sin CUIT.
- `auto-index-creation: true` está activo en `application.yml`.
- Además del mapeo de excepciones pedido en la especificación, se agregó un manejador para
  `AuthenticationException` (credenciales inválidas en el login), que responde 401 en lugar de un
  500 genérico.
