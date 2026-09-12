package ar.edu.unlam.crmferretero.empresa;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface EmpresaRepository extends MongoRepository<Empresa, String> {
    Optional<Empresa> findByCuit(String cuit);
}
