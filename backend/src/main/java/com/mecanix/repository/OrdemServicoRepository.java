package com.mecanix.repository;
import com.mecanix.model.OrdemServico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface OrdemServicoRepository extends JpaRepository<OrdemServico,Long> {
    List<OrdemServico> findByStatusOrderByIdDesc(OrdemServico.StatusOS status);
    List<OrdemServico> findAllByOrderByIdDesc();
    long countByStatus(OrdemServico.StatusOS status);
}
