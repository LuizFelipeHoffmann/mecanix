package com.mecanix.model;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Set;
@Entity @Table(name="estoque")
public class EstoqueItem {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false,unique=true,length=20) private String codigo;
    @Column(nullable=false,length=120) private String nome;
    @Column(length=40) private String categoria;
    @Column(nullable=false) private Integer quantidade;
    @Column(name="quantidade_minima",nullable=false) private Integer quantidadeMinima;
    @Column(name="preco_unitario",nullable=false,precision=10,scale=2) private BigDecimal precoUnitario;
    @ElementCollection(fetch=FetchType.EAGER)
    @CollectionTable(name="estoque_tipos",joinColumns=@JoinColumn(name="estoque_id"),
        uniqueConstraints=@UniqueConstraint(columnNames={"estoque_id","tipo"}))
    @Column(name="tipo",length=20) private Set<String> tipos;
    public boolean isAbaixoMinimo(){return quantidade<quantidadeMinima;}
    public Long getId(){return id;} public String getCodigo(){return codigo;}
    public String getNome(){return nome;} public String getCategoria(){return categoria;}
    public Integer getQuantidade(){return quantidade;} public Integer getQuantidadeMinima(){return quantidadeMinima;}
    public BigDecimal getPrecoUnitario(){return precoUnitario;} public Set<String> getTipos(){return tipos;}
    public void setId(Long id){this.id=id;} public void setCodigo(String c){this.codigo=c;}
    public void setNome(String n){this.nome=n;} public void setCategoria(String c){this.categoria=c;}
    public void setQuantidade(Integer q){this.quantidade=q;} public void setQuantidadeMinima(Integer q){this.quantidadeMinima=q;}
    public void setPrecoUnitario(BigDecimal p){this.precoUnitario=p;} public void setTipos(Set<String> t){this.tipos=t;}
}
