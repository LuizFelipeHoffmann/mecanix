package com.mecanix.service;

import com.mecanix.dto.PlanoResponse;
import com.mecanix.model.PlanoTipo;
import com.mecanix.service.pagamento.PagamentoGatewayService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

// Catálogo de planos do MECANIX. Os valores abaixo (nome/preço) são
// placeholders — ajuste conforme a precificação que vocês definirem. Os três
// planos dão acesso exatamente aos MESMOS recursos do sistema — não há
// diferença de funcionalidade entre eles, só o ciclo/valor de cobrança:
//   GRATIS  — 3 meses sem cobrança, mas exige cartão ou PIX cadastrado já no
//             ato do cadastro (ver PagamentoGatewayService/AssinaturaService);
//             ao fim dos 3 meses passa a cobrar o valor mensal automaticamente.
//   MENSAL  — cobrança recorrente todo mês.
//   ANUAL   — cobrança recorrente uma vez por ano, por um valor menor do que
//             pagar o mensal 12 vezes (aqui, equivalente a ~2 meses de desconto).
@Service
public class PlanoService {
    private final List<PagamentoGatewayService> gateways;

    @Value("${mecanix.pagamento.gateway-padrao:mercadopago}")
    private String gatewayPadrao;

    public PlanoService(List<PagamentoGatewayService> gateways) { this.gateways = gateways; }

    private static final BigDecimal PRECO_MENSAL = new BigDecimal("49.90");
    private static final BigDecimal PRECO_ANUAL = new BigDecimal("499.00"); // ~R$41,58/mês — mais barato que o mensal

    // Lista de recursos igual pros três planos, de propósito: a oferta é a
    // mesma, só muda como/quando é cobrada.
    private static final List<String> RECURSOS = List.of(
        "Todos os recursos do sistema liberados",
        "Clientes, veículos e ordens de serviço ilimitados",
        "Relatórios e indicadores",
        "Suporte por e-mail"
    );

    private static final Map<PlanoTipo, PlanoResponse> CATALOGO = Map.of(
        PlanoTipo.GRATIS, new PlanoResponse(PlanoTipo.GRATIS, "Grátis por 3 meses",
            PRECO_MENSAL, 1, 3, PRECO_MENSAL, RECURSOS),
        PlanoTipo.MENSAL, new PlanoResponse(PlanoTipo.MENSAL, "Mensal",
            PRECO_MENSAL, 1, 0, PRECO_MENSAL, RECURSOS),
        PlanoTipo.ANUAL, new PlanoResponse(PlanoTipo.ANUAL, "Anual",
            PRECO_ANUAL, 12, 0, PRECO_ANUAL.divide(new BigDecimal("12"), 2, RoundingMode.HALF_UP), RECURSOS)
    );

    public List<PlanoResponse> listar() { return List.copyOf(CATALOGO.values()); }

    public PlanoResponse buscar(PlanoTipo tipo) { return CATALOGO.get(tipo); }

    public PagamentoGatewayService gateway(String nome) {
        String alvo = (nome == null || nome.isBlank()) ? gatewayPadrao : nome;
        return gateways.stream().filter(g -> g.getNome().equalsIgnoreCase(alvo)).findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Gateway de pagamento desconhecido: " + alvo));
    }
}
