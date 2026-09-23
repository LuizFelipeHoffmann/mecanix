package com.mecanix.service;

import com.mecanix.exception.BusinessException;
import com.mecanix.model.*;
import com.mecanix.repository.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PagamentoTest {

    private final OrdemServicoRepository repo = mock(OrdemServicoRepository.class);
    private final OrdemServicoService service = new OrdemServicoService(repo,
        mock(ClienteRepository.class), mock(VeiculoRepository.class), mock(EstoqueRepository.class));

    private OrdemServico os(OrdemServico.StatusOS status) {
        OrdemServico o = new OrdemServico();
        o.setId(1L); o.setStatus(status);
        o.setCliente(new Cliente()); o.setVeiculo(new Veiculo());
        when(repo.findById(1L)).thenReturn(Optional.of(o));
        when(repo.save(o)).thenReturn(o);
        return o;
    }

    @Test
    void baixaRegistraDataEForma() {
        OrdemServico o = os(OrdemServico.StatusOS.CONCLUIDO);
        service.darBaixaPagamento(1L, "PIX");
        assertEquals(LocalDate.now(), o.getDataPagamento());
        assertEquals("PIX", o.getFormaPagamento());
    }

    @Test
    void estornoVoltaParaPendente() {
        OrdemServico o = os(OrdemServico.StatusOS.CONCLUIDO);
        service.darBaixaPagamento(1L, "DINHEIRO");
        service.estornarPagamento(1L);
        assertNull(o.getDataPagamento());
        assertNull(o.getFormaPagamento());
    }

    @Test
    void recusaOsCanceladaEFormaInvalida() {
        os(OrdemServico.StatusOS.CANCELADO);
        assertThrows(BusinessException.class, () -> service.darBaixaPagamento(1L, "PIX"));
        os(OrdemServico.StatusOS.CONCLUIDO);
        assertThrows(BusinessException.class, () -> service.darBaixaPagamento(1L, "CHEQUE"));
        assertThrows(BusinessException.class, () -> service.darBaixaPagamento(1L, null));
    }
}
