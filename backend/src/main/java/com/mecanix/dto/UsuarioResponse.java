package com.mecanix.dto;
import com.mecanix.model.Usuario;
public class UsuarioResponse {
    private Long id;
    private String nome,email,cargo,perfil,iniciais;
    public static UsuarioResponse from(Usuario u){
        UsuarioResponse r=new UsuarioResponse();
        r.id=u.getId();r.nome=u.getNome();r.email=u.getEmail();r.cargo=u.getCargo();r.perfil=u.getPerfil().name();
        if(u.getNome()!=null){String[]p=u.getNome().split(" ");r.iniciais=(p[0].substring(0,1)+(p.length>1?p[p.length-1].substring(0,1):"")).toUpperCase();}
        return r;
    }
    public Long getId(){return id;} public String getNome(){return nome;} public String getEmail(){return email;}
    public String getCargo(){return cargo;} public String getPerfil(){return perfil;} public String getIniciais(){return iniciais;}
}
