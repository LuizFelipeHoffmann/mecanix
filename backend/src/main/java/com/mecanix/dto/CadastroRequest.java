package com.mecanix.dto;

import jakarta.validation.constraints.*;

// Dados do formulário de "Criar conta": cria a Empresa (oficina que vai usar o
// MECANIX) e o primeiro usuário dela (perfil ADMIN) numa única requisição.
public class CadastroRequest {

    // ── dados da oficina (Empresa) ──
    @NotBlank(message = "Nome fantasia é obrigatório")
    @Size(min = 2, max = 100, message = "Nome fantasia deve ter entre 2 e 100 caracteres")
    private String nomeFantasia;

    @NotBlank(message = "Razão social é obrigatória")
    @Size(min = 2, max = 150, message = "Razão social deve ter entre 2 e 150 caracteres")
    private String razaoSocial;

    @NotBlank(message = "CNPJ é obrigatório")
    @Pattern(regexp = "\\d{2}\\.?\\d{3}\\.?\\d{3}/?\\d{4}-?\\d{2}", message = "CNPJ inválido")
    private String cnpj;

    @Pattern(regexp = "^(\\(\\d{2}\\)\\s?)?(9?\\d{4}[-\\s]?\\d{4})?$", message = "Telefone inválido")
    private String telefoneEmpresa;

    private String enderecoEmpresa;

    @AssertTrue(message = "É necessário aceitar os Termos de Uso e a Política de Privacidade")
    private boolean aceiteTermos;

    // ── dados do usuário administrador ──
    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres")
    private String nomeUsuario;

    @NotBlank(message = "E-mail é obrigatório")
    @Email(message = "E-mail inválido")
    private String emailUsuario;

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres")
    private String senha;

    public String getNomeFantasia() { return nomeFantasia; }
    public void setNomeFantasia(String n) { this.nomeFantasia = n; }
    public String getRazaoSocial() { return razaoSocial; }
    public void setRazaoSocial(String r) { this.razaoSocial = r; }
    public String getCnpj() { return cnpj; }
    public void setCnpj(String c) { this.cnpj = c; }
    public String getTelefoneEmpresa() { return telefoneEmpresa; }
    public void setTelefoneEmpresa(String t) { this.telefoneEmpresa = t; }
    public String getEnderecoEmpresa() { return enderecoEmpresa; }
    public void setEnderecoEmpresa(String e) { this.enderecoEmpresa = e; }
    public boolean isAceiteTermos() { return aceiteTermos; }
    public void setAceiteTermos(boolean a) { this.aceiteTermos = a; }
    public String getNomeUsuario() { return nomeUsuario; }
    public void setNomeUsuario(String n) { this.nomeUsuario = n; }
    public String getEmailUsuario() { return emailUsuario; }
    public void setEmailUsuario(String e) { this.emailUsuario = e; }
    public String getSenha() { return senha; }
    public void setSenha(String s) { this.senha = s; }
}
