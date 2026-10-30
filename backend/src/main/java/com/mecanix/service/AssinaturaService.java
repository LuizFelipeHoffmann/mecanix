package com.mecanix.service;

import com.mecanix.dto.CheckoutResponse;
import com.mecanix.dto.PlanoResponse;
import com.mecanix.exception.BusinessException;
import com.mecanix.model.*;
import com.mecanix.repository.AssinaturaRepository;
import com.mecanix.repository.EmpresaRepository;
import com.mecanix.service.pagamento.PagamentoGatewayService;
import com.mecanix.service.pagamento.ResultadoCheckout;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class AssinaturaService {
    private final AssinaturaRepository assinaturaRepo;
    private final EmpresaRepository empresaRepo;
    private final PlanoService planoService;

    public AssinaturaService(AssinaturaRepository assinaturaRepo, EmpresaRepository empresaRepo, PlanoService planoService) {
        this.assinaturaRepo = assinaturaRepo;
        this.empresaRepo = empresaRepo;
        this.planoService = planoService;
    }

    @Transactional
    public CheckoutResponse criarCheckout(Long empresaId, PlanoTipo plano, String gatewayNome, String formaPagamento, String cartaoFinal) {
        Empresa empresa = empresaRepo.findById(empresaId)
            .orElseThrow(() -> new BusinessException("Empresa não encontrada"));
        PlanoResponse planoInfo = planoService.buscar(plano);
        if (planoInfo == null) throw new BusinessException("Plano inválido");

        Assinatura a = new Assinatura();
        a.setEmpresa(empresa);
        a.setPlanoTipo(plano);
        a.setValor(planoInfo.getPrecoCobranca());
        a.setFormaPagamento(formaPagamento);
        a.setCartaoFinal(cartaoFinal);

        StatusAssinatura status;
        String checkoutUrl = null;
        boolean sandbox = true;
        String mensagem;

        if (planoInfo.getTrialMeses() > 0) {
            // Plano Grátis: só GUARDA a forma de pagamento (cartão/PIX) pra usar
            // mais tarde — de propósito, NÃO cria nenhuma cobrança recorrente no
            // gateway agora, então não existe risco nenhum de cobrança
            // automática no fim do teste. Quando os 3 meses acabarem, a
            // assinatura só é marcada como PENDENTE no próximo login
            // (AuthService.avisarSeTrialAcabou) — ninguém é cobrado sozinho,
            // só fica sinalizado que precisa confirmar o pagamento.
            status = StatusAssinatura.TRIAL;
            empresa.setTrialFim(LocalDate.now().plusMonths(planoInfo.getTrialMeses()));
            a.setGateway(null);
            a.setReferenciaExterna(null);
            mensagem = "Plano grátis ativado! Sem nenhuma cobrança até " +
                empresa.getTrialFim().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) +
                ". Perto do fim do período a gente avisa por aqui pra você confirmar o pagamento — nada é cobrado sozinho.";
        } else {
            PagamentoGatewayService gateway = planoService.gateway(gatewayNome);
            ResultadoCheckout resultado = gateway.criarCheckout(empresa, planoInfo, formaPagamento);
            // Sem chave de gateway configurada (sandbox): considera ATIVA direto,
            // pra não travar o cadastro de ninguém num projeto sem chave real.
            // Com gateway de verdade: PENDENTE até o webhook confirmar o pagamento.
            status = resultado.isSandbox() ? StatusAssinatura.ATIVA : StatusAssinatura.PENDENTE;
            a.setGateway(gateway.getNome());
            a.setReferenciaExterna(resultado.getReferenciaExterna());
            checkoutUrl = resultado.getCheckoutUrl();
            sandbox = resultado.isSandbox();
            mensagem = "Assinatura " + planoInfo.getNome().toLowerCase() + " confirmada no " + meioPagamento(formaPagamento, cartaoFinal) + "."
                + (sandbox ? " (modo sandbox — nenhuma cobrança real foi feita, só a conta foi liberada)" : "");
        }

        a.setStatus(status);
        assinaturaRepo.save(a);

        empresa.setPlanoTipo(plano);
        empresa.setStatusAssinatura(status);
        empresaRepo.save(empresa);

        CheckoutResponse resp = new CheckoutResponse();
        resp.setCheckoutUrl(checkoutUrl);
        resp.setSandbox(sandbox);
        resp.setMensagem(mensagem);
        return resp;
    }

    private String meioPagamento(String formaPagamento, String cartaoFinal) {
        return "PIX".equals(formaPagamento) ? "PIX" : "cartão" + (cartaoFinal != null ? " final " + cartaoFinal : "");
    }
}
