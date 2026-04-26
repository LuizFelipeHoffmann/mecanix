package com.mecanix.dto;
import com.mecanix.model.Cliente;
public class ClienteResponse {
    private Long id; private String nome,cpf,email,telefone,endereco; private int totalVeiculos,totalOrdens;
    public static ClienteResponse from(Cliente c){
        ClienteResponse r=new ClienteResponse();
        r.id=c.getId();r.nome=c.getNome();r.cpf=c.getCpf();r.email=c.getEmail();r.telefone=c.getTelefone();r.endereco=c.getEndereco();
        r.totalVeiculos=c.getVeiculos()!=null?c.getVeiculos().size():0;r.totalOrdens=c.getOrdens()!=null?c.getOrdens().size():0;return r;
    }
    public Long getId(){return id;} public String getNome(){return nome;} public String getCpf(){return cpf;}
    public String getEmail(){return email;} public String getTelefone(){return telefone;} public String getEndereco(){return endereco;}
    public int getTotalVeiculos(){return totalVeiculos;} public int getTotalOrdens(){return totalOrdens;}
}
