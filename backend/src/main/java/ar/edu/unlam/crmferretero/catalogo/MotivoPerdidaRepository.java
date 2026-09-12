package ar.edu.unlam.crmferretero.catalogo;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface MotivoPerdidaRepository extends MongoRepository<MotivoPerdida, String> {
    List<MotivoPerdida> findByActivoTrueOrderByOrdenAsc();
    List<MotivoPerdida> findByDistribuidoraIdAndActivoTrueOrderByOrdenAsc(String distribuidoraId);
    List<MotivoPerdida> findByDistribuidoraIdOrderByOrdenAsc(String distribuidoraId);
}
