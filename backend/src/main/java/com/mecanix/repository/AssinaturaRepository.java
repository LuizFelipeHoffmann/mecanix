package com.mecanix.repository;

import com.mecanix.model.Assinatura;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface AssinaturaRepository extends JpaRepository<Assinatura, Long> {
    List<Assinatura> findByEmpresaIdOrderByIdDesc(Long empresaId);
    Optional<Assinatura> findTopByEmpresaIdOrderByIdDesc(Long empresaId);
    Optional<Assinatura> findByReferenciaExterna(String referenciaExterna);
}
