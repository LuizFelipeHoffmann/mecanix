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

    @NotBlank(message = "CNPJ ou CPF é obrigatório")
    private String cnpj;

    // "CNPJ" ou "CPF" — o formato exigido do campo acima depende deste valor
    // (validado em AuthService, pois o formato correto não é fixo).
    @NotBlank(message = "Tipo de documento é obrigatório")
    @Pattern(regexp = "CNPJ|CPF", message = "Tipo de documento deve ser CNPJ ou CPF")
    private String tipoDocumento = "CNPJ";

    // "GRATIS" (padrão) | "MENSAL" | "ANUAL" — ver PlanoService.
    private String planoTipo = "GRATIS";

    // sem validação de formato: o cadastro não deve travar por telefone inválido
    private String telefoneEmpresa;

    @Pattern(regexp = "(\\d{5}-?\\d{3})?", message = "CEP inválido")
    private String cepEmpresa;

    private String ruaEmpresa;
    private String numeroEmpresa;
    private String bairroEmpresa;
    private String cidadeEmpresa;

    @Pattern(regexp = "([A-Za-z]{2})?", message = "UF inválida")
    private String ufEmpresa;

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
    public String getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(String t) { this.tipoDocumento = t; }
    public String getPlanoTipo() { return planoTipo; }
    public void setPlanoTipo(String p) { this.planoTipo = p; }
    public String getTelefoneEmpresa() { return telefoneEmpresa; }
    public void setTelefoneEmpresa(String t) { this.telefoneEmpresa = t; }
    public String getCepEmpresa() { return cepEmpresa; }
    public void setCepEmpresa(String c) { this.cepEmpresa = c; }
    public String getRuaEmpresa() { return ruaEmpresa; }
    public void setRuaEmpresa(String r) { this.ruaEmpresa = r; }
    public String getNumeroEmpresa() { return numeroEmpresa; }
    public void setNumeroEmpresa(String n) { this.numeroEmpresa = n; }
    public String getBairroEmpresa() { return bairroEmpresa; }
    public void setBairroEmpresa(String b) { this.bairroEmpresa = b; }
    public String getCidadeEmpresa() { return cidadeEmpresa; }
    public void setCidadeEmpresa(String c) { this.cidadeEmpresa = c; }
    public String getUfEmpresa() { return ufEmpresa; }
    public void setUfEmpresa(String u) { this.ufEmpresa = u; }
    public boolean isAceiteTermos() { return aceiteTermos; }
    public void setAceiteTermos(boolean a) { this.aceiteTermos = a; }
    public String getNomeUsuario() { return nomeUsuario; }
    public void setNomeUsuario(String n) { this.nomeUsuario = n; }
    public String getEmailUsuario() { return emailUsuario; }
    public void setEmailUsuario(String e) { this.emailUsuario = e; }
    public String getSenha() { return senha; }
    public void setSenha(String s) { this.senha = s; }
}
