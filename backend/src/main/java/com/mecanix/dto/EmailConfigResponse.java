package com.mecanix.dto;
import com.mecanix.model.Empresa;

// Estado atual da configuração de e-mail da empresa — nunca devolve a senha
// (nem criptografada), só se uma conta própria está configurada ou não.
public class EmailConfigResponse {
    private boolean contaPropriaConfigurada;
    private String email;
    private String nomeRemetente;
    private String remetenteAtual; // "Nome <email>" de quem efetivamente envia hoje (própria ou a compartilhada)

    public static EmailConfigResponse from(Empresa empresa, String remetenteAtual) {
        EmailConfigResponse r = new EmailConfigResponse();
        boolean configurada = empresa.getEmailRemetenteProprio() != null && !empresa.getEmailRemetenteProprio().isBlank()
            && empresa.getEmailSenhaProprio() != null && !empresa.getEmailSenhaProprio().isBlank();
        r.contaPropriaConfigurada = configurada;
        r.email = empresa.getEmailRemetenteProprio();
        r.nomeRemetente = empresa.getEmailNomeRemetente();
        r.remetenteAtual = remetenteAtual;
        return r;
    }

    public boolean isContaPropriaConfigurada() { return contaPropriaConfigurada; }
    public String getEmail() { return email; }
    public String getNomeRemetente() { return nomeRemetente; }
    public String getRemetenteAtual() { return remetenteAtual; }
}
