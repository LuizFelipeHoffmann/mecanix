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

    private static final Long EMPRESA_ID = 1L;

    private final OrdemServicoRepository repo = mock(OrdemServicoRepository.class);
    private final OrdemServicoService service = new OrdemServicoService(repo,
        mock(ClienteRepository.class), mock(VeiculoRepository.class), mock(EstoqueRepository.class),
        mock(EmpresaRepository.class));

    private OrdemServico os(OrdemServico.StatusOS status) {
        OrdemServico o = new OrdemServico();
        o.setId(1L); o.setStatus(status);
        o.setCliente(new Cliente()); o.setVeiculo(new Veiculo());
        when(repo.findByIdAndEmpresaId(1L, EMPRESA_ID)).thenReturn(Optional.of(o));
        when(repo.save(o)).thenReturn(o);
        return o;
    }

    @Test
    void baixaRegistraDataEForma() {
        OrdemServico o = os(OrdemServico.StatusOS.CONCLUIDO);
        service.darBaixaPagamento(1L, "PIX", EMPRESA_ID);
        assertEquals(LocalDate.now(), o.getDataPagamento());
        assertEquals("PIX", o.getFormaPagamento());
    }

    @Test
    void estornoVoltaParaPendente() {
        OrdemServico o = os(OrdemServico.StatusOS.CONCLUIDO);
        service.darBaixaPagamento(1L, "DINHEIRO", EMPRESA_ID);
        service.estornarPagamento(1L, EMPRESA_ID);
        assertNull(o.getDataPagamento());
        assertNull(o.getFormaPagamento());
    }

    @Test
    void recusaOsCanceladaEFormaInvalida() {
        os(OrdemServico.StatusOS.CANCELADO);
        assertThrows(BusinessException.class, () -> service.darBaixaPagamento(1L, "PIX", EMPRESA_ID));
        os(OrdemServico.StatusOS.CONCLUIDO);
        assertThrows(BusinessException.class, () -> service.darBaixaPagamento(1L, "CHEQUE", EMPRESA_ID));
        assertThrows(BusinessException.class, () -> service.darBaixaPagamento(1L, null, EMPRESA_ID));
    }
}
