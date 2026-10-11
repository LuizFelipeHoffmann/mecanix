package com.mecanix.repository;
import com.mecanix.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
@Repository
public interface ClienteRepository extends JpaRepository<Cliente,Long> {
    Optional<Cliente> findByIdAndEmpresaId(Long id, Long empresaId);
    Optional<Cliente> findByCpfAndEmpresaId(String cpf, Long empresaId);
    boolean existsByCpfAndEmpresaId(String cpf, Long empresaId);
    boolean existsByIdAndEmpresaId(Long id, Long empresaId);
    List<Cliente> findAllByEmpresaIdOrderByNomeAsc(Long empresaId);
    long countByEmpresaId(Long empresaId);
}
