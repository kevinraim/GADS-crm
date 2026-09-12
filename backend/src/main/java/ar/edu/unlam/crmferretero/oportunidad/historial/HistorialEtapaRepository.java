package ar.edu.unlam.crmferretero.oportunidad.historial;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface HistorialEtapaRepository extends MongoRepository<HistorialEtapa, String> {
    List<HistorialEtapa> findByOportunidadIdOrderByFechaHoraDesc(String oportunidadId);
}
