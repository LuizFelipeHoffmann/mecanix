package com.mecanix.service;

import com.mecanix.dto.ItemPecaRequest;
import com.mecanix.dto.OrdemRequest;
import com.mecanix.exception.BusinessException;
import com.mecanix.model.*;
import com.mecanix.repository.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CancelamentoTest {

    private final OrdemServicoRepository ordemRepo = mock(OrdemServicoRepository.class);
    private final ClienteRepository cliRepo = mock(ClienteRepository.class);
    private final VeiculoRepository veiRepo = mock(VeiculoRepository.class);
    private final EstoqueRepository estRepo = mock(EstoqueRepository.class);
    private final OrdemServicoService service = new OrdemServicoService(ordemRepo, cliRepo, veiRepo, estRepo);

    private final EstoqueItem filtro = new EstoqueItem();

    // OS com 2 filtros já baixados do estoque (8 em estoque, ou seja, 10 antes da OS)
    private OrdemServico os(OrdemServico.StatusOS status) {
        filtro.setId(5L); filtro.setNome("Filtro de óleo"); filtro.setQuantidade(8);
        filtro.setPrecoUnitario(new BigDecimal("28.00"));
        OrdemServico o = new OrdemServico();
        o.setId(1L); o.setStatus(status);
        Cliente c = new Cliente(); c.setId(1L); o.setCliente(c);
        Veiculo v = new Veiculo(); v.setId(1L); o.setVeiculo(v);
        ItemPeca ip = new ItemPeca();
        ip.setOrdemServico(o); ip.setEstoqueItem(filtro); ip.setNomePeca("Filtro de óleo");
        ip.setQuantidade(2); ip.setPrecoUnitario(new BigDecimal("28.00"));
        o.getPecas().add(ip);
        when(ordemRepo.findById(1L)).thenReturn(Optional.of(o));
        when(ordemRepo.save(o)).thenReturn(o);
        when(cliRepo.findById(1L)).thenReturn(Optional.of(c));
        when(veiRepo.findById(1L)).thenReturn(Optional.of(v));
        when(estRepo.findById(5L)).thenReturn(Optional.of(filtro));
        return o;
    }

    private OrdemRequest req(OrdemServico.StatusOS status) {
        ItemPecaRequest p = new ItemPecaRequest();
        p.setEstoqueId(5L); p.setQuantidade(2); p.setPrecoUnitario(new BigDecimal("28.00"));
        OrdemRequest r = new OrdemRequest();
        r.setClienteId(1L); r.setVeiculoId(1L); r.setStatus(status); r.setMecanico("Rafael");
        r.setPecas(List.of(p));
        return r;
    }

    @Test
    void cancelarDevolvePecasAoEstoque() {
        OrdemServico o = os(OrdemServico.StatusOS.ANDAMENTO);
        service.atualizar(1L, req(OrdemServico.StatusOS.CANCELADO));
        assertEquals(10, filtro.getQuantidade());
        assertEquals(OrdemServico.StatusOS.CANCELADO, o.getStatus());
        assertEquals(1, o.getPecas().size(), "a OS cancelada mantém o registro das peças");
    }

    @Test
    void editarOsAtivaMantemBaixa() {
        os(OrdemServico.StatusOS.ANDAMENTO);
        service.atualizar(1L, req(OrdemServico.StatusOS.CONCLUIDO));
        assertEquals(8, filtro.getQuantidade());
    }

    @Test
    void osCanceladaNaoPodeSerReaberta() {
        os(OrdemServico.StatusOS.CANCELADO);
        filtro.setQuantidade(10);
        assertThrows(BusinessException.class, () -> service.atualizar(1L, req(OrdemServico.StatusOS.ANDAMENTO)));
        assertEquals(10, filtro.getQuantidade());
    }

    @Test
    void excluirDevolveSoUmaVez() {
        os(OrdemServico.StatusOS.ANDAMENTO);
        service.deletar(1L);
        assertEquals(10, filtro.getQuantidade());

        os(OrdemServico.StatusOS.CANCELADO);
        filtro.setQuantidade(10);
        service.deletar(1L);
        assertEquals(10, filtro.getQuantidade(), "peças de OS cancelada já tinham voltado ao estoque");
    }
}
