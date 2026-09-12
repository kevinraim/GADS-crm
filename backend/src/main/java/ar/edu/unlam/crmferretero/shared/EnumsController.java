package ar.edu.unlam.crmferretero.shared;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import ar.edu.unlam.crmferretero.catalogo.TipoEtapa;
import ar.edu.unlam.crmferretero.empresa.CondicionIva;
import ar.edu.unlam.crmferretero.empresa.ListaPrecios;
import ar.edu.unlam.crmferretero.empresa.TipoComercio;
import ar.edu.unlam.crmferretero.oportunidad.EstadoOportunidad;
import ar.edu.unlam.crmferretero.producto.RubroProducto;
import ar.edu.unlam.crmferretero.producto.UnidadVenta;
import ar.edu.unlam.crmferretero.usuario.Rol;

/**
 * Devuelve las etiquetas de todos los enums del dominio para que agregar un valor a un enum no
 * obligue a tocar React: el frontend no debe tener ningún switch traduciendo enums a texto.
 */
@RestController
public class EnumsController {

    @GetMapping("/api/enums")
    public Map<String, List<OpcionResponse>> enums() {
        Map<String, List<OpcionResponse>> resultado = new LinkedHashMap<>();
        resultado.put("tipoComercio", opciones(TipoComercio.values(), TipoComercio::name, TipoComercio::getEtiqueta));
        resultado.put("condicionIva", opciones(CondicionIva.values(), CondicionIva::name, CondicionIva::getEtiqueta));
        resultado.put("listaPrecios", opciones(ListaPrecios.values(), ListaPrecios::name, ListaPrecios::getEtiqueta));
        resultado.put("condicionPago", opciones(CondicionPago.values(), CondicionPago::name, CondicionPago::getEtiqueta));
        resultado.put("estadoRegistro", opciones(EstadoRegistro.values(), EstadoRegistro::name, EstadoRegistro::getEtiqueta));
        resultado.put("estadoOportunidad", opciones(EstadoOportunidad.values(), EstadoOportunidad::name, EstadoOportunidad::getEtiqueta));
        resultado.put("rubroProducto", opciones(RubroProducto.values(), RubroProducto::name, RubroProducto::getEtiqueta));
        resultado.put("unidadVenta", opciones(UnidadVenta.values(), UnidadVenta::name, UnidadVenta::getEtiqueta));
        resultado.put("rol", opciones(Rol.values(), Rol::name, Rol::getEtiqueta));
        resultado.put("tipoEtapa", opciones(TipoEtapa.values(), TipoEtapa::name, TipoEtapa::getEtiqueta));
        return resultado;
    }

    private <T> List<OpcionResponse> opciones(T[] valores, java.util.function.Function<T, String> valor,
                                               java.util.function.Function<T, String> etiqueta) {
        return java.util.Arrays.stream(valores)
                .map(item -> new OpcionResponse(valor.apply(item), etiqueta.apply(item)))
                .toList();
    }
}
