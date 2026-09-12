package ar.edu.unlam.crmferretero.distribuidora;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface DistribuidoraRepository extends MongoRepository<Distribuidora, String> {
    Optional<Distribuidora> findByCuit(String cuit);
}
