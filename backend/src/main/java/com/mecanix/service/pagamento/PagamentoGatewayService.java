package com.mecanix.service.pagamento;

import com.mecanix.dto.PlanoResponse;
import com.mecanix.model.Empresa;

// Contrato comum pros gateways de pagamento (Mercado Pago, Asaas, ...).
// Cada implementação sabe criar uma cobrança recorrente pro plano escolhido
// (inclusive o plano Grátis, que registra a forma de pagamento mas só cobra
// depois do período de teste) e extrair do webhook a referência da
// assinatura/pagamento confirmado.
public interface PagamentoGatewayService {
    String getNome(); // "mercadopago" | "asaas" — usado pra escolher o gateway certo

    // formaPagamento: "CARTAO" ou "PIX" — escolhida pelo cliente na tela de cadastro.
    ResultadoCheckout criarCheckout(Empresa empresa, PlanoResponse plano, String formaPagamento);

    // payload = corpo bruto do webhook daquele gateway. Devolve a referência
    // externa confirmada, ou null se o evento não for de confirmação de pagamento.
    String idConfirmadoNoWebhook(String payloadJson);
}
