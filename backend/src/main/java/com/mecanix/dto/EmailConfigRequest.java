package com.mecanix.dto;

// Configura a conta de e-mail PRÓPRIA da oficina pra enviar as OS (em vez da
// conta compartilhada do MECANIX). Tudo opcional — ver regras em
// EmailConfigService: trocar o e-mail exige senha nova; manter o mesmo
// e-mail permite só atualizar o nome sem reenviar a senha.
public class EmailConfigRequest {
    private String email;
    private String senha;
    private String nomeRemetente;
    private boolean removerCredenciais;

    public String getEmail() { return email; }
    public void setEmail(String e) { this.email = e; }
    public String getSenha() { return senha; }
    public void setSenha(String s) { this.senha = s; }
    public String getNomeRemetente() { return nomeRemetente; }
    public void setNomeRemetente(String n) { this.nomeRemetente = n; }
    public boolean isRemoverCredenciais() { return removerCredenciais; }
    public void setRemoverCredenciais(boolean r) { this.removerCredenciais = r; }
}
