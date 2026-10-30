package com.mecanix.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

// Endpoints públicos (excluídos do SessionInterceptor em WebConfig) — os
// gateways de pagamento chamam isso direto, servidor a servidor, sem sessão
// de usuário.
// TODO antes de produção: validar a assinatura/segredo de cada gateway
// (x-signature do Mercado Pago, header access_token do Asaas) antes de
// confiar no corpo da requisição, e então atualizar o status da Assinatura
// e da Empresa via AssinaturaService.
@RestController @RequestMapping("/api/webhooks")
public class WebhookController {
    private static final Logger log = LoggerFactory.getLogger(WebhookController.class);

    @PostMapping("/mercadopago")
    public Map<String, String> mercadoPago(@RequestBody(required = false) String payload) {
        log.info("Webhook Mercado Pago recebido: {}", payload);
        return Map.of("status", "recebido");
    }

    @PostMapping("/asaas")
    public Map<String, String> asaas(@RequestBody(required = false) String payload) {
        log.info("Webhook Asaas recebido: {}", payload);
        return Map.of("status", "recebido");
    }
}
