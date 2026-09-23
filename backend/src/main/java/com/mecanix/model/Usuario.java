package com.mecanix.model;
import jakarta.persistence.*;
@Entity @Table(name="usuarios")
public class Usuario {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false,length=100) private String nome;
    @Column(nullable=false,unique=true,length=100) private String email;
    @Column(nullable=false) private String senha;
    @Column(length=60) private String cargo;
    @Enumerated(EnumType.STRING) @Column(nullable=false,length=20) private Perfil perfil;
    public enum Perfil { ADMIN, SERVICOS, ESTOQUE }
    public Long getId(){return id;} public String getNome(){return nome;}
    public String getEmail(){return email;} public String getSenha(){return senha;}
    public String getCargo(){return cargo;} public Perfil getPerfil(){return perfil;}
    public void setId(Long id){this.id=id;} public void setNome(String n){this.nome=n;}
    public void setEmail(String e){this.email=e;} public void setSenha(String s){this.senha=s;}
    public void setCargo(String c){this.cargo=c;} public void setPerfil(Perfil p){this.perfil=p;}
}
