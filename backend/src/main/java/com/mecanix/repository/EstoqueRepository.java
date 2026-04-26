package com.mecanix.repository;
import com.mecanix.model.EstoqueItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
@Repository
public interface EstoqueRepository extends JpaRepository<EstoqueItem,Long> {
    @Query("SELECT e FROM EstoqueItem e WHERE e.quantidade < e.quantidadeMinima")
    List<EstoqueItem> findAlertasEstoque();
    Optional<EstoqueItem> findByCodigo(String codigo);
    @Query("SELECT e FROM EstoqueItem e JOIN e.tipos t WHERE t = :tipo")
    List<EstoqueItem> findByTipoCompativel(String tipo);
}
