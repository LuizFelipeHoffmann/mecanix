package com.mecanix.service;
import com.mecanix.dto.EstoqueRequest;
import com.mecanix.dto.EstoqueResponse;
import com.mecanix.exception.BusinessException;
import com.mecanix.exception.ResourceNotFoundException;
import com.mecanix.model.EstoqueItem;
import com.mecanix.repository.EstoqueRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;
@Service
public class EstoqueService {
    private final EstoqueRepository repo;
    public EstoqueService(EstoqueRepository repo) { this.repo = repo; }
    public List<EstoqueResponse> listar() {
        return repo.findAll().stream().map(EstoqueResponse::from).collect(Collectors.toList());
    }
    public List<EstoqueResponse> listarAlertas() {
        return repo.findAlertasEstoque().stream().map(EstoqueResponse::from).collect(Collectors.toList());
    }
    public List<EstoqueResponse> listarPorTipo(String tipo) {
        return repo.findByTipoCompativel(tipo.toUpperCase()).stream().map(EstoqueResponse::from).collect(Collectors.toList());
    }
    public EstoqueResponse buscarPorId(Long id) {
        return EstoqueResponse.from(repo.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Item não encontrado")));
    }
    @Transactional
    public EstoqueResponse criar(EstoqueRequest req) {
        if (repo.findByCodigo(req.getCodigo()).isPresent()) throw new BusinessException("Código já cadastrado");
        EstoqueItem e = new EstoqueItem();
        e.setCodigo(req.getCodigo().toUpperCase()); e.setNome(req.getNome()); e.setCategoria(req.getCategoria());
        e.setQuantidade(req.getQuantidade()); e.setQuantidadeMinima(req.getQuantidadeMinima());
        e.setPrecoUnitario(req.getPrecoUnitario()); e.setTipos(req.getTipos());
        return EstoqueResponse.from(repo.save(e));
    }
    @Transactional
    public EstoqueResponse atualizar(Long id, EstoqueRequest req) {
        EstoqueItem e = repo.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Item não encontrado"));
        repo.findByCodigo(req.getCodigo()).ifPresent(outro -> {
            if (!outro.getId().equals(id)) throw new BusinessException("Código já cadastrado em outro item");
        });
        e.setCodigo(req.getCodigo().toUpperCase()); e.setNome(req.getNome()); e.setCategoria(req.getCategoria());
        e.setQuantidade(req.getQuantidade()); e.setQuantidadeMinima(req.getQuantidadeMinima());
        e.setPrecoUnitario(req.getPrecoUnitario()); e.setTipos(req.getTipos());
        return EstoqueResponse.from(repo.save(e));
    }
    @Transactional
    public void deletar(Long id) {
        if (!repo.existsById(id)) throw new ResourceNotFoundException("Item não encontrado");
        repo.deleteById(id);
    }
}
