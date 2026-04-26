package com.mecanix.dto;
public class ClienteRequest {
    private String nome,cpf,email,telefone,endereco;
    public String getNome(){return nome;} public void setNome(String n){this.nome=n;}
    public String getCpf(){return cpf;} public void setCpf(String c){this.cpf=c;}
    public String getEmail(){return email;} public void setEmail(String e){this.email=e;}
    public String getTelefone(){return telefone;} public void setTelefone(String t){this.telefone=t;}
    public String getEndereco(){return endereco;} public void setEndereco(String e){this.endereco=e;}
}
