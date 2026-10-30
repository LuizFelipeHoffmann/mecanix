package com.mecanix.dto;

import jakarta.validation.constraints.NotBlank;

public class SenhaRequest {
    @NotBlank(message = "Informe a senha atual")
    private String senhaAtual;

    @NotBlank(message = "Informe a nova senha")
    private String senhaNova;

    public String getSenhaAtual() { return senhaAtual; }
    public void setSenhaAtual(String s) { this.senhaAtual = s; }
    public String getSenhaNova() { return senhaNova; }
    public void setSenhaNova(String s) { this.senhaNova = s; }
}
