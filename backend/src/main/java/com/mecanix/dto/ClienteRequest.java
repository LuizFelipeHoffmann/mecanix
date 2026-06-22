package com.mecanix.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class ClienteRequest {
    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres")
    private String nome;

    @NotBlank(message = "CPF é obrigatório")
    @Pattern(regexp = "\\d{3}\\.?\\d{3}\\.?\\d{3}-?\\d{2}", message = "CPF inválido")
    private String cpf;

    @Email(message = "E-mail inválido")
    private String email;

    @Pattern(regexp = "^(\\(\\d{2}\\)\\s?)?(9?\\d{4}[-\\s]?\\d{4})?$", message = "Telefone inválido")
    private String telefone;

    private String endereco;

    public String getNome() { return nome; }
    public void setNome(String n) { this.nome = n; }
    public String getCpf() { return cpf; }
    public void setCpf(String c) { this.cpf = c; }
    public String getEmail() { return email; }
    public void setEmail(String e) { this.email = e; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String t) { this.telefone = t; }
    public String getEndereco() { return endereco; }
    public void setEndereco(String e) { this.endereco = e; }
}
