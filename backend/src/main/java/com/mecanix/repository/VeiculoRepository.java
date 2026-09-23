package com.mecanix.repository;
import com.mecanix.model.Veiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
@Repository
public interface VeiculoRepository extends JpaRepository<Veiculo,Long> {
    List<Veiculo> findByClienteId(Long clienteId);
    Optional<Veiculo> findByPlaca(String placa);
    boolean existsByPlaca(String placa);
}
