package com.mecanix.service.pagamento;

import com.mecanix.dto.PlanoResponse;
import com.mecanix.model.Empresa;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

// Integração com a API de Assinaturas do Asaas.
// Docs: https://docs.asaas.com/docs/criando-uma-assinatura
// Fluxo real: 1) POST /v3/customers (cria/recupera o cliente = a oficina)
//             2) POST /v3/subscriptions (cria a cobranca recorrente pro cliente)
// Auth: header "access_token: {api_key}" (nao e Bearer)
// Sandbox: https://api-sandbox.asaas.com/v3 (troque asaas.ambiente=producao pra ligar de verdade)
//
// Igual ao MercadoPagoGatewayService: sem ASAAS_API_KEY configurada, devolve
// um checkout sandbox e nao chama a API real — fica pronto pra ligar assim
// que a chave (sandbox ou producao) for configurada. Confira os nomes exatos
// dos campos na doc oficial antes de ligar em producao.
//
// Este serviço só é chamado pros planos MENSAL e ANUAL — o plano GRATIS não
// cria cobrança recorrente nenhuma aqui (ver AssinaturaService), pra não
// correr risco de cobrar ninguém sozinho ao fim do período de teste.
// Plano Anual (cicloMeses = 12): cycle "YEARLY". Asaas suporta PIX
// nativamente (billingType "PIX"), diferente do Mercado Pago.
@Service
public class AsaasGatewayService implements PagamentoGatewayService {
    private static final Logger log = LoggerFactory.getLogger(AsaasGatewayService.class);

    @Value("${asaas.api-key:}")
    private String apiKey;

    @Value("${asaas.ambiente:sandbox}")
    private String ambiente;

    @Value("${mecanix.app.url:http://localhost:5173}")
    private String appUrl;

    private final RestTemplate http = new RestTemplate();

    private String baseUrl() {
        return "sandbox".equalsIgnoreCase(ambiente) ? "https://api-sandbox.asaas.com/v3" : "https://api.asaas.com/v3";
    }

    @Override
    public String getNome() { return "asaas"; }

    @Override
    public ResultadoCheckout criarCheckout(Empresa empresa, PlanoResponse plano, String formaPagamento) {
        if (apiKey == null || apiKey.isBlank()) {
            log.warn("ASAAS_API_KEY nao configurado — devolvendo checkout sandbox (nenhuma cobranca real foi criada)");
            return new ResultadoCheckout(appUrl + "/dashboard?checkout=sandbox", "sandbox-" + System.currentTimeMillis(), true);
        }
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("access_token", apiKey);
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> clienteBody = Map.of(
                "name", empresa.getRazaoSocial(),
                "email", empresa.getEmail(),
                "cpfCnpj", empresa.getCnpj().replaceAll("\\D", ""),
                "externalReference", "empresa-" + empresa.getId()
            );
            ResponseEntity<Map> clienteResp = http.postForEntity(baseUrl() + "/customers",
                new HttpEntity<>(clienteBody, headers), Map.class);
            String customerId = clienteResp.getBody() != null ? (String) clienteResp.getBody().get("id") : null;

            String billingType = "PIX".equalsIgnoreCase(formaPagamento) ? "PIX" : "CREDIT_CARD";
            String cycle = plano.getCicloMeses() >= 12 ? "YEARLY" : "MONTHLY";
            LocalDate primeiraCobranca = LocalDate.now().plusDays(3); // tempo de compensação

            Map<String, Object> assinaturaBody = Map.of(
                "customer", customerId,
                "billingType", billingType,
                "value", plano.getPrecoCobranca(),
                "nextDueDate", primeiraCobranca.format(DateTimeFormatter.ISO_LOCAL_DATE),
                "cycle", cycle,
                "description", "MECANIX - Plano " + plano.getNome(),
                "externalReference", "empresa-" + empresa.getId()
            );
            ResponseEntity<Map> subResp = http.postForEntity(baseUrl() + "/subscriptions",
                new HttpEntity<>(assinaturaBody, headers), Map.class);
            Map subBody = subResp.getBody();
            String id = subBody != null ? (String) subBody.get("id") : null;
            String checkoutUrl = subBody != null ? (String) subBody.get("invoiceUrl") : null;
            return new ResultadoCheckout(checkoutUrl, id, false);
        } catch (Exception e) {
            log.error("Erro ao criar assinatura no Asaas", e);
            throw new RuntimeException("Nao foi possivel iniciar o pagamento com Asaas: " + e.getMessage(), e);
        }
    }

    @Override
    public String idConfirmadoNoWebhook(String payloadJson) {
        // TODO: parsear o evento do Asaas (PAYMENT_CONFIRMED / PAYMENT_RECEIVED).
        // Docs: https://docs.asaas.com/docs/webhook
        return null;
    }
}
