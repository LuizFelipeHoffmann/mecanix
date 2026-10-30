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

    // sem validação de formato: o cadastro não deve travar por telefone inválido
    private String telefone;

    @Pattern(regexp = "(\\d{5}-?\\d{3})?", message = "CEP inválido")
    private String cep;

    private String rua;
    private String numero;
    private String bairro;
    private String cidade;

    @Pattern(regexp = "([A-Za-z]{2})?", message = "UF inválida")
    private String uf;

    public String getNome() { return nome; }
    public void setNome(String n) { this.nome = n; }
    public String getCpf() { return cpf; }
    public void setCpf(String c) { this.cpf = c; }
    public String getEmail() { return email; }
    public void setEmail(String e) { this.email = e; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String t) { this.telefone = t; }
    public String getCep() { return cep; }
    public void setCep(String c) { this.cep = c; }
    public String getRua() { return rua; }
    public void setRua(String r) { this.rua = r; }
    public String getNumero() { return numero; }
    public void setNumero(String n) { this.numero = n; }
    public String getBairro() { return bairro; }
    public void setBairro(String b) { this.bairro = b; }
    public String getCidade() { return cidade; }
    public void setCidade(String c) { this.cidade = c; }
    public String getUf() { return uf; }
    public void setUf(String u) { this.uf = u; }
}
