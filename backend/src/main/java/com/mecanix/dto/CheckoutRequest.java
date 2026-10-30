package com.mecanix.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class CheckoutRequest {
    @NotBlank(message = "Plano é obrigatório")
    private String plano; // GRATIS | MENSAL | ANUAL

    private String gateway; // "mercadopago" | "asaas" — opcional, usa o padrão se não vier

    @NotBlank(message = "Forma de pagamento é obrigatória")
    @Pattern(regexp = "CARTAO|PIX", message = "Forma de pagamento deve ser CARTAO ou PIX")
    private String formaPagamento;

    // Dados do cartão só pra validar na hora — em produção isso deve ser
    // tokenizado no navegador com o SDK do gateway (chave pública) e nunca
    // chegar aqui como número completo. O backend não persiste número de
    // cartão nem CVV, só guarda os 4 últimos dígitos pra exibição.
    private String cartaoNumero;
    private String cartaoNome;
    private String cartaoValidade;
    private String cartaoCvv;

    public String getPlano() { return plano; }
    public void setPlano(String p) { this.plano = p; }
    public String getGateway() { return gateway; }
    public void setGateway(String g) { this.gateway = g; }
    public String getFormaPagamento() { return formaPagamento; }
    public void setFormaPagamento(String f) { this.formaPagamento = f; }
    public String getCartaoNumero() { return cartaoNumero; }
    public void setCartaoNumero(String c) { this.cartaoNumero = c; }
    public String getCartaoNome() { return cartaoNome; }
    public void setCartaoNome(String c) { this.cartaoNome = c; }
    public String getCartaoValidade() { return cartaoValidade; }
    public void setCartaoValidade(String c) { this.cartaoValidade = c; }
    public String getCartaoCvv() { return cartaoCvv; }
    public void setCartaoCvv(String c) { this.cartaoCvv = c; }
}
