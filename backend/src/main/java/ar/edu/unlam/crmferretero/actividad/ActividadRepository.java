package ar.edu.unlam.crmferretero.actividad;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ActividadRepository extends MongoRepository<Actividad, String> {
}
