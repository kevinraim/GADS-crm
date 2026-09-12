package ar.edu.unlam.crmferretero.catalogo;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface OrigenRepository extends MongoRepository<Origen, String> {
    List<Origen> findByActivoTrueOrderByOrdenAsc();
}
