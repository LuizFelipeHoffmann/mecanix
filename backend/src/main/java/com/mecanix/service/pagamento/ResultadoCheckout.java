package com.mecanix.service.pagamento;

public class ResultadoCheckout {
    private final String checkoutUrl;
    private final String referenciaExterna; // id da assinatura/preference no gateway
    private final boolean sandbox; // true quando ainda não há credenciais reais configuradas

    public ResultadoCheckout(String checkoutUrl, String referenciaExterna, boolean sandbox) {
        this.checkoutUrl = checkoutUrl;
        this.referenciaExterna = referenciaExterna;
        this.sandbox = sandbox;
    }
    public String getCheckoutUrl() { return checkoutUrl; }
    public String getReferenciaExterna() { return referenciaExterna; }
    public boolean isSandbox() { return sandbox; }
}
