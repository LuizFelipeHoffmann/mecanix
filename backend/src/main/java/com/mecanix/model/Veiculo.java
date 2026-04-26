package com.mecanix.model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.List;
@Entity @Table(name="veiculos")
public class Veiculo {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="cliente_id",nullable=false) private Cliente cliente;
    @Column(nullable=false,length=60) private String marca;
    @Column(nullable=false,length=80) private String modelo;
    @Column(length=4) private String ano;
    @Column(length=40) private String cor;
    @Column(nullable=false,unique=true,length=10) private String placa;
    @Column private Integer quilometragem;
    @Enumerated(EnumType.STRING) @Column(nullable=false,length=20) private TipoVeiculo tipo;
    @Column(length=300) private String observacoes;
    @JsonIgnore @OneToMany(mappedBy="veiculo",cascade=CascadeType.ALL,orphanRemoval=true) private List<OrdemServico> ordens;
    public enum TipoVeiculo { SEDAN, HATCH, SUV, PICKUP, ELETRICO }
    public Long getId(){return id;} public Cliente getCliente(){return cliente;}
    public String getMarca(){return marca;} public String getModelo(){return modelo;}
    public String getAno(){return ano;} public String getCor(){return cor;}
    public String getPlaca(){return placa;} public Integer getQuilometragem(){return quilometragem;}
    public TipoVeiculo getTipo(){return tipo;} public String getObservacoes(){return observacoes;}
    public List<OrdemServico> getOrdens(){return ordens;}
    public void setId(Long id){this.id=id;} public void setCliente(Cliente c){this.cliente=c;}
    public void setMarca(String m){this.marca=m;} public void setModelo(String m){this.modelo=m;}
    public void setAno(String a){this.ano=a;} public void setCor(String c){this.cor=c;}
    public void setPlaca(String p){this.placa=p;} public void setQuilometragem(Integer k){this.quilometragem=k;}
    public void setTipo(TipoVeiculo t){this.tipo=t;} public void setObservacoes(String o){this.observacoes=o;}
}
