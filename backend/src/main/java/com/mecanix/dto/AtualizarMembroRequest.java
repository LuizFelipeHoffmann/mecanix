package com.mecanix.dto;

// Edita um acesso de funcionário já existente. Tudo opcional — manda só o
// que foi alterado na tela; senha só é trocada se vier preenchida, e o
// perfil continua restrito a SERVICOS/ESTOQUE (validado em EquipeService).
public class AtualizarMembroRequest {
    private String nome;
    private String cargo;
    private String perfil;
    private String senha;

    public String getNome() { return nome; }
    public void setNome(String n) { this.nome = n; }
    public String getCargo() { return cargo; }
    public void setCargo(String c) { this.cargo = c; }
    public String getPerfil() { return perfil; }
    public void setPerfil(String p) { this.perfil = p; }
    public String getSenha() { return senha; }
    public void setSenha(String s) { this.senha = s; }
}
