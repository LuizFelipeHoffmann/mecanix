package com.mecanix.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class LoginRequest {
    @NotBlank(message = "E-mail é obrigatório")
    @Email(message = "E-mail inválido")
    private String email;

    @NotBlank(message = "Senha é obrigatória")
    private String senha;

    public String getEmail() { return email; }
    public void setEmail(String e) { this.email = e; }
    public String getSenha() { return senha; }
    public void setSenha(String s) { this.senha = s; }
}
