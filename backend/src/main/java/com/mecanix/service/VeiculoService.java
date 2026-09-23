package com.mecanix.service;
import com.mecanix.dto.VeiculoRequest;
import com.mecanix.dto.VeiculoResponse;
import com.mecanix.exception.BusinessException;
import com.mecanix.exception.ResourceNotFoundException;
import com.mecanix.model.Cliente;
import com.mecanix.model.Veiculo;
import com.mecanix.repository.ClienteRepository;
import com.mecanix.repository.VeiculoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;
@Service
public class VeiculoService {
    private final VeiculoRepository veiRepo;
    private final ClienteRepository cliRepo;
    public VeiculoService(VeiculoRepository veiRepo, ClienteRepository cliRepo) {
        this.veiRepo = veiRepo; this.cliRepo = cliRepo;
    }
    public List<VeiculoResponse> listar() {
        return veiRepo.findAll().stream().map(VeiculoResponse::from).collect(Collectors.toList());
    }
    public List<VeiculoResponse> listarPorCliente(Long cliId) {
        return veiRepo.findByClienteId(cliId).stream().map(VeiculoResponse::from).collect(Collectors.toList());
    }
    public VeiculoResponse buscarPorId(Long id) {
        return VeiculoResponse.from(veiRepo.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Veículo não encontrado")));
    }
    @Transactional
    public VeiculoResponse criar(VeiculoRequest req) {
        if (veiRepo.existsByPlaca(req.getPlaca())) throw new BusinessException("Placa já cadastrada");
        Cliente c = cliRepo.findById(req.getClienteId())
            .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado"));
        Veiculo v = new Veiculo();
        v.setCliente(c); v.setMarca(req.getMarca()); v.setModelo(req.getModelo());
        v.setAno(req.getAno()); v.setCor(req.getCor()); v.setPlaca(req.getPlaca());
        v.setQuilometragem(req.getQuilometragem()); v.setTipo(req.getTipo()); v.setObservacoes(req.getObservacoes());
        return VeiculoResponse.from(veiRepo.save(v));
    }
    @Transactional
    public VeiculoResponse atualizar(Long id, VeiculoRequest req) {
        Veiculo v = veiRepo.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Veículo não encontrado"));
        veiRepo.findByPlaca(req.getPlaca()).ifPresent(outro -> {
            if (!outro.getId().equals(id)) throw new BusinessException("Placa já cadastrada em outro veículo");
        });
        Cliente c = cliRepo.findById(req.getClienteId())
            .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado"));
        v.setCliente(c); v.setMarca(req.getMarca()); v.setModelo(req.getModelo());
        v.setAno(req.getAno()); v.setCor(req.getCor()); v.setPlaca(req.getPlaca());
        v.setQuilometragem(req.getQuilometragem()); v.setTipo(req.getTipo()); v.setObservacoes(req.getObservacoes());
        return VeiculoResponse.from(veiRepo.save(v));
    }
    @Transactional
    public void deletar(Long id) {
        if (!veiRepo.existsById(id)) throw new ResourceNotFoundException("Veículo não encontrado");
        veiRepo.deleteById(id);
    }
}
