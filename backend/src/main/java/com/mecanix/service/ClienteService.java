package com.mecanix.service;
import com.mecanix.dto.ClienteRequest;
import com.mecanix.dto.ClienteResponse;
import com.mecanix.exception.BusinessException;
import com.mecanix.exception.ResourceNotFoundException;
import com.mecanix.model.Cliente;
import com.mecanix.repository.ClienteRepository;
import com.mecanix.repository.EmpresaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;
@Service
public class ClienteService {
    private final ClienteRepository repo;
    private final EmpresaRepository empresaRepo;
    public ClienteService(ClienteRepository repo, EmpresaRepository empresaRepo) {
        this.repo = repo; this.empresaRepo = empresaRepo;
    }
    public List<ClienteResponse> listar(Long empresaId) {
        return repo.findAllByEmpresaIdOrderByNomeAsc(empresaId).stream().map(ClienteResponse::from).collect(Collectors.toList());
    }
    public ClienteResponse buscarPorId(Long id, Long empresaId) {
        return ClienteResponse.from(repo.findByIdAndEmpresaId(id, empresaId)
            .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado: " + id)));
    }
    @Transactional
    public ClienteResponse criar(ClienteRequest req, Long empresaId) {
        if (repo.existsByCpfAndEmpresaId(req.getCpf(), empresaId)) throw new BusinessException("CPF já cadastrado");
        Cliente c = new Cliente();
        c.setEmpresa(empresaRepo.getReferenceById(empresaId));
        c.setNome(req.getNome()); c.setCpf(req.getCpf()); c.setEmail(req.getEmail());
        c.setTelefone(req.getTelefone()); c.setEndereco(req.getEndereco());
        return ClienteResponse.from(repo.save(c));
    }
    @Transactional
    public ClienteResponse atualizar(Long id, ClienteRequest req, Long empresaId) {
        Cliente c = repo.findByIdAndEmpresaId(id, empresaId)
            .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado: " + id));
        repo.findByCpfAndEmpresaId(req.getCpf(), empresaId).ifPresent(outro -> {
            if (!outro.getId().equals(id)) throw new BusinessException("CPF já cadastrado em outro cliente");
        });
        c.setNome(req.getNome()); c.setCpf(req.getCpf()); c.setEmail(req.getEmail());
        c.setTelefone(req.getTelefone()); c.setEndereco(req.getEndereco());
        return ClienteResponse.from(repo.save(c));
    }
    @Transactional
    public void deletar(Long id, Long empresaId) {
        if (!repo.existsByIdAndEmpresaId(id, empresaId)) throw new ResourceNotFoundException("Cliente não encontrado");
        repo.deleteById(id);
    }
}
