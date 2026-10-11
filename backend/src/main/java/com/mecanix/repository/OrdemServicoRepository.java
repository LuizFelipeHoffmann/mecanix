package com.mecanix.repository;
import com.mecanix.model.OrdemServico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
@Repository
public interface OrdemServicoRepository extends JpaRepository<OrdemServico,Long> {
    List<OrdemServico> findByStatusAndEmpresaIdOrderByIdDesc(OrdemServico.StatusOS status, Long empresaId);
    List<OrdemServico> findAllByEmpresaIdOrderByIdDesc(Long empresaId);
    List<OrdemServico> findByEmpresaId(Long empresaId);
    Optional<OrdemServico> findByIdAndEmpresaId(Long id, Long empresaId);
    long countByStatusAndEmpresaId(OrdemServico.StatusOS status, Long empresaId);
}
