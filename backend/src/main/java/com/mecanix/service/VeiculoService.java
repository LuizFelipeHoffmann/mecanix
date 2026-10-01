package com.mecanix.service;
import com.mecanix.dto.VeiculoRequest;
import com.mecanix.dto.VeiculoResponse;
import com.mecanix.exception.BusinessException;
import com.mecanix.exception.ResourceNotFoundException;
import com.mecanix.model.Cliente;
import com.mecanix.model.Veiculo;
import com.mecanix.repository.ClienteRepository;
import com.mecanix.repository.EmpresaRepository;
import com.mecanix.repository.VeiculoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;
@Service
public class VeiculoService {
    private final VeiculoRepository veiRepo;
    private final ClienteRepository cliRepo;
    private final EmpresaRepository empresaRepo;
    public VeiculoService(VeiculoRepository veiRepo, ClienteRepository cliRepo, EmpresaRepository empresaRepo) {
        this.veiRepo = veiRepo; this.cliRepo = cliRepo; this.empresaRepo = empresaRepo;
    }
    public List<VeiculoResponse> listar(Long empresaId) {
        return veiRepo.findByEmpresaId(empresaId).stream().map(VeiculoResponse::from).collect(Collectors.toList());
    }
    public List<VeiculoResponse> listarPorCliente(Long cliId, Long empresaId) {
        return veiRepo.findByClienteIdAndEmpresaId(cliId, empresaId).stream().map(VeiculoResponse::from).collect(Collectors.toList());
    }
    public VeiculoResponse buscarPorId(Long id, Long empresaId) {
        return VeiculoResponse.from(veiRepo.findByIdAndEmpresaId(id, empresaId)
            .orElseThrow(() -> new ResourceNotFoundException("Veículo não encontrado")));
    }
    @Transactional
    public VeiculoResponse criar(VeiculoRequest req, Long empresaId) {
        if (veiRepo.existsByPlacaAndEmpresaId(req.getPlaca(), empresaId)) throw new BusinessException("Placa já cadastrada");
        Cliente c = cliRepo.findByIdAndEmpresaId(req.getClienteId(), empresaId)
            .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado"));
        Veiculo v = new Veiculo();
        v.setEmpresa(empresaRepo.getReferenceById(empresaId));
        v.setCliente(c); v.setMarca(req.getMarca()); v.setModelo(req.getModelo());
        v.setAno(req.getAno()); v.setCor(req.getCor()); v.setPlaca(req.getPlaca());
        v.setQuilometragem(req.getQuilometragem()); v.setTipo(req.getTipo()); v.setObservacoes(req.getObservacoes());
        return VeiculoResponse.from(veiRepo.save(v));
    }
    @Transactional
    public VeiculoResponse atualizar(Long id, VeiculoRequest req, Long empresaId) {
        Veiculo v = veiRepo.findByIdAndEmpresaId(id, empresaId)
            .orElseThrow(() -> new ResourceNotFoundException("Veículo não encontrado"));
        veiRepo.findByPlacaAndEmpresaId(req.getPlaca(), empresaId).ifPresent(outro -> {
            if (!outro.getId().equals(id)) throw new BusinessException("Placa já cadastrada em outro veículo");
        });
        Cliente c = cliRepo.findByIdAndEmpresaId(req.getClienteId(), empresaId)
            .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado"));
        v.setCliente(c); v.setMarca(req.getMarca()); v.setModelo(req.getModelo());
        v.setAno(req.getAno()); v.setCor(req.getCor()); v.setPlaca(req.getPlaca());
        v.setQuilometragem(req.getQuilometragem()); v.setTipo(req.getTipo()); v.setObservacoes(req.getObservacoes());
        return VeiculoResponse.from(veiRepo.save(v));
    }
    @Transactional
    public void deletar(Long id, Long empresaId) {
        if (!veiRepo.existsByIdAndEmpresaId(id, empresaId)) throw new ResourceNotFoundException("Veículo não encontrado");
        veiRepo.deleteById(id);
    }
}
