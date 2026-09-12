package ar.edu.unlam.crmferretero.catalogo;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface TipoActividadRepository extends MongoRepository<TipoActividad, String> {
    List<TipoActividad> findByDistribuidoraIdAndActivoTrueOrderByOrdenAsc(String distribuidoraId);
    List<TipoActividad> findByDistribuidoraIdOrderByOrdenAsc(String distribuidoraId);
}
