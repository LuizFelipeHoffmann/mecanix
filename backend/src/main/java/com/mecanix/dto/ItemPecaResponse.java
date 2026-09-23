package com.mecanix.dto;
import java.math.BigDecimal;
public class ItemPecaResponse {
    private Long id,estoqueId; private String nomePeca; private Integer quantidade; private BigDecimal precoUnitario,subtotal;
    public Long getId(){return id;} public void setId(Long id){this.id=id;}
    public Long getEstoqueId(){return estoqueId;} public void setEstoqueId(Long id){this.estoqueId=id;}
    public String getNomePeca(){return nomePeca;} public void setNomePeca(String n){this.nomePeca=n;}
    public Integer getQuantidade(){return quantidade;} public void setQuantidade(Integer q){this.quantidade=q;}
    public BigDecimal getPrecoUnitario(){return precoUnitario;} public void setPrecoUnitario(BigDecimal p){this.precoUnitario=p;}
    public BigDecimal getSubtotal(){return subtotal;} public void setSubtotal(BigDecimal s){this.subtotal=s;}
}
