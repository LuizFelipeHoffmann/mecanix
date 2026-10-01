package com.mecanix.service;
import com.mecanix.dto.EstoqueRequest;
import com.mecanix.dto.EstoqueResponse;
import com.mecanix.exception.BusinessException;
import com.mecanix.exception.ResourceNotFoundException;
import com.mecanix.model.EstoqueItem;
import com.mecanix.repository.EmpresaRepository;
import com.mecanix.repository.EstoqueRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;
@Service
public class EstoqueService {
    private final EstoqueRepository repo;
    private final EmpresaRepository empresaRepo;
    public EstoqueService(EstoqueRepository repo, EmpresaRepository empresaRepo) {
        this.repo = repo; this.empresaRepo = empresaRepo;
    }
    public List<EstoqueResponse> listar(Long empresaId) {
        return repo.findByEmpresaId(empresaId).stream().map(EstoqueResponse::from).collect(Collectors.toList());
    }
    public List<EstoqueResponse> listarAlertas(Long empresaId) {
        return repo.findAlertasEstoque(empresaId).stream().map(EstoqueResponse::from).collect(Collectors.toList());
    }
    public List<EstoqueResponse> listarPorTipo(String tipo, Long empresaId) {
        return repo.findByTipoCompativel(tipo.toUpperCase(), empresaId).stream().map(EstoqueResponse::from).collect(Collectors.toList());
    }
    public EstoqueResponse buscarPorId(Long id, Long empresaId) {
        return EstoqueResponse.from(repo.findByIdAndEmpresaId(id, empresaId)
            .orElseThrow(() -> new ResourceNotFoundException("Item não encontrado")));
    }
    @Transactional
    public EstoqueResponse criar(EstoqueRequest req, Long empresaId) {
        if (repo.findByCodigoAndEmpresaId(req.getCodigo(), empresaId).isPresent()) throw new BusinessException("Código já cadastrado");
        EstoqueItem e = new EstoqueItem();
        e.setEmpresa(empresaRepo.getReferenceById(empresaId));
        e.setCodigo(req.getCodigo().toUpperCase()); e.setNome(req.getNome()); e.setCategoria(req.getCategoria());
        e.setQuantidade(req.getQuantidade()); e.setQuantidadeMinima(req.getQuantidadeMinima());
        e.setPrecoUnitario(req.getPrecoUnitario()); e.setTipos(req.getTipos());
        return EstoqueResponse.from(repo.save(e));
    }
    @Transactional
    public EstoqueResponse atualizar(Long id, EstoqueRequest req, Long empresaId) {
        EstoqueItem e = repo.findByIdAndEmpresaId(id, empresaId)
            .orElseThrow(() -> new ResourceNotFoundException("Item não encontrado"));
        repo.findByCodigoAndEmpresaId(req.getCodigo(), empresaId).ifPresent(outro -> {
            if (!outro.getId().equals(id)) throw new BusinessException("Código já cadastrado em outro item");
        });
        e.setCodigo(req.getCodigo().toUpperCase()); e.setNome(req.getNome()); e.setCategoria(req.getCategoria());
        e.setQuantidade(req.getQuantidade()); e.setQuantidadeMinima(req.getQuantidadeMinima());
        e.setPrecoUnitario(req.getPrecoUnitario()); e.setTipos(req.getTipos());
        return EstoqueResponse.from(repo.save(e));
    }
    @Transactional
    public void deletar(Long id, Long empresaId) {
        if (!repo.existsByIdAndEmpresaId(id, empresaId)) throw new ResourceNotFoundException("Item não encontrado");
        repo.deleteById(id);
    }
}
