package com.mecanix.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

// Cria um novo acesso (conta de funcionário) dentro da MESMA empresa do
// administrador logado — enxerga os mesmos dados (clientes, OS, estoque),
// mas só com o nível de acesso escolhido. Nunca cria outro ADMIN por aqui:
// é por isso que existe uma rota separada da de cadastro de empresa. Como
// cada funcionário tem seu próprio login/sessão, duas pessoas (ex: um do
// Atendimento e um do Estoque, ou até dois do mesmo nível) conseguem usar
// o sistema ao mesmo tempo, cada um na sua sessão.
public class CriarMembroRequest {
    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    @NotBlank(message = "E-mail é obrigatório")
    @Email(message = "E-mail inválido")
    private String email;

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres")
    private String senha;

    private String cargo;

    @NotBlank(message = "Nível de acesso é obrigatório")
    @Pattern(regexp = "SERVICOS|ESTOQUE", message = "Nível de acesso deve ser Atendimento ou Estoque")
    private String perfil;

    public String getNome() { return nome; }
    public void setNome(String n) { this.nome = n; }
    public String getEmail() { return email; }
    public void setEmail(String e) { this.email = e; }
    public String getSenha() { return senha; }
    public void setSenha(String s) { this.senha = s; }
    public String getCargo() { return cargo; }
    public void setCargo(String c) { this.cargo = c; }
    public String getPerfil() { return perfil; }
    public void setPerfil(String p) { this.perfil = p; }
}
