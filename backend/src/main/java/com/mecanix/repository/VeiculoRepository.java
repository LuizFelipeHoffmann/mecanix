package com.mecanix.repository;
import com.mecanix.model.Veiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
@Repository
public interface VeiculoRepository extends JpaRepository<Veiculo,Long> {
    List<Veiculo> findByEmpresaId(Long empresaId);
    List<Veiculo> findByClienteIdAndEmpresaId(Long clienteId, Long empresaId);
    Optional<Veiculo> findByIdAndEmpresaId(Long id, Long empresaId);
    Optional<Veiculo> findByPlacaAndEmpresaId(String placa, Long empresaId);
    boolean existsByPlacaAndEmpresaId(String placa, Long empresaId);
    boolean existsByIdAndEmpresaId(Long id, Long empresaId);
}
