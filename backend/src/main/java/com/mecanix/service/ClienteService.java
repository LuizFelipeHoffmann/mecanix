package com.mecanix.service;
import com.mecanix.dto.ClienteRequest;
import com.mecanix.dto.ClienteResponse;
import com.mecanix.exception.BusinessException;
import com.mecanix.exception.ResourceNotFoundException;
import com.mecanix.model.Cliente;
import com.mecanix.repository.ClienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;
@Service
public class ClienteService {
    private final ClienteRepository repo;
    public ClienteService(ClienteRepository repo) { this.repo = repo; }
    public List<ClienteResponse> listar() {
        return repo.findAllByOrderByNomeAsc().stream().map(ClienteResponse::from).collect(Collectors.toList());
    }
    public ClienteResponse buscarPorId(Long id) {
        return ClienteResponse.from(repo.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado: " + id)));
    }
    @Transactional
    public ClienteResponse criar(ClienteRequest req) {
        if (repo.existsByCpf(req.getCpf())) throw new BusinessException("CPF já cadastrado");
        Cliente c = new Cliente();
        c.setNome(req.getNome()); c.setCpf(req.getCpf()); c.setEmail(req.getEmail());
        c.setTelefone(req.getTelefone()); c.setEndereco(req.getEndereco());
        return ClienteResponse.from(repo.save(c));
    }
    @Transactional
    public ClienteResponse atualizar(Long id, ClienteRequest req) {
        Cliente c = repo.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado: " + id));
        repo.findByCpf(req.getCpf()).ifPresent(outro -> {
            if (!outro.getId().equals(id)) throw new BusinessException("CPF já cadastrado em outro cliente");
        });
        c.setNome(req.getNome()); c.setCpf(req.getCpf()); c.setEmail(req.getEmail());
        c.setTelefone(req.getTelefone()); c.setEndereco(req.getEndereco());
        return ClienteResponse.from(repo.save(c));
    }
    @Transactional
    public void deletar(Long id) {
        if (!repo.existsById(id)) throw new ResourceNotFoundException("Cliente não encontrado");
        repo.deleteById(id);
    }
}
