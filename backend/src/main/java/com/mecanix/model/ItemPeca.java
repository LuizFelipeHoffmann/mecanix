package com.mecanix.model;
import jakarta.persistence.*;
import java.math.BigDecimal;
@Entity @Table(name="itens_peca")
public class ItemPeca {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="ordem_id",nullable=false) private OrdemServico ordemServico;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="estoque_id") private EstoqueItem estoqueItem;
    @Column(length=120) private String nomePeca;
    @Column(nullable=false) private Integer quantidade;
    @Column(name="preco_unitario",nullable=false,precision=10,scale=2) private BigDecimal precoUnitario;
    public Long getId(){return id;} public OrdemServico getOrdemServico(){return ordemServico;}
    public EstoqueItem getEstoqueItem(){return estoqueItem;} public String getNomePeca(){return nomePeca;}
    public Integer getQuantidade(){return quantidade;} public BigDecimal getPrecoUnitario(){return precoUnitario;}
    public void setId(Long id){this.id=id;} public void setOrdemServico(OrdemServico o){this.ordemServico=o;}
    public void setEstoqueItem(EstoqueItem e){this.estoqueItem=e;} public void setNomePeca(String n){this.nomePeca=n;}
    public void setQuantidade(Integer q){this.quantidade=q;} public void setPrecoUnitario(BigDecimal p){this.precoUnitario=p;}
}
