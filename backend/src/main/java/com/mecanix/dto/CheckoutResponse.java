package com.mecanix.dto;

public class CheckoutResponse {
    private String checkoutUrl;
    private boolean sandbox;
    private String mensagem; // texto de confirmação pra mostrar na tela, mesmo em modo sandbox

    public String getCheckoutUrl() { return checkoutUrl; }
    public void setCheckoutUrl(String u) { this.checkoutUrl = u; }
    public boolean isSandbox() { return sandbox; }
    public void setSandbox(boolean s) { this.sandbox = s; }
    public String getMensagem() { return mensagem; }
    public void setMensagem(String m) { this.mensagem = m; }
}
