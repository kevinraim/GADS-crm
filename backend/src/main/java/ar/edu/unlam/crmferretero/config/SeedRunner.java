package ar.edu.unlam.crmferretero.config;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import ar.edu.unlam.crmferretero.catalogo.Etapa;
import ar.edu.unlam.crmferretero.catalogo.EtapaRepository;
import ar.edu.unlam.crmferretero.catalogo.MotivoPerdida;
import ar.edu.unlam.crmferretero.catalogo.MotivoPerdidaRepository;
import ar.edu.unlam.crmferretero.catalogo.Origen;
import ar.edu.unlam.crmferretero.catalogo.OrigenRepository;
import ar.edu.unlam.crmferretero.catalogo.TipoEtapa;
import ar.edu.unlam.crmferretero.contacto.Contacto;
import ar.edu.unlam.crmferretero.contacto.ContactoRepository;
import ar.edu.unlam.crmferretero.empresa.CondicionIva;
import ar.edu.unlam.crmferretero.empresa.Empresa;
import ar.edu.unlam.crmferretero.empresa.EmpresaRepository;
import ar.edu.unlam.crmferretero.empresa.ListaPrecios;
import ar.edu.unlam.crmferretero.empresa.TipoComercio;
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
 * Carga los datos iniciales. Idempotente: cada colección se carga solo si está vacía.
 * Controlado por app.seed.enabled (default true). El seed va tipado y versionado con el código,
 * no como scripts de mongo-init.
 */
@Component
public class SeedRunner implements CommandLineRunner {

    private final EtapaRepository etapaRepository;
    private final OrigenRepository origenRepository;
    private final MotivoPerdidaRepository motivoPerdidaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;
    private final EmpresaRepository empresaRepository;
    private final ContactoRepository contactoRepository;
    private final OportunidadRepository oportunidadRepository;
    private final HistorialEtapaService historialEtapaService;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.seed.enabled:true}")
    private boolean seedHabilitado;

    public SeedRunner(EtapaRepository etapaRepository, OrigenRepository origenRepository,
                       MotivoPerdidaRepository motivoPerdidaRepository, UsuarioRepository usuarioRepository,
                       ProductoRepository productoRepository, EmpresaRepository empresaRepository,
                       ContactoRepository contactoRepository, OportunidadRepository oportunidadRepository,
                       HistorialEtapaService historialEtapaService, PasswordEncoder passwordEncoder) {
        this.etapaRepository = etapaRepository;
        this.origenRepository = origenRepository;
        this.motivoPerdidaRepository = motivoPerdidaRepository;
        this.usuarioRepository = usuarioRepository;
        this.productoRepository = productoRepository;
        this.empresaRepository = empresaRepository;
        this.contactoRepository = contactoRepository;
        this.oportunidadRepository = oportunidadRepository;
        this.historialEtapaService = historialEtapaService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (!seedHabilitado) {
            return;
        }

        List<Etapa> etapas = seedEtapas();
        List<Origen> origenes = seedOrigenes();
        seedMotivos();
        List<Usuario> usuarios = seedUsuarios();
        List<Producto> productos = seedProductos();
        List<Empresa> empresas = seedEmpresas(usuarios, origenes);
        List<Contacto> contactos = seedContactos(empresas, usuarios, origenes);
        seedOportunidades(empresas, contactos, usuarios, etapas, origenes, productos);
    }

    private List<Etapa> seedEtapas() {
        if (etapaRepository.count() > 0) {
            return etapaRepository.findByActivaTrueOrderByOrdenAsc();
        }
        List<Etapa> etapas = List.of(
                new Etapa("Consulta recibida", "Entró un pedido de cotización por WhatsApp, teléfono o mostrador",
                        1, TipoEtapa.ABIERTA, "#94A3B8", true),
                new Etapa("Cotización enviada", "Se mandó la lista de materiales con precios y plazo de entrega",
                        2, TipoEtapa.ABIERTA, "#64748B", true),
                new Etapa("Negociación", "Descuento por volumen, condición de pago, flete, alta de cuenta corriente",
                        3, TipoEtapa.ABIERTA, "#0E5C8A", true),
                new Etapa("Pedido confirmado", "El comercio confirmó y el pedido pasa a logística",
                        4, TipoEtapa.GANADA, "#15803D", true),
                new Etapa("Perdida", "No se concretó, con motivo registrado",
                        5, TipoEtapa.PERDIDA, "#C2410C", true)
        );
        return etapaRepository.saveAll(etapas);
    }

    private List<Origen> seedOrigenes() {
        if (origenRepository.count() > 0) {
            return origenRepository.findByActivoTrueOrderByOrdenAsc();
        }
        List<Origen> origenes = List.of(
                new Origen("Referido de otro comercio", 1, true),
                new Origen("Prospección en ruta", 2, true),
                new Origen("WhatsApp Business", 3, true),
                new Origen("Instagram o Facebook", 4, true),
                new Origen("Google", 5, true),
                new Origen("Cámara de comercio o feria", 6, true),
                new Origen("Cliente existente (recompra)", 7, true),
                new Origen("Mostrador", 8, true)
        );
        return origenRepository.saveAll(origenes);
    }

    private void seedMotivos() {
        if (motivoPerdidaRepository.count() > 0) {
            return;
        }
        List<MotivoPerdida> motivos = List.of(
                new MotivoPerdida("Precio de la competencia", 1, true),
                new MotivoPerdida("Plazo de entrega largo", 2, true),
                new MotivoPerdida("No acepta las condiciones de pago", 3, true),
                new MotivoPerdida("No califica para cuenta corriente", 4, true),
                new MotivoPerdida("No alcanza la compra mínima", 5, true),
                new MotivoPerdida("Zona fuera de reparto", 6, true),
                new MotivoPerdida("Compró directo al fabricante", 7, true),
                new MotivoPerdida("Sin respuesta del cliente", 8, true)
        );
        motivoPerdidaRepository.saveAll(motivos);
    }

    private List<Usuario> seedUsuarios() {
        if (usuarioRepository.count() > 0) {
            return usuarioRepository.findAll();
        }
        List<Usuario> usuarios = List.of(
                new Usuario("Admin", "del sistema", "admin@crmferretero.com",
                        passwordEncoder.encode("Admin123!"), Rol.ADMIN, true),
                new Usuario("Sergio", "Rivas", "sergio.rivas@crmferretero.com",
                        passwordEncoder.encode("Vendedor123!"), Rol.VENDEDOR, true),
                new Usuario("Paula", "Ortiz", "paula.ortiz@crmferretero.com",
                        passwordEncoder.encode("Vendedor123!"), Rol.VENDEDOR, true),
                new Usuario("Marta", "Coria", "marta.coria@crmferretero.com",
                        passwordEncoder.encode("Responsable123!"), Rol.RESPONSABLE_COMERCIAL, true)
        );
        return usuarioRepository.saveAll(usuarios);
    }

    private List<Producto> seedProductos() {
        if (productoRepository.count() > 0) {
            return productoRepository.findAll();
        }
        List<Producto> productos = List.of(
                new Producto("ELE-001", "Cable unipolar 2,5 mm²", "Pirelli", RubroProducto.ELECTRICIDAD, UnidadVenta.ROLLO, "rollo 100 m", new BigDecimal("45000"), true),
                new Producto("ELE-002", "Cable unipolar 1,5 mm²", "Pirelli", RubroProducto.ELECTRICIDAD, UnidadVenta.ROLLO, "rollo 100 m", new BigDecimal("32000"), true),
                new Producto("ELE-003", "Llave termomagnética 16A", "Schneider", RubroProducto.ELECTRICIDAD, UnidadVenta.UNIDAD, "unidad", new BigDecimal("8500"), true),
                new Producto("ELE-004", "Toma corriente doble con tierra", "Sica", RubroProducto.ELECTRICIDAD, UnidadVenta.CAJA, "caja x 10", new BigDecimal("22000"), true),
                new Producto("ELE-005", "Cinta aisladora 19mm x 20m", "3M", RubroProducto.ELECTRICIDAD, UnidadVenta.CAJA, "caja x 10", new BigDecimal("15000"), true),
                new Producto("PLO-001", "Caño PVC 110 mm x 4 m", "Awaduct", RubroProducto.PLOMERIA, UnidadVenta.UNIDAD, "unidad", new BigDecimal("12500"), true),
                new Producto("PLO-002", "Caño PVC 63 mm x 4 m", "Awaduct", RubroProducto.PLOMERIA, UnidadVenta.UNIDAD, "unidad", new BigDecimal("6200"), true),
                new Producto("PLO-003", "Codo PVC 110 mm 90°", "Awaduct", RubroProducto.PLOMERIA, UnidadVenta.CAJA, "caja x 20", new BigDecimal("18000"), true),
                new Producto("PLO-004", "Llave de paso esférica 1/2\"", "FV", RubroProducto.PLOMERIA, UnidadVenta.UNIDAD, "unidad", new BigDecimal("4300"), true),
                new Producto("PLO-005", "Cinta de teflón 18mm", "Genérica", RubroProducto.PLOMERIA, UnidadVenta.CAJA, "caja x 50", new BigDecimal("9000"), true),
                new Producto("HMA-001", "Martillo carpintero 27mm", "Stanley", RubroProducto.HERRAMIENTAS_MANUALES, UnidadVenta.UNIDAD, "unidad", new BigDecimal("11500"), true),
                new Producto("HMA-002", "Juego de destornilladores x 6", "Bahco", RubroProducto.HERRAMIENTAS_MANUALES, UnidadVenta.CAJA, "caja x 1", new BigDecimal("21000"), true),
                new Producto("HMA-003", "Pinza universal 8\"", "Knipex", RubroProducto.HERRAMIENTAS_MANUALES, UnidadVenta.UNIDAD, "unidad", new BigDecimal("18500"), true),
                new Producto("HMA-004", "Cinta métrica 5m", "Stanley", RubroProducto.HERRAMIENTAS_MANUALES, UnidadVenta.UNIDAD, "unidad", new BigDecimal("6900"), true),
                new Producto("HEL-001", "Taladro percutor 1/2\" 750W", "Black+Decker", RubroProducto.HERRAMIENTAS_ELECTRICAS, UnidadVenta.UNIDAD, "unidad", new BigDecimal("95000"), true),
                new Producto("HEL-002", "Amoladora angular 4 1/2\" 850W", "Bosch", RubroProducto.HERRAMIENTAS_ELECTRICAS, UnidadVenta.UNIDAD, "unidad", new BigDecimal("78000"), true),
                new Producto("HEL-003", "Sierra caladora 650W", "Black+Decker", RubroProducto.HERRAMIENTAS_ELECTRICAS, UnidadVenta.UNIDAD, "unidad", new BigDecimal("84000"), true),
                new Producto("FIJ-001", "Tornillo autoperforante 8x1\"", "Cachan", RubroProducto.FIJACIONES, UnidadVenta.CAJA, "caja x 500", new BigDecimal("14000"), true),
                new Producto("FIJ-002", "Tarugo plástico S8", "Cachan", RubroProducto.FIJACIONES, UnidadVenta.CAJA, "caja x 100", new BigDecimal("5200"), true),
                new Producto("FIJ-003", "Clavo punta París 2\"", "Acindar", RubroProducto.FIJACIONES, UnidadVenta.KILO, "kilo", new BigDecimal("3100"), true),
                new Producto("FIJ-004", "Bulón hexagonal 1/4 x 2\"", "Cachan", RubroProducto.FIJACIONES, UnidadVenta.CAJA, "caja x 200", new BigDecimal("9800"), true),
                new Producto("PIN-001", "Látex interior 20 L", "Alba", RubroProducto.PINTURERIA, UnidadVenta.BALDE, "balde", new BigDecimal("68000"), true),
                new Producto("PIN-002", "Esmalte sintético 4 L", "Sherwin Williams", RubroProducto.PINTURERIA, UnidadVenta.UNIDAD, "lata", new BigDecimal("42000"), true),
                new Producto("PIN-003", "Rodillo antigota 22cm", "Pintemas", RubroProducto.PINTURERIA, UnidadVenta.UNIDAD, "unidad", new BigDecimal("4800"), true),
                new Producto("PIN-004", "Pincel n°20", "Pintemas", RubroProducto.PINTURERIA, UnidadVenta.UNIDAD, "unidad", new BigDecimal("3200"), true),
                new Producto("CON-001", "Cemento Portland 50 kg", "Loma Negra", RubroProducto.CONSTRUCCION, UnidadVenta.BOLSA, "bolsa", new BigDecimal("11000"), true),
                new Producto("CON-002", "Cal hidratada 25 kg", "Loma Negra", RubroProducto.CONSTRUCCION, UnidadVenta.BOLSA, "bolsa", new BigDecimal("6500"), true),
                new Producto("CON-003", "Arena fina m³", "Genérica", RubroProducto.CONSTRUCCION, UnidadVenta.UNIDAD, "metro cúbico", new BigDecimal("35000"), true),
                new Producto("CON-004", "Ladrillo hueco 8x18x33", "Cerámica San Luis", RubroProducto.CONSTRUCCION, UnidadVenta.UNIDAD, "unidad", new BigDecimal("850"), true),
                new Producto("SEG-001", "Guantes de descarne", "3M", RubroProducto.SEGURIDAD, UnidadVenta.CAJA, "par, caja x 12", new BigDecimal("32000"), true),
                new Producto("SEG-002", "Casco de seguridad", "3M", RubroProducto.SEGURIDAD, UnidadVenta.UNIDAD, "unidad", new BigDecimal("9500"), true),
                new Producto("JAR-001", "Manguera de riego 1/2\" x 25m", "Gardena", RubroProducto.JARDIN, UnidadVenta.ROLLO, "rollo", new BigDecimal("28000"), true)
        );
        return productoRepository.saveAll(productos);
    }

    private List<Empresa> seedEmpresas(List<Usuario> usuarios, List<Origen> origenes) {
        if (empresaRepository.count() > 0) {
            return empresaRepository.findAll();
        }

        String sergio = idDeVendedor(usuarios, "sergio.rivas@crmferretero.com");
        String paula = idDeVendedor(usuarios, "paula.ortiz@crmferretero.com");
        String marta = idDeVendedor(usuarios, "marta.coria@crmferretero.com");

        String origenReferido = idDeOrigen(origenes, "Referido de otro comercio");
        String origenRuta = idDeOrigen(origenes, "Prospección en ruta");
        String origenWhatsapp = idDeOrigen(origenes, "WhatsApp Business");
        String origenGoogle = idDeOrigen(origenes, "Google");
        String origenFeria = idDeOrigen(origenes, "Cámara de comercio o feria");
        String origenMostrador = idDeOrigen(origenes, "Mostrador");

        List<Empresa> empresas = new ArrayList<>();
        empresas.add(empresa("Ferretería El Tornillo Feliz S.R.L.", "El Tornillo Feliz", "30712345671",
                TipoComercio.FERRETERIA_MINORISTA, CondicionIva.RESPONSABLE_INSCRIPTO, "Morón",
                CondicionPago.CTA_CTE_30, ListaPrecios.B, new BigDecimal("1500000"),
                EstadoRegistro.CLIENTE, sergio, origenReferido));
        empresas.add(empresa("Corralón San Cayetano S.A.", "Corralón San Cayetano", "30712345672",
                TipoComercio.CORRALON, CondicionIva.RESPONSABLE_INSCRIPTO, "La Matanza",
                CondicionPago.CTA_CTE_60, ListaPrecios.A, new BigDecimal("4000000"),
                EstadoRegistro.CLIENTE, sergio, origenRuta));
        empresas.add(empresa("Taller Mecánico Bianchi", "Taller Bianchi", "30712345673",
                TipoComercio.TALLER, CondicionIva.MONOTRIBUTO, "Merlo",
                CondicionPago.CONTADO, ListaPrecios.C, new BigDecimal("300000"),
                EstadoRegistro.POTENCIAL, paula, origenWhatsapp));
        empresas.add(empresa("Constructora Rioplatense S.A.", "Rioplatense", "30712345674",
                TipoComercio.CONSTRUCTORA, CondicionIva.RESPONSABLE_INSCRIPTO, "Tres de Febrero",
                CondicionPago.CTA_CTE_30, ListaPrecios.A, new BigDecimal("6000000"),
                EstadoRegistro.CLIENTE, marta, origenFeria));
        empresas.add(empresa("Ferretería Industrial del Oeste", "Industrial del Oeste", "30712345675",
                TipoComercio.INDUSTRIA, CondicionIva.RESPONSABLE_INSCRIPTO, "Ituzaingó",
                CondicionPago.CTA_CTE_15, ListaPrecios.B, new BigDecimal("2200000"),
                EstadoRegistro.POTENCIAL, paula, origenGoogle));
        empresas.add(empresa("Depósito de Materiales Don Aurelio", "Don Aurelio", null,
                TipoComercio.CORRALON, CondicionIva.MONOTRIBUTO, "Moreno",
                CondicionPago.CONTADO, ListaPrecios.C, new BigDecimal("500000"),
                EstadoRegistro.POTENCIAL, sergio, origenMostrador));

        return empresaRepository.saveAll(empresas);
    }

    private Empresa empresa(String razonSocial, String fantasia, String cuit, TipoComercio tipo, CondicionIva iva,
                             String zona, CondicionPago condicionPago, ListaPrecios lista, BigDecimal limite,
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
        empresa.setEmail("contacto@" + fantasia.toLowerCase().replace(" ", "") + ".com.ar");
        empresa.setTelefono("11-4000-0000");
        return empresa;
    }

    private List<Contacto> seedContactos(List<Empresa> empresas, List<Usuario> usuarios, List<Origen> origenes) {
        if (contactoRepository.count() > 0) {
            return contactoRepository.findAll();
        }

        String sergio = idDeVendedor(usuarios, "sergio.rivas@crmferretero.com");
        String paula = idDeVendedor(usuarios, "paula.ortiz@crmferretero.com");
        String marta = idDeVendedor(usuarios, "marta.coria@crmferretero.com");
        String origenMostrador = idDeOrigen(origenes, "Mostrador");

        String tornillo = idDeEmpresa(empresas, "El Tornillo Feliz");
        String sanCayetano = idDeEmpresa(empresas, "Corralón San Cayetano");
        String bianchi = idDeEmpresa(empresas, "Taller Bianchi");
        String rioplatense = idDeEmpresa(empresas, "Rioplatense");
        String industrialOeste = idDeEmpresa(empresas, "Industrial del Oeste");
        String donAurelio = idDeEmpresa(empresas, "Don Aurelio");

        List<Contacto> contactos = new ArrayList<>();
        contactos.add(contacto("Roberto", "Fernández", "Dueño", tornillo, sergio, EstadoRegistro.CLIENTE, null));
        contactos.add(contacto("Marisa", "Gómez", "Encargada de compras", tornillo, sergio, EstadoRegistro.CLIENTE, null));
        contactos.add(contacto("Hugo", "Benítez", "Dueño", sanCayetano, sergio, EstadoRegistro.CLIENTE, null));
        contactos.add(contacto("Claudio", "Bianchi", "Dueño", bianchi, paula, EstadoRegistro.POTENCIAL, null));
        contactos.add(contacto("Lucía", "Medina", "Jefa de compras", rioplatense, marta, EstadoRegistro.CLIENTE, null));
        contactos.add(contacto("Diego", "Ramallo", "Capataz de obra", rioplatense, marta, EstadoRegistro.CLIENTE, null));
        contactos.add(contacto("Andrea", "Suárez", "Encargada de depósito", industrialOeste, paula, EstadoRegistro.POTENCIAL, null));
        // Contacto sin empresa: cliente individual llegado por mostrador
        contactos.add(contacto("Walter", "Ibáñez", "Dueño", null, sergio, EstadoRegistro.POTENCIAL, origenMostrador));

        return contactoRepository.saveAll(contactos);
    }

    private Contacto contacto(String nombre, String apellido, String cargo, String empresaId, String responsableId,
                               EstadoRegistro estado, String origenId) {
        Contacto contacto = new Contacto();
        contacto.setNombre(nombre);
        contacto.setApellido(apellido);
        contacto.setCargo(cargo);
        contacto.setEmpresaId(empresaId);
        contacto.setResponsableComercialId(responsableId);
        contacto.setEstado(estado);
        contacto.setOrigenId(origenId);
        contacto.setTelefono("11-5555-" + (1000 + (int) (Math.random() * 8999)));
        return contacto;
    }

    private void seedOportunidades(List<Empresa> empresas, List<Contacto> contactos, List<Usuario> usuarios,
                                    List<Etapa> etapas, List<Origen> origenes, List<Producto> productos) {
        if (oportunidadRepository.count() > 0) {
            return;
        }

        String sergio = idDeVendedor(usuarios, "sergio.rivas@crmferretero.com");
        String paula = idDeVendedor(usuarios, "paula.ortiz@crmferretero.com");
        String marta = idDeVendedor(usuarios, "marta.coria@crmferretero.com");

        Etapa consultaRecibida = idDeEtapa(etapas, "Consulta recibida");
        Etapa cotizacionEnviada = idDeEtapa(etapas, "Cotización enviada");
        Etapa negociacion = idDeEtapa(etapas, "Negociación");
        Etapa pedidoConfirmado = idDeEtapa(etapas, "Pedido confirmado");
        Etapa perdida = idDeEtapa(etapas, "Perdida");

        String origenReferido = idDeOrigen(origenes, "Referido de otro comercio");
        String origenWhatsapp = idDeOrigen(origenes, "WhatsApp Business");

        String tornillo = idDeEmpresa(empresas, "El Tornillo Feliz");
        String sanCayetano = idDeEmpresa(empresas, "Corralón San Cayetano");
        String bianchi = idDeEmpresa(empresas, "Taller Bianchi");
        String rioplatense = idDeEmpresa(empresas, "Rioplatense");
        String industrialOeste = idDeEmpresa(empresas, "Industrial del Oeste");
        String donAurelio = idDeEmpresa(empresas, "Don Aurelio");

        Producto cemento = idDeProducto(productos, "CON-001");
        Producto cable25 = idDeProducto(productos, "ELE-001");
        Producto canoPvc = idDeProducto(productos, "PLO-001");
        Producto tornilloAuto = idDeProducto(productos, "FIJ-001");
        Producto latex = idDeProducto(productos, "PIN-001");
        Producto taladro = idDeProducto(productos, "HEL-001");
        Producto guantes = idDeProducto(productos, "SEG-001");
        Producto ladrillo = idDeProducto(productos, "CON-004");

        crearOportunidadDemo("Reposición de fijaciones y pinturería", tornillo, null, sergio,
                consultaRecibida, EstadoOportunidad.ABIERTA, origenWhatsapp, false, List.of(
                        item(tornilloAuto, 10), item(latex, 3)));

        crearOportunidadDemo("Pedido de materiales para obra Ituzaingó", industrialOeste, null, paula,
                cotizacionEnviada, EstadoOportunidad.ABIERTA, origenReferido, false, List.of(
                        item(cable25, 8), item(canoPvc, 15)));

        crearOportunidadDemo("Ampliación de cuenta corriente y pedido grande", sanCayetano, null, sergio,
                negociacion, EstadoOportunidad.ABIERTA, origenReferido, true, List.of(
                        item(cemento, 200), item(ladrillo, 5000)));

        crearOportunidadDemo("Cotización herramientas eléctricas", bianchi, null, paula,
                consultaRecibida, EstadoOportunidad.ABIERTA, origenWhatsapp, false, List.of(
                        item(taladro, 2)));

        crearOportunidadDemo("Provisión mensual obra Rioplatense", rioplatense, null, marta,
                negociacion, EstadoOportunidad.ABIERTA, origenReferido, true, List.of(
                        item(cemento, 500), item(cable25, 20)));

        crearOportunidadDemo("Pedido de seguridad e insumos varios", industrialOeste, null, paula,
                cotizacionEnviada, EstadoOportunidad.ABIERTA, origenReferido, false, List.of(
                        item(guantes, 15)));

        Oportunidad ganada = crearOportunidadDemo("Obra completa Rioplatense - etapa 1", rioplatense, null, marta,
                pedidoConfirmado, EstadoOportunidad.GANADA, origenReferido, true, List.of(
                        item(cemento, 800), item(ladrillo, 12000), item(cable25, 40)));
        ganada.setFechaRealCierre(LocalDate.now().minusDays(3));
        oportunidadRepository.save(ganada);

        Oportunidad perdidaDemo = crearOportunidadDemo("Pedido de materiales - Don Aurelio", donAurelio, null, sergio,
                perdida, EstadoOportunidad.PERDIDA, null, false, List.of(item(cemento, 50)));
        perdidaDemo.setFechaRealCierre(LocalDate.now().minusDays(1));
        oportunidadRepository.save(perdidaDemo);
    }

    private record ItemDemo(Producto producto, java.math.BigDecimal cantidad) {
    }

    private ItemDemo item(Producto producto, int cantidad) {
        return new ItemDemo(producto, java.math.BigDecimal.valueOf(cantidad));
    }

    private Oportunidad crearOportunidadDemo(String titulo, String empresaId, String contactoId, String responsableId,
                                              Etapa etapa, EstadoOportunidad estado, String origenId,
                                              boolean requiereAltaCC, List<ItemDemo> itemsDemo) {
        Oportunidad oportunidad = new Oportunidad();
        oportunidad.setTitulo(titulo);
        oportunidad.setEmpresaId(empresaId);
        oportunidad.setContactoId(contactoId);
        oportunidad.setResponsableComercialId(responsableId);
        oportunidad.setEtapaActualId(etapa.getId());
        oportunidad.setEstado(estado);
        oportunidad.setOrigenId(origenId);
        oportunidad.setRequiereAltaCuentaCorriente(requiereAltaCC);
        oportunidad.setFechaEstimadaCierre(LocalDate.now().plusDays(15));

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

    private String idDeVendedor(List<Usuario> usuarios, String email) {
        return usuarios.stream().filter(u -> u.getEmail().equals(email)).findFirst()
                .orElseThrow(() -> new IllegalStateException("Usuario de seed no encontrado: " + email))
                .getId();
    }

    private String idDeOrigen(List<Origen> origenes, String nombre) {
        return origenes.stream().filter(o -> o.getNombre().equals(nombre)).findFirst()
                .orElseThrow(() -> new IllegalStateException("Origen de seed no encontrado: " + nombre))
                .getId();
    }

    private Etapa idDeEtapa(List<Etapa> etapas, String nombre) {
        return etapas.stream().filter(e -> e.getNombre().equals(nombre)).findFirst()
                .orElseThrow(() -> new IllegalStateException("Etapa de seed no encontrada: " + nombre));
    }

    private String idDeEmpresa(List<Empresa> empresas, String fantasia) {
        return empresas.stream().filter(e -> fantasia.equals(e.getNombreFantasia())).findFirst()
                .orElseThrow(() -> new IllegalStateException("Empresa de seed no encontrada: " + fantasia))
                .getId();
    }

    private Producto idDeProducto(List<Producto> productos, String codigo) {
        return productos.stream().filter(p -> p.getCodigo().equals(codigo)).findFirst()
                .orElseThrow(() -> new IllegalStateException("Producto de seed no encontrado: " + codigo));
    }
}
