package com.mecanix.dto;

// Dados editáveis do perfil do usuário logado. Tudo opcional — manda só o
// que a pessoa alterou na tela; nome em branco é ignorado (não some o nome).
// foto = data-URI base64 da imagem, ou string vazia explícita pra remover a foto atual.
public class PerfilRequest {
    private String nome;
    private String foto;
    private boolean removerFoto;

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getFoto() { return foto; }
    public void setFoto(String foto) { this.foto = foto; }
    public boolean isRemoverFoto() { return removerFoto; }
    public void setRemoverFoto(boolean removerFoto) { this.removerFoto = removerFoto; }
}
