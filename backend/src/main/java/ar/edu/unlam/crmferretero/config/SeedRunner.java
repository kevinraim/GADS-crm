package ar.edu.unlam.crmferretero.config;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import ar.edu.unlam.crmferretero.actividad.Actividad;
import ar.edu.unlam.crmferretero.actividad.ActividadRepository;
import ar.edu.unlam.crmferretero.catalogo.CatalogoService;
import ar.edu.unlam.crmferretero.catalogo.Etapa;
import ar.edu.unlam.crmferretero.catalogo.EtapaRepository;
import ar.edu.unlam.crmferretero.catalogo.Origen;
import ar.edu.unlam.crmferretero.catalogo.OrigenRepository;
import ar.edu.unlam.crmferretero.catalogo.TipoActividad;
import ar.edu.unlam.crmferretero.catalogo.TipoActividadRepository;
import ar.edu.unlam.crmferretero.contacto.Contacto;
import ar.edu.unlam.crmferretero.contacto.ContactoRepository;
import ar.edu.unlam.crmferretero.distribuidora.Distribuidora;
import ar.edu.unlam.crmferretero.distribuidora.DistribuidoraRepository;
import ar.edu.unlam.crmferretero.empresa.CondicionIva;
import ar.edu.unlam.crmferretero.empresa.Empresa;
import ar.edu.unlam.crmferretero.empresa.EmpresaRepository;
import ar.edu.unlam.crmferretero.empresa.ListaPrecios;
import ar.edu.unlam.crmferretero.empresa.TipoComercio;
import ar.edu.unlam.crmferretero.producto.EscalonDescuento;
import ar.edu.unlam.crmferretero.oportunidad.EstadoOportunidad;
import ar.edu.unlam.crmferretero.oportunidad.ItemOportunidad;
import ar.edu.unlam.crmferretero.oportunidad.Oportunidad;
import ar.edu.unlam.crmferretero.oportunidad.OportunidadRepository;
import ar.edu.unlam.crmferretero.oportunidad.historial.HistorialEtapaService;
import ar.edu.unlam.crmferretero.producto.Producto;
import ar.edu.unlam.crmferretero.producto.ProductoRepository;
import ar.edu.unlam.crmferretero.producto.RubroProducto;
import ar.edu.unlam.crmferretero.producto.UnidadVenta;
import ar.edu.unlam.crmferretero.shared.CondicionPago;
import ar.edu.unlam.crmferretero.shared.EstadoRegistro;
import ar.edu.unlam.crmferretero.usuario.Rol;
import ar.edu.unlam.crmferretero.usuario.Usuario;
import ar.edu.unlam.crmferretero.usuario.UsuarioRepository;

/**
 * Carga los datos iniciales: un ADMIN (superadmin, sin distribuidora) y dos distribuidoras de
 * demostración completamente aisladas entre sí (cada una con su propio ADMIN_COMERCIO, vendedores,
 * catálogos, productos, comercios, contactos, oportunidades y actividades), para poder mostrar en
 * la demo que los datos de una no se filtran a la otra.
 * Idempotente: si ya hay usuarios cargados, no hace nada. Controlado por app.seed.enabled.
 */
@Component
public class SeedRunner implements CommandLineRunner {

    private final DistribuidoraRepository distribuidoraRepository;
    private final EtapaRepository etapaRepository;
    private final OrigenRepository origenRepository;
    private final TipoActividadRepository tipoActividadRepository;
    private final CatalogoService catalogoService;
    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;
    private final EmpresaRepository empresaRepository;
    private final ContactoRepository contactoRepository;
    private final OportunidadRepository oportunidadRepository;
    private final ActividadRepository actividadRepository;
    private final HistorialEtapaService historialEtapaService;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.seed.enabled:true}")
    private boolean seedHabilitado;

    public SeedRunner(DistribuidoraRepository distribuidoraRepository, EtapaRepository etapaRepository,
                       OrigenRepository origenRepository, TipoActividadRepository tipoActividadRepository,
                       CatalogoService catalogoService, UsuarioRepository usuarioRepository,
                       ProductoRepository productoRepository, EmpresaRepository empresaRepository,
                       ContactoRepository contactoRepository, OportunidadRepository oportunidadRepository,
                       ActividadRepository actividadRepository, HistorialEtapaService historialEtapaService,
                       PasswordEncoder passwordEncoder) {
        this.distribuidoraRepository = distribuidoraRepository;
        this.etapaRepository = etapaRepository;
        this.origenRepository = origenRepository;
        this.tipoActividadRepository = tipoActividadRepository;
        this.catalogoService = catalogoService;
        this.usuarioRepository = usuarioRepository;
        this.productoRepository = productoRepository;
        this.empresaRepository = empresaRepository;
        this.contactoRepository = contactoRepository;
        this.oportunidadRepository = oportunidadRepository;
        this.actividadRepository = actividadRepository;
        this.historialEtapaService = historialEtapaService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (!seedHabilitado || usuarioRepository.count() > 0) {
            return;
        }

        usuarioRepository.save(new Usuario("Admin", "de la plataforma", "admin@crmferretero.com",
                passwordEncoder.encode("Admin123!"), Rol.ADMIN, true, null));

        seedDistribuidora(
                "Ferretera del Oeste S.A.", "Ferretera del Oeste", "30711111110",
                "admin@ferreteradeloeste.com.ar",
                new String[]{"Sergio", "Rivas", "sergio.rivas@ferreteradeloeste.com.ar", Rol.VENDEDOR.name()},
                new String[]{"Paula", "Ortiz", "paula.ortiz@ferreteradeloeste.com.ar", Rol.RESPONSABLE_COMERCIAL.name()},
                "Morón", "La Matanza", "Merlo");

        seedDistribuidora(
                "Distribuidora Central de Materiales S.R.L.", "Distribuidora Central", "30722222220",
                "admin@distribuidoracentral.com.ar",
                new String[]{"Marta", "Coria", "marta.coria@distribuidoracentral.com.ar", Rol.VENDEDOR.name()},
                new String[]{"Diego", "Ramallo", "diego.ramallo@distribuidoracentral.com.ar", Rol.RESPONSABLE_COMERCIAL.name()},
                "Tres de Febrero", "Ituzaingó", "Moreno");
    }

    private void seedDistribuidora(String razonSocial, String fantasia, String cuit, String emailAdmin,
                                    String[] vendedor1, String[] vendedor2, String zona1, String zona2, String zona3) {
        Distribuidora distribuidora = new Distribuidora();
        distribuidora.setRazonSocial(razonSocial);
        distribuidora.setNombreFantasia(fantasia);
        distribuidora.setCuit(cuit);
        distribuidora.setEmail(emailAdmin);
        distribuidora.setTelefono("11-4000-0000");
        distribuidora = distribuidoraRepository.save(distribuidora);
        String distribuidoraId = distribuidora.getId();

        Usuario admin = usuarioRepository.save(new Usuario(fantasia.split(" ")[0], "Admin", emailAdmin,
                passwordEncoder.encode("AdminComercio123!"), Rol.ADMIN_COMERCIO, true, distribuidoraId));
        Usuario v1 = usuarioRepository.save(new Usuario(vendedor1[0], vendedor1[1], vendedor1[2],
                passwordEncoder.encode("Vendedor123!"), Rol.valueOf(vendedor1[3]), true, distribuidoraId));
        Usuario v2 = usuarioRepository.save(new Usuario(vendedor2[0], vendedor2[1], vendedor2[2],
                passwordEncoder.encode("Vendedor123!"), Rol.valueOf(vendedor2[3]), true, distribuidoraId));

        catalogoService.precargarDefaults(distribuidoraId);
        List<Etapa> etapas = etapaRepository.findByDistribuidoraIdOrderByOrdenAsc(distribuidoraId);
        List<Origen> origenes = origenRepository.findByDistribuidoraIdOrderByOrdenAsc(distribuidoraId);
        List<TipoActividad> tiposActividad = tipoActividadRepository.findByDistribuidoraIdOrderByOrdenAsc(distribuidoraId);

        List<Producto> productos = seedProductos(distribuidoraId);
        List<Empresa> empresas = seedEmpresas(distribuidoraId, fantasia, v1.getId(), v2.getId(), origenes, zona1, zona2, zona3);
        List<Contacto> contactos = seedContactos(distribuidoraId, empresas, v1.getId(), v2.getId());
        seedOportunidades(distribuidoraId, empresas, v1.getId(), v2.getId(), etapas, origenes, productos);
        seedActividades(distribuidoraId, empresas, contactos, admin.getId(), v1.getId(), tiposActividad);
    }

    private List<Producto> seedProductos(String distribuidoraId) {
        List<Producto> productos = List.of(
                new Producto("ELE-001", "Cable unipolar 2,5 mm²", "Pirelli", RubroProducto.ELECTRICIDAD, UnidadVenta.ROLLO, "rollo 100 m", new BigDecimal("45000"), true, distribuidoraId),
                new Producto("ELE-002", "Llave termomagnética 16A", "Schneider", RubroProducto.ELECTRICIDAD, UnidadVenta.UNIDAD, "unidad", new BigDecimal("8500"), true, distribuidoraId),
                new Producto("PLO-001", "Caño PVC 110 mm x 4 m", "Awaduct", RubroProducto.PLOMERIA, UnidadVenta.UNIDAD, "unidad", new BigDecimal("12500"), true, distribuidoraId),
                new Producto("PLO-002", "Llave de paso esférica 1/2\"", "FV", RubroProducto.PLOMERIA, UnidadVenta.UNIDAD, "unidad", new BigDecimal("4300"), true, distribuidoraId),
                new Producto("HMA-001", "Martillo carpintero 27mm", "Stanley", RubroProducto.HERRAMIENTAS_MANUALES, UnidadVenta.UNIDAD, "unidad", new BigDecimal("11500"), true, distribuidoraId),
                new Producto("HEL-001", "Taladro percutor 1/2\" 750W", "Black+Decker", RubroProducto.HERRAMIENTAS_ELECTRICAS, UnidadVenta.UNIDAD, "unidad", new BigDecimal("95000"), true, distribuidoraId),
                new Producto("FIJ-001", "Tornillo autoperforante 8x1\"", "Cachan", RubroProducto.FIJACIONES, UnidadVenta.CAJA, "caja x 500", new BigDecimal("14000"), true, distribuidoraId),
                new Producto("PIN-001", "Látex interior 20 L", "Alba", RubroProducto.PINTURERIA, UnidadVenta.BALDE, "balde", new BigDecimal("68000"), true, distribuidoraId),
                new Producto("CON-001", "Cemento Portland 50 kg", "Loma Negra", RubroProducto.CONSTRUCCION, UnidadVenta.BOLSA, "bolsa", new BigDecimal("11000"), true, distribuidoraId),
                new Producto("CON-002", "Ladrillo hueco 8x18x33", "Cerámica San Luis", RubroProducto.CONSTRUCCION, UnidadVenta.UNIDAD, "unidad", new BigDecimal("850"), true, distribuidoraId),
                new Producto("SEG-001", "Guantes de descarne", "3M", RubroProducto.SEGURIDAD, UnidadVenta.CAJA, "par, caja x 12", new BigDecimal("32000"), true, distribuidoraId),
                new Producto("JAR-001", "Manguera de riego 1/2\" x 25m", "Gardena", RubroProducto.JARDIN, UnidadVenta.ROLLO, "rollo", new BigDecimal("28000"), true, distribuidoraId)
        );

        // Ejemplo de precio por lista + descuento por cantidad: el cemento es el producto típico
        // que las constructoras (lista A) compran en volumen.
        Producto cemento = productos.stream().filter(p -> p.getCodigo().equals("CON-001")).findFirst().orElseThrow();
        cemento.setPreciosPorLista(java.util.Map.of(
                ListaPrecios.A, new BigDecimal("9500"),
                ListaPrecios.B, new BigDecimal("10200"),
                ListaPrecios.C, new BigDecimal("11000")));
        cemento.setEscalonesDescuento(List.of(
                new EscalonDescuento(new BigDecimal("100"), new BigDecimal("5")),
                new EscalonDescuento(new BigDecimal("500"), new BigDecimal("10"))));

        Producto cable = productos.stream().filter(p -> p.getCodigo().equals("ELE-001")).findFirst().orElseThrow();
        cable.setEscalonesDescuento(List.of(new EscalonDescuento(new BigDecimal("10"), new BigDecimal("8"))));

        return productoRepository.saveAll(productos);
    }

    private List<Empresa> seedEmpresas(String distribuidoraId, String fantasiaDistribuidora, String responsable1, String responsable2,
                                        List<Origen> origenes, String zona1, String zona2, String zona3) {
        String origenReferido = idPorNombre(origenes, "Referido de otro comercio", Origen::getNombre, Origen::getId);
        String origenRuta = idPorNombre(origenes, "Prospección en ruta", Origen::getNombre, Origen::getId);
        String origenWhatsapp = idPorNombre(origenes, "WhatsApp Business", Origen::getNombre, Origen::getId);

        List<Empresa> empresas = new ArrayList<>();
        empresas.add(empresa(distribuidoraId, "Ferretería El Tornillo Feliz S.R.L.", "El Tornillo Feliz " + fantasiaDistribuidora.substring(0, 3),
                "30" + Math.abs((fantasiaDistribuidora + "1").hashCode() % 100000000) + "1", TipoComercio.FERRETERIA_MINORISTA,
                CondicionIva.RESPONSABLE_INSCRIPTO, zona1, CondicionPago.CTA_CTE_30, ListaPrecios.B,
                new BigDecimal("1500000"), EstadoRegistro.CLIENTE, responsable1, origenReferido));
        empresas.add(empresa(distribuidoraId, "Corralón San Cayetano S.A.", "Corralón San Cayetano " + fantasiaDistribuidora.substring(0, 3),
                "30" + Math.abs((fantasiaDistribuidora + "2").hashCode() % 100000000) + "1", TipoComercio.CORRALON,
                CondicionIva.RESPONSABLE_INSCRIPTO, zona2, CondicionPago.CTA_CTE_60, ListaPrecios.A,
                new BigDecimal("4000000"), EstadoRegistro.CLIENTE, responsable1, origenRuta));
        empresas.add(empresa(distribuidoraId, "Taller Mecánico Bianchi", "Taller Bianchi " + fantasiaDistribuidora.substring(0, 3),
                null, TipoComercio.TALLER, CondicionIva.MONOTRIBUTO, zona3, CondicionPago.CONTADO, ListaPrecios.C,
                new BigDecimal("300000"), EstadoRegistro.POTENCIAL, responsable2, origenWhatsapp));
        empresas.add(empresa(distribuidoraId, "Constructora Rioplatense S.A.", "Rioplatense " + fantasiaDistribuidora.substring(0, 3),
                null, TipoComercio.CONSTRUCTORA, CondicionIva.RESPONSABLE_INSCRIPTO, zona2, CondicionPago.CTA_CTE_30, ListaPrecios.A,
                new BigDecimal("6000000"), EstadoRegistro.POTENCIAL, responsable2, origenReferido));

        return empresaRepository.saveAll(empresas);
    }

    private Empresa empresa(String distribuidoraId, String razonSocial, String fantasia, String cuit, TipoComercio tipo,
                             CondicionIva iva, String zona, CondicionPago condicionPago, ListaPrecios lista, BigDecimal limite,
                             EstadoRegistro estado, String responsableId, String origenId) {
        Empresa empresa = new Empresa();
        empresa.setRazonSocial(razonSocial);
        empresa.setNombreFantasia(fantasia);
        empresa.setCuit(cuit);
        empresa.setTipoComercio(tipo);
        empresa.setCondicionIva(iva);
        empresa.setZonaReparto(zona);
        empresa.setCondicionPagoHabitual(condicionPago);
        empresa.setListaPrecios(lista);
        empresa.setLimiteCreditoEstimado(limite);
        empresa.setEstado(estado);
        empresa.setResponsableComercialId(responsableId);
        empresa.setOrigenId(origenId);
        empresa.setEmail("contacto@" + fantasia.toLowerCase().replaceAll("[^a-z0-9]", "") + ".com.ar");
        empresa.setTelefono("11-4000-0000");
        empresa.setDistribuidoraId(distribuidoraId);
        return empresa;
    }

    private List<Contacto> seedContactos(String distribuidoraId, List<Empresa> empresas, String responsable1, String responsable2) {
        List<Contacto> contactos = new ArrayList<>();
        contactos.add(contacto(distribuidoraId, "Roberto", "Fernández", "Dueño", empresas.get(0).getId(), responsable1, EstadoRegistro.CLIENTE));
        contactos.add(contacto(distribuidoraId, "Hugo", "Benítez", "Dueño", empresas.get(1).getId(), responsable1, EstadoRegistro.CLIENTE));
        contactos.add(contacto(distribuidoraId, "Claudio", "Bianchi", "Dueño", empresas.get(2).getId(), responsable2, EstadoRegistro.POTENCIAL));
        contactos.add(contacto(distribuidoraId, "Lucía", "Medina", "Jefa de compras", empresas.get(3).getId(), responsable2, EstadoRegistro.POTENCIAL));
        return contactoRepository.saveAll(contactos);
    }

    private Contacto contacto(String distribuidoraId, String nombre, String apellido, String cargo, String empresaId,
                               String responsableId, EstadoRegistro estado) {
        Contacto contacto = new Contacto();
        contacto.setNombre(nombre);
        contacto.setApellido(apellido);
        contacto.setCargo(cargo);
        contacto.setEmpresaId(empresaId);
        contacto.setResponsableComercialId(responsableId);
        contacto.setEstado(estado);
        contacto.setTelefono("11-5555-" + (1000 + (int) (Math.random() * 8999)));
        contacto.setDistribuidoraId(distribuidoraId);
        return contacto;
    }

    private void seedOportunidades(String distribuidoraId, List<Empresa> empresas, String responsable1, String responsable2,
                                    List<Etapa> etapas, List<Origen> origenes, List<Producto> productos) {
        Etapa consultaRecibida = etapaPorTipo(etapas, "Consulta recibida");
        Etapa negociacion = etapaPorTipo(etapas, "Negociación");
        Etapa pedidoConfirmado = etapaPorTipo(etapas, "Pedido confirmado");
        Etapa perdida = etapaPorTipo(etapas, "Perdida");

        String origenReferido = idPorNombre(origenes, "Referido de otro comercio", Origen::getNombre, Origen::getId);

        Producto cemento = productos.stream().filter(p -> p.getCodigo().equals("CON-001")).findFirst().orElseThrow();
        Producto taladro = productos.stream().filter(p -> p.getCodigo().equals("HEL-001")).findFirst().orElseThrow();
        Producto tornillo = productos.stream().filter(p -> p.getCodigo().equals("FIJ-001")).findFirst().orElseThrow();

        crearOportunidadDemo(distribuidoraId, "Reposición de fijaciones", empresas.get(0).getId(), responsable1,
                consultaRecibida, EstadoOportunidad.ABIERTA, origenReferido, false, List.of(item(tornillo, 10)));

        crearOportunidadDemo(distribuidoraId, "Ampliación de cuenta corriente", empresas.get(1).getId(), responsable1,
                negociacion, EstadoOportunidad.ABIERTA, origenReferido, true, List.of(item(cemento, 200)));

        crearOportunidadDemo(distribuidoraId, "Cotización herramientas eléctricas", empresas.get(2).getId(), responsable2,
                consultaRecibida, EstadoOportunidad.ABIERTA, origenReferido, false, List.of(item(taladro, 2)));

        Oportunidad ganada = crearOportunidadDemo(distribuidoraId, "Obra completa - etapa 1", empresas.get(3).getId(), responsable2,
                pedidoConfirmado, EstadoOportunidad.GANADA, origenReferido, true, List.of(item(cemento, 800)));
        ganada.setFechaRealCierre(LocalDate.now().minusDays(3));
        oportunidadRepository.save(ganada);

        Oportunidad perdidaDemo = crearOportunidadDemo(distribuidoraId, "Pedido de materiales sin cerrar", empresas.get(0).getId(), responsable1,
                perdida, EstadoOportunidad.PERDIDA, origenReferido, false, List.of(item(cemento, 50)));
        perdidaDemo.setFechaRealCierre(LocalDate.now().minusDays(1));
        oportunidadRepository.save(perdidaDemo);
    }

    private record ItemDemo(Producto producto, BigDecimal cantidad) {
    }

    private ItemDemo item(Producto producto, int cantidad) {
        return new ItemDemo(producto, BigDecimal.valueOf(cantidad));
    }

    private Oportunidad crearOportunidadDemo(String distribuidoraId, String titulo, String empresaId, String responsableId,
                                              Etapa etapa, EstadoOportunidad estado, String origenId, boolean requiereAltaCC,
                                              List<ItemDemo> itemsDemo) {
        Oportunidad oportunidad = new Oportunidad();
        oportunidad.setTitulo(titulo);
        oportunidad.setEmpresaId(empresaId);
        oportunidad.setResponsableComercialId(responsableId);
        oportunidad.setEtapaActualId(etapa.getId());
        oportunidad.setEstado(estado);
        oportunidad.setOrigenId(origenId);
        oportunidad.setRequiereAltaCuentaCorriente(requiereAltaCC);
        oportunidad.setFechaEstimadaCierre(LocalDate.now().plusDays(15));
        oportunidad.setDistribuidoraId(distribuidoraId);

        List<ItemOportunidad> items = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;
        for (ItemDemo itemDemo : itemsDemo) {
            BigDecimal subtotal = itemDemo.cantidad().multiply(itemDemo.producto().getPrecioListaReferencia());
            total = total.add(subtotal);
            items.add(new ItemOportunidad(itemDemo.producto().getId(), itemDemo.producto().getNombre(),
                    itemDemo.producto().getUnidadVenta(), itemDemo.cantidad(), itemDemo.producto().getPrecioListaReferencia()));
        }
        oportunidad.setItems(items);
        oportunidad.setValorEstimado(total);

        Oportunidad guardada = oportunidadRepository.save(oportunidad);
        historialEtapaService.registrar(guardada.getId(), null, null, etapa.getId(), etapa.getNombre(),
                "Oportunidad de demostración cargada por el seed");
        return guardada;
    }

    private void seedActividades(String distribuidoraId, List<Empresa> empresas, List<Contacto> contactos,
                                  String usuarioAdminId, String usuarioVendedorId, List<TipoActividad> tiposActividad) {
        String llamada = idPorNombre(tiposActividad, "Llamada", TipoActividad::getNombre, TipoActividad::getId);
        String visita = idPorNombre(tiposActividad, "Visita a obra/depósito", TipoActividad::getNombre, TipoActividad::getId);

        List<Actividad> actividades = new ArrayList<>();
        actividades.add(actividad(distribuidoraId, llamada, "Llamada de seguimiento por el pedido pendiente",
                usuarioVendedorId, empresas.get(0).getId(), null, 2));
        actividades.add(actividad(distribuidoraId, visita, "Visita al depósito para relevar necesidades",
                usuarioVendedorId, empresas.get(1).getId(), null, 10));
        actividades.add(actividad(distribuidoraId, llamada, "Contacto inicial con el nuevo comercio",
                usuarioAdminId, null, contactos.get(2).getId(), 40));
        actividadRepository.saveAll(actividades);
    }

    private Actividad actividad(String distribuidoraId, String tipoActividadId, String descripcion, String usuarioId,
                                 String empresaId, String contactoId, int diasAtras) {
        Actividad actividad = new Actividad();
        actividad.setTipoActividadId(tipoActividadId);
        actividad.setDescripcion(descripcion);
        actividad.setUsuarioId(usuarioId);
        actividad.setEmpresaId(empresaId);
        actividad.setContactoId(contactoId);
        actividad.setFecha(Instant.now().minus(diasAtras, ChronoUnit.DAYS));
        actividad.setDistribuidoraId(distribuidoraId);
        return actividad;
    }

    private Etapa etapaPorTipo(List<Etapa> etapas, String nombre) {
        return etapas.stream().filter(e -> e.getNombre().equals(nombre)).findFirst()
                .orElseThrow(() -> new IllegalStateException("Etapa de seed no encontrada: " + nombre));
    }

    private <T> String idPorNombre(List<T> lista, String nombre, java.util.function.Function<T, String> nombreDe,
                                    java.util.function.Function<T, String> idDe) {
        return lista.stream().filter(item -> nombreDe.apply(item).equals(nombre)).findFirst()
                .map(idDe)
                .orElseThrow(() -> new IllegalStateException("No encontrado en el seed: " + nombre));
    }
}
