package com.mecanix.dto;
import com.mecanix.model.Cliente;
public class ClienteResponse {
    private Long id; private String nome,cpf,email,telefone,cep,rua,numero,bairro,cidade,uf; private int totalVeiculos,totalOrdens;
    public static ClienteResponse from(Cliente c){
        ClienteResponse r=new ClienteResponse();
        r.id=c.getId();r.nome=c.getNome();r.cpf=c.getCpf();r.email=c.getEmail();r.telefone=c.getTelefone();
        r.cep=c.getCep();r.rua=c.getRua();r.numero=c.getNumero();r.bairro=c.getBairro();r.cidade=c.getCidade();r.uf=c.getUf();
        r.totalVeiculos=c.getVeiculos()!=null?c.getVeiculos().size():0;r.totalOrdens=c.getOrdens()!=null?c.getOrdens().size():0;return r;
    }
    public Long getId(){return id;} public String getNome(){return nome;} public String getCpf(){return cpf;}
    public String getEmail(){return email;} public String getTelefone(){return telefone;}
    public String getCep(){return cep;} public String getRua(){return rua;} public String getNumero(){return numero;}
    public String getBairro(){return bairro;} public String getCidade(){return cidade;} public String getUf(){return uf;}
    public int getTotalVeiculos(){return totalVeiculos;} public int getTotalOrdens(){return totalOrdens;}
}
