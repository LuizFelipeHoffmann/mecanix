package com.mecanix.repository;
import com.mecanix.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
@Repository
public interface ClienteRepository extends JpaRepository<Cliente,Long> {
    Optional<Cliente> findByCpf(String cpf);
    boolean existsByCpf(String cpf);
    List<Cliente> findAllByOrderByNomeAsc();
}
