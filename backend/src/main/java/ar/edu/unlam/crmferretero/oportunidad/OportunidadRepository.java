package ar.edu.unlam.crmferretero.oportunidad;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface OportunidadRepository extends MongoRepository<Oportunidad, String> {
}
