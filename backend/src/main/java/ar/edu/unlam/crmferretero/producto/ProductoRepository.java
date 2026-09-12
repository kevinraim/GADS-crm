package ar.edu.unlam.crmferretero.producto;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductoRepository extends MongoRepository<Producto, String> {
    Optional<Producto> findByCodigoAndDistribuidoraId(String codigo, String distribuidoraId);
}
