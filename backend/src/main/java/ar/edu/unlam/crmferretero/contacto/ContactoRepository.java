package ar.edu.unlam.crmferretero.contacto;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ContactoRepository extends MongoRepository<Contacto, String> {
    List<Contacto> findByEmpresaId(String empresaId);
}
