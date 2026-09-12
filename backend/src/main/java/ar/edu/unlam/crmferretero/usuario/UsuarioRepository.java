package ar.edu.unlam.crmferretero.usuario;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface UsuarioRepository extends MongoRepository<Usuario, String> {

    Optional<Usuario> findByEmail(String email);

    List<Usuario> findByActivoTrue();

    List<Usuario> findByDistribuidoraIdAndActivoTrue(String distribuidoraId);

    List<Usuario> findByDistribuidoraId(String distribuidoraId);
}
