package ar.edu.unlam.crmferretero.catalogo;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface EtapaRepository extends MongoRepository<Etapa, String> {
    List<Etapa> findByActivaTrueOrderByOrdenAsc();
    List<Etapa> findByDistribuidoraIdAndActivaTrueOrderByOrdenAsc(String distribuidoraId);
    List<Etapa> findByDistribuidoraIdOrderByOrdenAsc(String distribuidoraId);
}
