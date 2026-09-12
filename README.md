# CRM Ferretero

CRM especializado en distribuidoras mayoristas de artículos de ferretería y materiales de
construcción, ofrecido como producto **multi-tenant**: varias distribuidoras usan el mismo sistema,
cada una con sus datos completamente aislados. Los "clientes" que carga cada distribuidora son
ferreterías minoristas, corralones, talleres y constructoras (entidad `Empresa`, sin cambios de
nombre respecto de la primera entrega) — no confundir con `Distribuidora`, la empresa dueña de la
cuenta que compró el CRM.

> **Estado de esta entrega (segunda):** multi-tenancy completo, nuevo modelo de roles y permisos,
> módulo de actividades/historial comercial, catálogos configurables con ABM, productos con ABM
> (incluye precio por lista de precios y descuentos por escalón de cantidad), motivo de pérdida
> obligatorio al cerrar una oportunidad, y una pantalla de métricas.

## Stack

- **Backend**: Java 21, Spring Boot 3.3.4, Maven.
- **Base de datos**: MongoDB 7.
- **Seguridad**: Spring Security + JWT (jjwt 0.12.6), stateless.
- **Frontend**: React 18 + Vite, React Router 6, Axios, Tailwind CSS. Sin librería de íconos (SVGs
  propios) y sin drag & drop de terceros (API nativa de HTML5 en el tablero del embudo).
- **Infraestructura**: Docker + Docker Compose. Todo levanta con `docker compose up --build`.
- **Sin Lombok**: por decisión explícita, ningún módulo del backend usa Lombok. Los documentos de
  Mongo son clases Java normales (constructor, getters y setters escritos a mano) y los DTOs son
  `record`.
- **Sin tests**: no se agregó JUnit, Vitest ni ningún framework de testing, según lo pedido.

## Cómo levantarlo con Docker (recomendado)

Requisito: Docker y Docker Compose.

```bash
docker compose up --build
```

- Frontend: http://localhost:3000
- Backend: http://localhost:8080

El `SeedRunner` carga automáticamente catálogos, usuarios y datos de demostración la primera vez
(es idempotente). Los datos de Mongo persisten en el volumen `mongo_data` entre reinicios.

## Cómo correrlo sin Docker (desarrollo)

Requisitos: JDK 21, Maven 3.9+, Node.js 18+, y un MongoDB 7 corriendo en `localhost:27017`
(por ejemplo `docker run -d --name mongo-crm -p 27017:27017 mongo:7`).

**Backend:**

```bash
cd backend
mvn spring-boot:run
```

Se conecta por defecto a `mongodb://localhost:27017/crm_ferretero` y expone la API en
`http://localhost:8080`.

**Frontend** (en otra terminal):

```bash
cd frontend
npm install
npm run dev
```

Se sirve en `http://localhost:5173`, con Vite proxeando `/api` a `http://localhost:8080`.

### Variables de entorno (opcionales)

Si no las definís, se usan los valores por defecto de `application.yml`:

| Variable | Default | Descripción |
|---|---|---|
| `MONGODB_URI` | `mongodb://localhost:27017/crm_ferretero` | Conexión a Mongo |
| `JWT_SECRET` | secreto de ejemplo (32+ caracteres) | **Cambiar en cualquier entorno real** |
| `JWT_VENCIMIENTO_MINUTOS` | `480` | Duración del token |
| `CORS_ALLOWED_ORIGINS` | `http://localhost:5173,http://localhost:3000` | Orígenes permitidos |
| `APP_SEED_ENABLED` | `true` | Poné `false` para arrancar con la base vacía sin que falle |

En `docker-compose.yml` además se pueden sobreescribir `MONGO_USER` y `MONGO_PASSWORD` (usuario y
contraseña root de Mongo, `admin`/`admin123` por defecto).

## Modelo de roles

| Rol | Quién es | Alcance |
|---|---|---|
| `ADMIN` | Superadmin de la plataforma, no pertenece a ninguna distribuidora. | Ve y administra todas las distribuidoras; crea distribuidoras nuevas junto con su primer `ADMIN_COMERCIO`. En el frontend solo ve "Distribuidoras" y "Métricas" (agregadas). |
| `ADMIN_COMERCIO` | Administra su propia distribuidora de punta a punta. | Todo lo de su distribuidora: comercios, contactos, oportunidades, productos, catálogos y sus vendedores. |
| `VENDEDOR` | Vendedor de zona de una distribuidora. | Solo ve/edita comercios, contactos y oportunidades que él creó o que tiene asignados (`responsableComercialId`). |
| `RESPONSABLE_COMERCIAL` | Igual que `VENDEDOR`; se separa solo por vocabulario/reporting. | Idéntico a `VENDEDOR`, sin diferencias de permisos. |

## Multi-tenancy

Cada distribuidora es un tenant aislado. El JWT lleva el claim `distribuidoraId` (`null` para
`ADMIN`); un filtro (`JwtAuthFilter` + `TenantContext`) lo deja disponible en cada request, y todos
los listados y altas de `Empresa`, `Contacto`, `Oportunidad`, `Producto`, `Etapa`, `Origen`,
`MotivoPerdida`, `TipoActividad` y `Actividad` se filtran automáticamente por él
(`AlcanceUtils.porDistribuidora`). El `distribuidoraId` de un alta nunca se toma del body: siempre
sale del token. `ADMIN` puede pasar `?distribuidoraId=` en los listados para inspeccionar una
distribuidora puntual; sin ese parámetro ve el agregado de todas (el embudo y las métricas por
etapa, al agregar todas las distribuidoras, agrupan por estado ABIERTA/GANADA/PERDIDA en vez de por
etapa puntual, porque cada distribuidora configura las suyas).

`VENDEDOR`/`RESPONSABLE_COMERCIAL` tienen además una visibilidad más fina: solo ven, dentro de su
distribuidora, los comercios/contactos/oportunidades que ellos crearon o que tienen asignados.

`Usuario.email` es el único índice que sigue siendo único **global** (no por distribuidora): el
login no pide elegir distribuidora, así que el email tiene que alcanzar para identificar al usuario.

## Usuarios de prueba

Cargados por el seed, contraseñas con BCrypt. Hay dos distribuidoras de demostración, con datos
completamente aislados entre sí (para comprobarlo: iniciar sesión con un vendedor de una y
verificar que no ve nada de la otra).

| Nombre | Email | Contraseña | Rol | Distribuidora |
|---|---|---|---|---|
| Admin de la plataforma | `admin@crmferretero.com` | `Admin123!` | ADMIN | — |
| Admin de Ferretera del Oeste | `admin@ferreteradeloeste.com.ar` | `AdminComercio123!` | ADMIN_COMERCIO | Ferretera del Oeste S.A. |
| Sergio Rivas | `sergio.rivas@ferreteradeloeste.com.ar` | `Vendedor123!` | VENDEDOR | Ferretera del Oeste S.A. |
| Paula Ortiz | `paula.ortiz@ferreteradeloeste.com.ar` | `Vendedor123!` | RESPONSABLE_COMERCIAL | Ferretera del Oeste S.A. |
| Admin de Distribuidora Central | `admin@distribuidoracentral.com.ar` | `AdminComercio123!` | ADMIN_COMERCIO | Distribuidora Central de Materiales S.R.L. |
| Marta Coria | `marta.coria@distribuidoracentral.com.ar` | `Vendedor123!` | VENDEDOR | Distribuidora Central de Materiales S.R.L. |
| Diego Ramallo | `diego.ramallo@distribuidoracentral.com.ar` | `Vendedor123!` | RESPONSABLE_COMERCIAL | Distribuidora Central de Materiales S.R.L. |

## Guion de demostración

Los seis pasos que pide el enunciado se pueden hacer directamente desde la interfaz
(`http://localhost:3000`, o `:5173` en desarrollo): iniciar sesión, cargar un comercio y un
contacto desde "Comercios"/"Contactos", crear una oportunidad, verla en "Embudo", arrastrarla a
otra columna (o cambiarla de etapa desde su detalle) y recargar para comprobar que persiste.

Para demostrar el aislamiento entre distribuidoras: iniciar sesión como
`sergio.rivas@ferreteradeloeste.com.ar`, anotar los comercios que ve, cerrar sesión e iniciar con
`marta.coria@distribuidoracentral.com.ar` — son comercios completamente distintos, aunque ambos
usuarios apunten al mismo backend y misma base de datos.

Alternativamente, el guion de alta/oportunidad/embudo probado directamente contra la API (con un
`ADMIN_COMERCIO`, que es quien puede cargar comercios y oportunidades):

```bash
# 1. Iniciar sesión
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@ferreteradeloeste.com.ar","password":"AdminComercio123!"}'
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
POST   /api/auth/login                       { email, password } → { token, expiraEnSegundos, usuario }
GET    /api/auth/me

GET    /api/distribuidoras                   ADMIN
GET    /api/distribuidoras/{id}              ADMIN
POST   /api/distribuidoras                   ADMIN → { ...datos, admin: { nombre, apellido, email, password } } → 201
PUT    /api/distribuidoras/{id}               ADMIN
PATCH  /api/distribuidoras/{id}/estado        ADMIN → { activo }

GET    /api/usuarios                          activos de la propia distribuidora (para selectores de responsable)
GET    /api/usuarios/administracion           ADMIN | ADMIN_COMERCIO → todos (activos e inactivos) de la distribuidora
POST   /api/usuarios                          ADMIN_COMERCIO → alta de VENDEDOR/RESPONSABLE_COMERCIAL → 201
PATCH  /api/usuarios/{id}/estado              ADMIN | ADMIN_COMERCIO → { activo }

GET    /api/empresas                          ?q=&estado=&tipoComercio=&zona=&responsableId=&distribuidoraId=&pagina=&tamanio=
GET    /api/empresas/opciones
GET    /api/empresas/{id}
POST   /api/empresas                          ADMIN_COMERCIO | VENDEDOR | RESPONSABLE_COMERCIAL → 201
PUT    /api/empresas/{id}                     ADMIN_COMERCIO | VENDEDOR | RESPONSABLE_COMERCIAL
PATCH  /api/empresas/{id}/estado              { estado }

GET    /api/contactos                         ?q=&empresaId=&estado=&responsableId=&distribuidoraId=&pagina=&tamanio=
GET    /api/contactos/{id}
POST   /api/contactos                         ADMIN_COMERCIO | VENDEDOR | RESPONSABLE_COMERCIAL → 201
PUT    /api/contactos/{id}                    ADMIN_COMERCIO | VENDEDOR | RESPONSABLE_COMERCIAL
PATCH  /api/contactos/{id}/estado             { estado }

GET    /api/productos                         ?q=&rubro=&distribuidoraId=&pagina=&tamanio=
GET    /api/productos/opciones
GET    /api/productos/{id}
POST   /api/productos                         ADMIN_COMERCIO → 201
PUT    /api/productos/{id}                    ADMIN_COMERCIO
PATCH  /api/productos/{id}/estado             ADMIN_COMERCIO → { activo }

GET    /api/oportunidades                     ?q=&etapaId=&responsableId=&estado=&empresaId=&distribuidoraId=&pagina=&tamanio=
GET    /api/oportunidades/{id}
POST   /api/oportunidades                     ADMIN_COMERCIO | VENDEDOR | RESPONSABLE_COMERCIAL → 201
PUT    /api/oportunidades/{id}                ADMIN_COMERCIO | VENDEDOR | RESPONSABLE_COMERCIAL
PATCH  /api/oportunidades/{id}/etapa          { etapaId, observacion, motivoPerdidaId }  ← obligatorio si la etapa destino es de tipo PERDIDA
GET    /api/oportunidades/{id}/historial-etapas

GET    /api/actividades                       ?empresaId=|contactoId=|oportunidadId=  → historial comercial combinado con lo anterior en el frontend
POST   /api/actividades                       ADMIN_COMERCIO | VENDEDOR | RESPONSABLE_COMERCIAL → 201 (inmutable, sin edición ni baja)

GET    /api/embudo                            ?responsableId=&zona=&distribuidoraId=
GET    /api/metricas                          ?distribuidoraId=&dias=  (comercios sin actividad reciente; dias default 30)

GET    /api/etapas | /api/origenes | /api/motivos-perdida | /api/tipos-actividad     → activos de la propia distribuidora
GET    .../administracion                     ADMIN_COMERCIO → incluye inactivos
POST/PUT .../{id}                             ADMIN_COMERCIO → alta/edición
PATCH  .../{id}/estado                        ADMIN_COMERCIO → { activo }

GET    /api/enums                             → opciones de todos los enums, con etiquetas (incluye "rol" y "tipoEtapa")
```

Ninguna requiere autenticación salvo `/api/auth/login`. No hay ningún endpoint `DELETE`: todas las
bajas son lógicas, vía `PATCH /estado`. Los endpoints sin rol indicado en la lista aceptan
cualquier usuario autenticado (el filtrado por distribuidora/visibilidad se hace igual a nivel de
servicio).

## Qué queda fuera de esta entrega

- Autoregistro público de distribuidoras: las alta el `ADMIN` a mano, junto con el primer
  `ADMIN_COMERCIO`.
- Gestión de permisos granulares por usuario más allá del rol (un `ADMIN_COMERCIO` con permisos
  extra puntuales, por ejemplo). El rol es, por definición, el techo de lo que puede hacer.
- Edición o baja de actividades ya cargadas: son inmutables, igual que `historial_etapas`.
- Inteligencia artificial, reportes exportables, notificaciones, integraciones, facturación, stock.

## Supuestos e interpretaciones de esta entrega

Sobre partes del pedido que no quedaron 100% explícitas:

1. El catálogo de **productos es propio de cada distribuidora** (cada una carga los suyos), no
   global.
2. El ABM de productos y de catálogos (`Etapa`/`Origen`/`MotivoPerdida`/`TipoActividad`) es
   exclusivo de `ADMIN_COMERCIO`; `ADMIN` solo los lee (agregado o filtrando por
   `?distribuidoraId=`), ya que no pertenece a ninguna distribuidora a la que asociarles un alta.
3. Por el mismo motivo, `ADMIN` no crea/edita comercios, contactos, oportunidades ni actividades
   (esos endpoints están restringidos a `ADMIN_COMERCIO`/`VENDEDOR`/`RESPONSABLE_COMERCIAL`): en el
   frontend, `ADMIN` solo ve "Distribuidoras" y "Métricas".
4. Como cada distribuidora configura sus propias etapas, el embudo y las métricas por etapa, vistos
   por `ADMIN` sin elegir una distribuidora puntual, agrupan por estado (Abiertas/Ganadas/Perdidas)
   en lugar de por etapa configurada.
5. La visibilidad fina de `Actividad` para `VENDEDOR`/`RESPONSABLE_COMERCIAL` no se restringe por
   sí misma: como siempre se pide el historial de un comercio/contacto/oportunidad puntual, y esos
   ya aplican su propio filtro de visibilidad al consultarlos, alcanza con el filtro por
   distribuidora en `Actividad`.

## Precios por lista y descuentos por cantidad

Cada `Producto` puede tener, además del `precioListaReferencia`:

- **`preciosPorLista`**: un precio especial opcional por cada `ListaPrecios` (A/B/C). Al armar los
  ítems de una oportunidad, el precio unitario se sugiere automáticamente resolviendo la lista de
  precios del comercio elegido (`Empresa.listaPrecios`); si el producto no tiene un precio cargado
  para esa lista, se usa el de referencia.
- **`escalonesDescuento`**: descuentos por cantidad (`cantidadMinima`, `descuentoPorcentaje`). Al
  cambiar la cantidad de un ítem, se aplica automáticamente el descuento del escalón de mayor
  `cantidadMinima` que la cantidad alcance.

Importante: en ambos casos el precio unitario resultante es solo una **sugerencia inicial** — el
campo sigue siendo editable a mano en el formulario de oportunidad, igual que en la primera
entrega. El backend no fuerza el precio: sigue confiando en el `precioUnitario` que manda el
cliente por ítem (mismo criterio ya usado desde la entrega 1). El cálculo de la sugerencia vive en
`frontend/src/utils/precios.js`, para no duplicar lógica de negocio de precios en el backend cuando
lo único que se necesita es una sugerencia editable.

## Decisiones de arquitectura

1. Etapas, orígenes, motivos de pérdida y tipos de actividad viven en colecciones de Mongo (no
   enums), scoped por `distribuidoraId`, con ABM para `ADMIN_COMERCIO`.
2. `TenantContext` (`ThreadLocal`, poblado por `JwtAuthFilter` en cada request y limpiado al
   final) + `AlcanceUtils` centralizan el scoping por distribuidora y la visibilidad fina, para no
   repetir la lógica de filtrado en cada servicio.
3. El historial de cambios de etapa se sigue escribiendo en `historial_etapas` (incluido el alta
   inicial), y ahora además se muestra combinado con `Actividad` en el frontend
   (`HistorialComercial`).
4. Auditoría (`creadoEn/Por`, `modificadoEn/Por`) en todos los documentos vía `BaseDocument` +
   `AuditorAware`.
5. Baja lógica en todos lados: no hay ningún `DELETE` en la API.
6. Estado (`EstadoOportunidad`, fijo) y etapa (`Etapa`, configurable) están separados, con la
   validación de compatibilidad centralizada en `OportunidadService`.
7. Organización por módulos verticales: cada carpeta de dominio tiene su documento, repositorio,
   servicio, controlador y DTOs (`distribuidora`, `actividad` y `metrica` son carpetas nuevas de
   esta entrega, siguiendo el mismo patrón).
8. DTOs separados de los documentos de Mongo, con `*Mapper` explícitos (`@Component`), y sin
   Lombok en ningún lado.
9. Paginación y filtros dinámicos (con `MongoTemplate` + `Criteria`) en todos los listados.
10. Formato único de error (`ApiError`) en un solo lugar del backend
    (`GlobalExceptionHandler` + `SecurityConfig` para 401/403).
11. `/api/enums` centraliza las etiquetas de los enums (incluidos `Rol` y `TipoEtapa`), para que
    agregar un valor no obligue a tocar el frontend.
12. Permisos: `@PreAuthorize` a nivel de rol grueso en los controladores (quién puede llamar al
    endpoint) + `AlcanceUtils.porVisibilidadFina` a nivel de servicio (qué subconjunto de datos ve),
    porque una sola anotación de rol no alcanza para resolver la visibilidad fina de
    `VENDEDOR`/`RESPONSABLE_COMERCIAL`. En el frontend, `RutaPrivada` acepta una lista de roles por
    ruta y `Layout` arma el menú según el rol logueado.

## Notas de implementación

- El secreto JWT (`JWT_SECRET`) tiene que tener 32 caracteres o más, o la librería jjwt falla al
  arrancar la aplicación. El token ahora incluye el claim `distribuidoraId` (`null` para `ADMIN`).
- Los índices únicos de `cuit` (en `empresas`) y `codigo` (en `productos`) son compuestos con
  `distribuidoraId` (`@CompoundIndex`), no globales; el de `email` en `usuarios` sigue siendo
  global y de un solo campo.
- `auto-index-creation: true` está activo en `application.yml`.
- Además del mapeo de excepciones pedido en la especificación, se agregó un manejador para
  `AuthenticationException` (credenciales inválidas en el login), que responde 401 en lugar de un
  500 genérico.
- El frontend resuelve todas las etiquetas de enums desde `/api/enums` vía `AuthContext.etiqueta()`;
  no hay ningún `switch` de traducción en el código de React.
