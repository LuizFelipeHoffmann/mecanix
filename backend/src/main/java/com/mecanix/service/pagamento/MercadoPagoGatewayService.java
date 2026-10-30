package com.mecanix.service.pagamento;

import com.mecanix.dto.PlanoResponse;
import com.mecanix.model.Empresa;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

// Integração com a API de Assinaturas (preapproval) do Mercado Pago.
// Docs: https://www.mercadopago.com.br/developers/pt/docs/subscriptions
// Endpoint: POST https://api.mercadopago.com/preapproval
// Auth: header "Authorization: Bearer {access_token}"
//
// Enquanto MERCADOPAGO_ACCESS_TOKEN não for configurado (variável de ambiente
// ou application-dev.properties), este serviço NÃO chama a API real — devolve
// um checkout "sandbox" pra não travar o cadastro de ninguém. Assim que a
// chave real (de teste ou produção) for configurada, a integração passa a
// criar a assinatura de verdade automaticamente, sem precisar mudar código.
// Confira os nomes exatos dos campos do corpo na doc oficial antes de ligar
// em produção — o shape abaixo é o formato padrão da API de preapproval.
//
// Este serviço só é chamado pros planos MENSAL e ANUAL — o plano GRATIS não
// cria cobrança recorrente nenhuma aqui (ver AssinaturaService), pra não
// correr risco de cobrar ninguém sozinho ao fim do período de teste.
// Plano Anual (cicloMeses = 12): cobrança recorrente a cada 12 meses.
//
// IMPORTANTE (produção): o número do cartão NUNCA deve passar pelo nosso
// backend — o certo é tokenizar no navegador com o SDK/Checkout Bricks do
// Mercado Pago (chave pública) e mandar só o token aqui. Esta implementação
// (sandbox, sem chave real) não recebe nem armazena dado de cartão completo.
@Service
public class MercadoPagoGatewayService implements PagamentoGatewayService {
    private static final Logger log = LoggerFactory.getLogger(MercadoPagoGatewayService.class);
    private static final String PREAPPROVAL_URL = "https://api.mercadopago.com/preapproval";

    @Value("${mercadopago.access-token:}")
    private String accessToken;

    @Value("${mecanix.app.url:http://localhost:5173}")
    private String appUrl;

    private final RestTemplate http = new RestTemplate();

    @Override
    public String getNome() { return "mercadopago"; }

    @Override
    public ResultadoCheckout criarCheckout(Empresa empresa, PlanoResponse plano, String formaPagamento) {
        if (accessToken == null || accessToken.isBlank()) {
            log.warn("MERCADOPAGO_ACCESS_TOKEN nao configurado — devolvendo checkout sandbox (nenhuma cobranca real foi criada)");
            return new ResultadoCheckout(appUrl + "/dashboard?checkout=sandbox", "sandbox-" + System.currentTimeMillis(), true);
        }
        if ("PIX".equalsIgnoreCase(formaPagamento)) {
            // A API de preapproval (assinatura recorrente) do Mercado Pago exige
            // cartão — não tem recorrência automática via PIX. Pra PIX, o certo é
            // gerar uma cobrança avulsa (Pix Copia e Cola) a cada ciclo; isso fica
            // fora do escopo deste serviço de assinatura recorrente por enquanto.
            log.warn("Checkout recorrente via PIX ainda nao implementado no gateway Mercado Pago — use CARTAO ou configure o Asaas pra PIX.");
        }
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> autoRecurring = Map.of(
                "frequency", plano.getCicloMeses(),
                "frequency_type", "months",
                "transaction_amount", plano.getPrecoCobranca(),
                "currency_id", "BRL"
            );
            Map<String, Object> body = Map.of(
                "reason", "MECANIX - Plano " + plano.getNome(),
                "external_reference", "empresa-" + empresa.getId(),
                "payer_email", empresa.getEmail(),
                "back_url", appUrl + "/dashboard?checkout=sucesso",
                "auto_recurring", autoRecurring,
                "status", "pending"
            );

            ResponseEntity<Map> resp = http.postForEntity(PREAPPROVAL_URL, new HttpEntity<>(body, headers), Map.class);
            Map respBody = resp.getBody();
            String initPoint = respBody != null ? (String) respBody.get("init_point") : null;
            String id = respBody != null ? String.valueOf(respBody.get("id")) : null;
            return new ResultadoCheckout(initPoint, id, false);
        } catch (Exception e) {
            log.error("Erro ao criar assinatura no Mercado Pago", e);
            throw new RuntimeException("Nao foi possivel iniciar o pagamento com Mercado Pago: " + e.getMessage(), e);
        }
    }

    @Override
    public String idConfirmadoNoWebhook(String payloadJson) {
        // TODO: parsear a notificacao do MP (type=preapproval / payment) e
        // validar a origem antes de confiar no conteudo. Referencia:
        // https://www.mercadopago.com.br/developers/pt/docs/subscriptions
        return null;
    }
}
