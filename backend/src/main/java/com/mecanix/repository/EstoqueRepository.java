package com.mecanix.repository;
import com.mecanix.model.EstoqueItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
@Repository
public interface EstoqueRepository extends JpaRepository<EstoqueItem,Long> {
    List<EstoqueItem> findByEmpresaId(Long empresaId);
    @Query("SELECT e FROM EstoqueItem e WHERE e.empresa.id = :empresaId AND e.quantidade < e.quantidadeMinima")
    List<EstoqueItem> findAlertasEstoque(@Param("empresaId") Long empresaId);
    Optional<EstoqueItem> findByCodigoAndEmpresaId(String codigo, Long empresaId);
    Optional<EstoqueItem> findByIdAndEmpresaId(Long id, Long empresaId);
    boolean existsByIdAndEmpresaId(Long id, Long empresaId);
    @Query("SELECT e FROM EstoqueItem e JOIN e.tipos t WHERE t = :tipo AND e.empresa.id = :empresaId")
    List<EstoqueItem> findByTipoCompativel(@Param("tipo") String tipo, @Param("empresaId") Long empresaId);
}
