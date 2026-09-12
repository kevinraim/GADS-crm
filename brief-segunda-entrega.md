# Brief técnico — Segunda entrega: CRM Ferretero

## 0. Contexto para quien implemente

Este documento es un pedido de cambios sobre un proyecto ya existente (backend Java 21 / Spring
Boot 3.3.4 / MongoDB, frontend React 18 / Vite / Tailwind). Vos tenés acceso al código; este
documento no asume que tengas contexto de conversaciones previas, así que repite todo lo necesario
para implementar de punta a punta.

El proyecto actual es un CRM de una sola distribuidora ferretera. Este cambio lo convierte en un
producto que se vende a **varias distribuidoras distintas**, cada una con sus propios datos
aislados. Es el cambio más grande de todos los que pide este documento — léelo primero, porque
condiciona el resto.

---

## 1. Choque de vocabulario — leer con atención

El modelo de datos actual ya usa la palabra **"comercio"** para referirse a la ferretería
minorista / corralón / taller / constructora que la distribuidora carga como cliente (la entidad
`Empresa`). Esto **no cambia**.

Lo nuevo es un concepto distinto: la **distribuidora misma como cliente de este producto**. Para
evitar ambigüedad, en todo el código y la UI hay que llamar a esto **`Distribuidora`**, nunca
"comercio". Ejemplos de cómo no confundirse:

- `Empresa` = la ferretería/corralón que un vendedor de la distribuidora visita. Sin cambios de
  nombre.
- `Distribuidora` (entidad **nueva**) = la empresa dueña de la cuenta, la que compró el CRM.

---

## 2. Nuevo modelo de roles

Reemplazar el enum `Rol` actual (`ADMIN`, `VENDEDOR`, `RESPONSABLE_COMERCIAL`) por:

| Rol | Quién es | Alcance |
|---|---|---|
| `ADMIN` | El dueño de la plataforma (superadmin). Una sola persona/equipo, no pertenece a ninguna distribuidora. | Ve y administra **todas** las distribuidoras. Crea distribuidoras nuevas y su primer usuario `ADMIN_COMERCIO`. |
| `ADMIN_COMERCIO` | La persona que usa el CRM para gestionar su propia distribuidora (antes era, básicamente, el `ADMIN` de la entrega 1). | Ve y gestiona **todo** dentro de su propia distribuidora: empresas, contactos, oportunidades, productos, catálogos y usuarios de su distribuidora. |
| `VENDEDOR` | Vendedor de zona de una distribuidora. | Solo ve empresas/contactos/oportunidades que **él creó** o que tiene **asignadas**. |
| `RESPONSABLE_COMERCIAL` | Mismo nivel que `VENDEDOR`. Se mantiene como rol separado por una cuestión de vocabulario/reporting dentro de la distribuidora, **no tiene permisos distintos** de `VENDEDOR`. | Igual que `VENDEDOR`. |

"Asignada" = el campo `responsableComercialId` que ya existe hoy en `Empresa`, `Contacto` y
`Oportunidad`. No hay que crear un mecanismo nuevo de asignación: alcanza con filtrar por ese
campo. El `ADMIN_COMERCIO` sigue siendo quien completa ese campo al crear o editar un registro
(el combo de responsable comercial ya existe en el frontend).

---

## 3. Multi-tenancy: cambios en el modelo de datos

### 3.1 Entidad nueva: `Distribuidora`

Nueva colección `distribuidoras`, extiende `BaseDocument` igual que el resto:

```
id, razonSocial, nombreFantasia, cuit, email, telefono, activa (baja lógica, sin DELETE)
```

CRUD completo, accesible únicamente por `ADMIN` (superadmin). Al dar de alta una distribuidora
nueva, el mismo flujo debería permitir cargar de una vez el primer usuario `ADMIN_COMERCIO` de esa
distribuidora (no hay autoregistro público en esta entrega).

### 3.2 Campo `distribuidoraId` en las colecciones existentes

Agregar `distribuidoraId` (String, referencia a `Distribuidora`) a:

- `Usuario` (excepto los de rol `ADMIN`, que no pertenecen a ninguna distribuidora: el campo queda
  `null`)
- `Empresa`, `Contacto`, `Producto`, `Oportunidad`
- `Etapa`, `Origen`, `MotivoPerdida` (cada distribuidora configura su propio embudo y catálogos,
  ver sección 6)
- Las colecciones nuevas de la sección 5 (`Actividad`, `TipoActividad`)

`historial_etapas` no necesita el campo directamente: se resuelve siempre a través de la
`Oportunidad` a la que pertenece.

### 3.3 Índices y unicidad

Los índices únicos actuales pasan de ser globales a ser **por distribuidora**, salvo el que se
indica como excepción:

- `Empresa.cuit`: único por `distribuidoraId` (dos distribuidoras distintas podrían tener cargado
  el mismo comercio cliente sin conflicto).
- `Producto.codigo`: único por `distribuidoraId`.
- `Usuario.email`: **se mantiene único global** (no por distribuidora), porque el login no pide
  elegir una distribuidora antes de autenticarse — el email por sí solo tiene que identificar al
  usuario y, a través de él, su distribuidora.

### 3.4 Scoping automático (muy importante)

Crear un mecanismo central para no repetir la lógica de filtrado en cada servicio. Sugerencia:

1. Agregar el claim `distribuidoraId` al JWT (`null` para `ADMIN`).
2. En `JwtAuthFilter`, además de la autenticación, poblar un contexto de request (por ejemplo un
   `TenantContext` respaldado por `ThreadLocal` o un bean de scope `request`) con
   `{ usuarioId, rol, distribuidoraId }`.
3. Todos los servicios que hoy arman `Criteria` para listados (empresa, contacto, producto,
   oportunidad, catálogos) deben agregar automáticamente `distribuidoraId = contexto.actual()`
   salvo cuando `rol == ADMIN`. Para `ADMIN`, permitir opcionalmente un parámetro
   `?distribuidoraId=` en los listados para poder inspeccionar una distribuidora puntual; sin ese
   parámetro, `ADMIN` ve agregado de todas.
4. Al crear cualquier documento de una colección con `distribuidoraId`, completarlo automáticamente
   desde el contexto (nunca confiar en un valor que mande el cliente en el body).

### 3.5 Regla de visibilidad fina para `VENDEDOR` / `RESPONSABLE_COMERCIAL`

Además del filtro por `distribuidoraId`, en `Empresa`, `Contacto` y `Oportunidad` estos dos roles
solo deben ver registros donde `creadoPor == su email` **o** `responsableComercialId == su id`.
`ADMIN_COMERCIO` no tiene esta restricción adicional (ve todo lo de su distribuidora).
`Producto` y los catálogos no se filtran por responsable, solo por `distribuidoraId`.

---

## 4. Matriz de permisos

| Recurso | `ADMIN` | `ADMIN_COMERCIO` | `VENDEDOR` / `RESPONSABLE_COMERCIAL` |
|---|---|---|---|
| Distribuidoras | CRUD completo | — (no ve el módulo) | — |
| Usuarios | Todos, cualquier distribuidora | ABM de `VENDEDOR`/`RESPONSABLE_COMERCIAL` de su propia distribuidora; no puede crear otro `ADMIN_COMERCIO` ni `ADMIN` | Ve solo su propio perfil |
| Empresas / Contactos / Oportunidades | Todas, cualquier distribuidora | Todas las de su distribuidora | Solo las creadas por él o asignadas a él |
| Productos | Todos | CRUD completo de los de su distribuidora | Solo lectura, catálogo de su distribuidora |
| Catálogos (Etapa/Origen/MotivoPerdida/TipoActividad) | Todos | CRUD de los de su distribuidora | Solo lectura |
| Embudo | Todas las distribuidoras (agregado o filtrando con `?distribuidoraId=`) | El de su distribuidora | Filtrado a sus propias oportunidades |
| Actividades | Todas | Todas las de su distribuidora | Las propias + las de sus registros asignados |
| Métricas | Agregadas globales, con filtro opcional por distribuidora | Las de su distribuidora | Las propias |

Implementación sugerida: `@PreAuthorize` a nivel de rol grueso (quién puede *llamar* al endpoint) +
el filtrado fino de la sección 3.5 a nivel de servicio (qué subconjunto de datos ve). Una sola
anotación de rol no alcanza para resolver la visibilidad fina.

En el frontend: extender `RutaPrivada` para aceptar una lista de roles permitidos por ruta, y en
`Layout` ocultar las secciones que no correspondan al rol del usuario logueado (por ejemplo,
"Distribuidoras" solo para `ADMIN`; "Usuarios" y "Configuración" solo para `ADMIN`/`ADMIN_COMERCIO`).

---

## 5. Módulo nuevo: actividades e historial comercial

- **`TipoActividad`** (catálogo nuevo, mismo patrón que `Etapa`/`Origen`/`MotivoPerdida`):
  `id, nombre, orden, activo, distribuidoraId`. Ejemplos iniciales: Llamada, Visita a obra/depósito,
  WhatsApp, Email, Reunión.
- **`Actividad`**: `id, tipoActividadId, fecha, descripcion, usuarioId, empresaId, contactoId,
  oportunidadId, distribuidoraId`. Al menos uno de `empresaId`/`contactoId`/`oportunidadId` debe
  estar presente (regla de negocio, 409 si no).
- CRUD de actividades (sin `DELETE`, baja lógica si hiciera falta desactivar alguna cargada por
  error — a definir si es necesario o directamente no se permite borrar/editar pasado cierto
  tiempo).
- **Historial comercial visible**: en el detalle de `Empresa`, `Contacto` y `Oportunidad`, agregar
  una sección que combine cronológicamente las actividades relacionadas y —solo para
  oportunidades— los cambios de etapa (`GET /oportunidades/{id}/historial-etapas`, que ya existe
  en el backend desde la entrega 1 pero no se mostraba en ninguna pantalla). Permitir cargar una
  actividad nueva desde esa misma sección.

---

## 6. Catálogos configurables

`Etapa`, `Origen`, `MotivoPerdida` y `TipoActividad` pasan de ser solo lectura (cargados por seed)
a tener ABM completo, scoped por `distribuidoraId`, con una pantalla de "Configuración" visible
para `ADMIN_COMERCIO`. Al dar de alta una distribuidora nueva, precargarle un set inicial por
default (los mismos valores que usa hoy el `SeedRunner`), editable después por el
`ADMIN_COMERCIO`.

---

## 7. Productos: ABM completo

Pasa de solo lectura a CRUD completo (alta, edición, baja lógica), scoped por `distribuidoraId`,
gestionable por `ADMIN_COMERCIO`.

---

## 8. Cierre de oportunidades con motivo obligatorio

Al mover una oportunidad a una etapa de tipo `PERDIDA`, `motivoPerdidaId` pasa a ser **obligatorio**
(hoy es opcional). Si falta, responder 400 con un mensaje claro para el usuario final. En el
frontend, no dejar confirmar el cambio de etapa a una etapa de tipo `PERDIDA` sin haber elegido un
motivo (ya existe el selector condicional en `OportunidadDetalle`, solo hay que hacerlo requerido).

---

## 9. Métricas

Endpoint nuevo `GET /api/metricas`, scoped igual que el resto (según el rol del usuario
autenticado). Métricas propuestas — son una sugerencia, ajustalas si se te ocurre algo mejor o si
el negocio pide otra cosa:

1. **Oportunidades por etapa**: cantidad y valor total (reutiliza la misma agregación que ya usa
   el embudo).
2. **Tasa de conversión**: ganadas / (ganadas + perdidas), global y por responsable comercial.
3. **Valor total en pipeline abierto**: suma de `valorEstimado` de oportunidades en estado
   `ABIERTA`.
4. **Ranking de responsables comerciales** por cantidad y valor de oportunidades ganadas.
5. **Motivos de pérdida más frecuentes** (top 5).
6. **Comercios sin actividad reciente**: empresas sin ninguna oportunidad ni actividad registrada
   en los últimos N días (configurable, default 30). Esta es la métrica más importante del lote:
   ataca directamente el problema que motivó el proyecto ("no hay seguimiento de clientes
   inactivos").
7. **Comercios nuevos cargados por mes** (tendencia simple de los últimos 6 meses).

Frontend: nueva sección "Métricas" en el sidebar, con tarjetas numéricas simples y, si da el
tiempo, algún gráfico básico (barras para oportunidades por etapa, por ejemplo). Mantener la
misma paleta y densidad del resto de la app, sin sobrecargar de color. Para `ADMIN`, agregar un
selector de distribuidora (o mostrar agregado de todas si no elige ninguna).

---

## 10. Seed y datos de demostración

Reescribir el `SeedRunner` para reflejar multi-tenancy:

- Un usuario `ADMIN` (superadmin), sin `distribuidoraId`.
- Al menos **dos** distribuidoras de demostración, cada una con: 1 `ADMIN_COMERCIO`, 2
  `VENDEDOR`/`RESPONSABLE_COMERCIAL`, su propio set de catálogos (`Etapa`/`Origen`/
  `MotivoPerdida`/`TipoActividad`), productos, empresas, contactos, oportunidades y algunas
  actividades — para poder demostrar que los datos de una distribuidora no se filtran a la otra.
- No hay datos de producción reales todavía, así que se asume que se puede limpiar la base y
  re-seedear en limpio en vez de migrar los datos actuales (de un solo tenant) al nuevo modelo.

---

## 11. Supuestos hechos en este documento — confirmar antes de programar a ciegas

Estas son interpretaciones mías sobre partes que no quedaron 100% explícitas. Si alguna está mal,
avisen antes de implementar esa parte:

1. *"El `ADMIN` puede ver todo y sumar permisos"* se interpretó como que `ADMIN` ya tiene, por
   definición, el superconjunto de todos los permisos posibles — no como una funcionalidad de
   gestión dinámica de permisos granulares por usuario. Si en realidad se quiere que `ADMIN` pueda
   armar permisos custom más finos que el rol (por ejemplo, un `ADMIN_COMERCIO` con algunos
   permisos extra puntuales), eso es una feature aparte que no está diseñada en este documento.
2. Se asume que el catálogo de **productos es propio de cada distribuidora** (cada una carga los
   suyos), no un catálogo global compartido entre todas. Si en realidad debería ser un catálogo
   único para todo el sistema, el diseño de la sección 7 cambia.
3. No hay autoregistro público de distribuidoras nuevas en esta entrega: las alta el `ADMIN`
   manualmente, junto con el primer usuario `ADMIN_COMERCIO`.
4. `RESPONSABLE_COMERCIAL` se mantiene como rol separado de `VENDEDOR` solo por vocabulario/
   reporting (por ejemplo, para poder filtrar métricas o listados por ese rol en particular), sin
   ninguna diferencia de permisos.

---

## 12. Checklist de la entrega

- [ ] `Distribuidora`: modelo, CRUD (solo `ADMIN`), alta conjunta con el primer `ADMIN_COMERCIO`.
- [ ] `distribuidoraId` agregado a `Usuario`, `Empresa`, `Contacto`, `Producto`, `Oportunidad`,
      `Etapa`, `Origen`, `MotivoPerdida`, `Actividad`, `TipoActividad`.
- [ ] Índices únicos de `cuit` y `codigo` recalculados por distribuidora; `email` de usuario sigue
      global.
- [ ] `TenantContext` + scoping automático en `JwtAuthFilter` y en los servicios.
- [ ] Enum `Rol` actualizado: `ADMIN`, `ADMIN_COMERCIO`, `VENDEDOR`, `RESPONSABLE_COMERCIAL`.
- [ ] Regla de visibilidad fina (`creadoPor` / `responsableComercialId`) para `VENDEDOR`/
      `RESPONSABLE_COMERCIAL` en empresas, contactos y oportunidades.
- [ ] `@PreAuthorize` según la matriz de la sección 4 + `RutaPrivada` con roles en el frontend.
- [ ] Módulo `Actividad` + catálogo `TipoActividad` + historial comercial visible en los detalles.
- [ ] Catálogos (`Etapa`/`Origen`/`MotivoPerdida`/`TipoActividad`) con ABM completo y pantalla de
      configuración.
- [ ] Productos con ABM completo.
- [ ] Motivo de pérdida obligatorio al cerrar una oportunidad como perdida.
- [ ] Historial de cambios de etapa visible en el detalle de oportunidad.
- [ ] Endpoint `GET /api/metricas` + pantalla "Métricas" en el frontend.
- [ ] `SeedRunner` reescrito con al menos dos distribuidoras de demostración aisladas entre sí.
- [ ] README actualizado: nuevo modelo de roles, multi-tenancy, usuarios de prueba por
      distribuidora, y cómo probar el aislamiento entre distribuidoras en la demo.
