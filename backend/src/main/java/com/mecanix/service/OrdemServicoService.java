package com.mecanix.service;
import com.mecanix.dto.*;
import com.mecanix.exception.BusinessException;
import com.mecanix.exception.ResourceNotFoundException;
import com.mecanix.model.*;
import com.mecanix.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrdemServicoService {
    private final OrdemServicoRepository ordemRepo;
    private final ClienteRepository cliRepo;
    private final VeiculoRepository veiRepo;
    private final EstoqueRepository estRepo;

    public OrdemServicoService(OrdemServicoRepository ordemRepo, ClienteRepository cliRepo,
                               VeiculoRepository veiRepo, EstoqueRepository estRepo) {
        this.ordemRepo = ordemRepo; this.cliRepo = cliRepo;
        this.veiRepo = veiRepo; this.estRepo = estRepo;
    }

    public List<OrdemResponse> listar() {
        return ordemRepo.findAllByOrderByIdDesc().stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<OrdemResponse> listarPorStatus(String status) {
        return ordemRepo.findByStatusOrderByIdDesc(OrdemServico.StatusOS.valueOf(status.toUpperCase()))
            .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public OrdemResponse buscarPorId(Long id) {
        return toResponse(ordemRepo.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("OS não encontrada: " + id)));
    }

    @Transactional
    public OrdemResponse criar(OrdemRequest req) {
        Cliente c = cliRepo.findById(req.getClienteId())
            .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado"));
        Veiculo v = veiRepo.findById(req.getVeiculoId())
            .orElseThrow(() -> new ResourceNotFoundException("Veículo não encontrado"));
        validarEstoque(req.getPecas());

        OrdemServico o = new OrdemServico();
        o.setCliente(c); o.setVeiculo(v); o.setStatus(req.getStatus());
        o.setMecanico(req.getMecanico()); o.setObservacoes(req.getObservacoes());
        o.setData(req.getData() != null ? req.getData() : LocalDate.now());

        if (req.getServicos() != null) {
            for (ItemServicoRequest s : req.getServicos()) {
                ItemServico is = new ItemServico();
                is.setOrdemServico(o); is.setDescricao(s.getDescricao()); is.setValor(s.getValor());
                o.getServicos().add(is);
            }
        }
        if (req.getPecas() != null) {
            for (ItemPecaRequest p : req.getPecas()) {
                EstoqueItem item = estRepo.findById(p.getEstoqueId())
                    .orElseThrow(() -> new ResourceNotFoundException("Peça não encontrada"));
                ItemPeca ip = new ItemPeca();
                ip.setOrdemServico(o); ip.setEstoqueItem(item); ip.setNomePeca(item.getNome());
                ip.setQuantidade(p.getQuantidade());
                ip.setPrecoUnitario(p.getPrecoUnitario() != null ? p.getPrecoUnitario() : item.getPrecoUnitario());
                o.getPecas().add(ip);
                item.setQuantidade(item.getQuantidade() - p.getQuantidade());
                estRepo.save(item);
            }
        }
        return toResponse(ordemRepo.save(o));
    }

    @Transactional
    public OrdemResponse atualizar(Long id, OrdemRequest req) {
        OrdemServico o = ordemRepo.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("OS não encontrada: " + id));

        // Devolve peças ao estoque
        for (ItemPeca p : o.getPecas()) {
            if (p.getEstoqueItem() != null) {
                p.getEstoqueItem().setQuantidade(p.getEstoqueItem().getQuantidade() + p.getQuantidade());
                estRepo.save(p.getEstoqueItem());
            }
        }

        validarEstoque(req.getPecas());

        Cliente c = cliRepo.findById(req.getClienteId())
            .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado"));
        Veiculo v = veiRepo.findById(req.getVeiculoId())
            .orElseThrow(() -> new ResourceNotFoundException("Veículo não encontrado"));

        o.setCliente(c); o.setVeiculo(v); o.setStatus(req.getStatus());
        o.setMecanico(req.getMecanico());
        o.setData(req.getData() != null ? req.getData() : o.getData());
        o.setObservacoes(req.getObservacoes());
        o.getServicos().clear();
        o.getPecas().clear();

        if (req.getServicos() != null) {
            for (ItemServicoRequest s : req.getServicos()) {
                ItemServico is = new ItemServico();
                is.setOrdemServico(o); is.setDescricao(s.getDescricao()); is.setValor(s.getValor());
                o.getServicos().add(is);
            }
        }
        if (req.getPecas() != null) {
            for (ItemPecaRequest p : req.getPecas()) {
                EstoqueItem item = estRepo.findById(p.getEstoqueId())
                    .orElseThrow(() -> new ResourceNotFoundException("Peça não encontrada"));
                ItemPeca ip = new ItemPeca();
                ip.setOrdemServico(o); ip.setEstoqueItem(item); ip.setNomePeca(item.getNome());
                ip.setQuantidade(p.getQuantidade());
                ip.setPrecoUnitario(p.getPrecoUnitario() != null ? p.getPrecoUnitario() : item.getPrecoUnitario());
                o.getPecas().add(ip);
                item.setQuantidade(item.getQuantidade() - p.getQuantidade());
                estRepo.save(item);
            }
        }
        return toResponse(ordemRepo.save(o));
    }

    @Transactional
    public void deletar(Long id) {
        OrdemServico o = ordemRepo.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("OS não encontrada: " + id));
        for (ItemPeca p : o.getPecas()) {
            if (p.getEstoqueItem() != null) {
                p.getEstoqueItem().setQuantidade(p.getEstoqueItem().getQuantidade() + p.getQuantidade());
                estRepo.save(p.getEstoqueItem());
            }
        }
        ordemRepo.delete(o);
    }

    public DashboardResponse getDashboard() {
        List<OrdemServico> todas = ordemRepo.findAll();
        List<OrdemServico> concluidas = todas.stream()
            .filter(o -> o.getStatus() == OrdemServico.StatusOS.CONCLUIDO).collect(Collectors.toList());
        BigDecimal fat = BigDecimal.ZERO;
        for (OrdemServico o : concluidas) fat = fat.add(o.getTotal());
        BigDecimal ticket = concluidas.isEmpty() ? BigDecimal.ZERO
            : fat.divide(BigDecimal.valueOf(concluidas.size()), 2, RoundingMode.HALF_UP);
        DashboardResponse r = new DashboardResponse();
        r.setOrdensAbertas(ordemRepo.countByStatus(OrdemServico.StatusOS.ANDAMENTO)
                         + ordemRepo.countByStatus(OrdemServico.StatusOS.AGUARDANDO));
        r.setOrdensConcluidas(concluidas.size());
        r.setTotalClientes(cliRepo.count());
        r.setFaturamentoConcluido(fat);
        r.setTicketMedio(ticket);
        return r;
    }

    private OrdemResponse toResponse(OrdemServico o) {
        OrdemResponse r = new OrdemResponse();
        r.setId(o.getId());
        r.setClienteId(o.getCliente().getId());
        r.setClienteNome(o.getCliente().getNome());
        r.setClienteEmail(o.getCliente().getEmail());
        r.setVeiculoId(o.getVeiculo().getId());
        r.setVeiculoDesc(o.getVeiculo().getMarca()+" "+o.getVeiculo().getModelo()+" "+o.getVeiculo().getAno());
        r.setVeiculoPlaca(o.getVeiculo().getPlaca());
        r.setStatus(o.getStatus().name());
        r.setMecanico(o.getMecanico());
        r.setData(o.getData());
        r.setObservacoes(o.getObservacoes());
        List<ItemServicoResponse> svcs = new ArrayList<>();
        for (ItemServico s : o.getServicos()) {
            ItemServicoResponse sr = new ItemServicoResponse();
            sr.setId(s.getId()); sr.setDescricao(s.getDescricao()); sr.setValor(s.getValor());
            svcs.add(sr);
        }
        List<ItemPecaResponse> pecs = new ArrayList<>();
        for (ItemPeca p : o.getPecas()) {
            ItemPecaResponse pr = new ItemPecaResponse();
            pr.setId(p.getId());
            pr.setEstoqueId(p.getEstoqueItem() != null ? p.getEstoqueItem().getId() : null);
            pr.setNomePeca(p.getNomePeca()); pr.setQuantidade(p.getQuantidade());
            pr.setPrecoUnitario(p.getPrecoUnitario());
            pr.setSubtotal(p.getPrecoUnitario().multiply(BigDecimal.valueOf(p.getQuantidade())));
            pecs.add(pr);
        }
        r.setServicos(svcs); r.setPecas(pecs);
        r.setTotalServicos(o.getTotalServicos()); r.setTotalPecas(o.getTotalPecas()); r.setTotal(o.getTotal());
        return r;
    }

    private void validarEstoque(List<ItemPecaRequest> pecas) {
        if (pecas == null) return;
        for (ItemPecaRequest p : pecas) {
            EstoqueItem item = estRepo.findById(p.getEstoqueId())
                .orElseThrow(() -> new ResourceNotFoundException("Peça não encontrada: " + p.getEstoqueId()));
            if (item.getQuantidade() < p.getQuantidade())
                throw new BusinessException("Estoque insuficiente: " + item.getNome()
                    + " (disponível: " + item.getQuantidade() + " un.)");
        }
    }
}
